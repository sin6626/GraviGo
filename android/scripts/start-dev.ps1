param([string]$Device = 'emulator-5554')

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$sdk = $env:ANDROID_HOME
if (!$sdk) { $sdk = $env:ANDROID_SDK_ROOT }
if (!$sdk) {
    $properties = Join-Path $projectRoot 'android\local.properties'
    if (Test-Path -LiteralPath $properties) {
        $sdkLine = Get-Content -LiteralPath $properties | Where-Object { $_ -match '^sdk.dir=' } | Select-Object -First 1
        if ($sdkLine) { $sdk = $sdkLine.Substring(8).Replace('\:', ':').Replace('\\', '\') }
    }
}
$adb = if ($sdk) { Join-Path $sdk 'platform-tools\adb.exe' } else { $null }
if (!$adb -or !(Test-Path -LiteralPath $adb)) { throw 'Set ANDROID_HOME or android/local.properties to your Android SDK.' }
$state = & $adb -s $Device get-state 2>$null
if ($LASTEXITCODE -ne 0 -or $state -ne 'device') { throw "Start the Android emulator first. Device $Device is not online." }

& (Join-Path $PSScriptRoot 'start-emulator-gateway.ps1')
$previousJava = $env:JAVA_HOME
$previousGradle = $env:GRADLE_USER_HOME
try {
    $studioJdk = Join-Path (Split-Path $sdk) 'Android Studio\jbr'
    if (!$env:JAVA_HOME -and (Test-Path -LiteralPath (Join-Path $studioJdk 'bin\java.exe'))) { $env:JAVA_HOME = $studioJdk }
    if (!$env:GRADLE_USER_HOME -and (Test-Path -LiteralPath (Join-Path $projectRoot '.gradle-user'))) {
        $env:GRADLE_USER_HOME = Join-Path $projectRoot '.gradle-user'
    }
    & (Join-Path $projectRoot 'android\gradlew.bat') -p (Join-Path $projectRoot 'android') --no-daemon :app:assembleDebug --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Android build failed; the running gateway and pairing were preserved.' }
} finally {
    $env:JAVA_HOME = $previousJava
    $env:GRADLE_USER_HOME = $previousGradle
}
$apk = Join-Path $projectRoot 'android\app\build\outputs\apk\debug\app-debug.apk'
$aapt = Join-Path $sdk 'build-tools\36.0.0\aapt.exe'
$badging = & $aapt dump badging $apk
if ($LASTEXITCODE -ne 0) { throw 'Cannot inspect the built APK.' }
$packageLine = $badging | Where-Object { $_ -match "^package: name='([^']+)'" } | Select-Object -First 1
$activityLine = $badging | Where-Object { $_ -match "^launchable-activity: name='([^']+)'" } | Select-Object -First 1
if (!$packageLine -or !$activityLine) { throw 'The APK has no package or launcher activity.' }
$package = [regex]::Match($packageLine, "name='([^']+)'").Groups[1].Value
$activity = [regex]::Match($activityLine, "name='([^']+)'").Groups[1].Value
& $adb -s $Device install -r $apk
if ($LASTEXITCODE -ne 0) { throw 'APK installation failed; app storage was not cleared.' }
& $adb -s $Device shell am start -W -n "$package/$activity"
if ($LASTEXITCODE -ne 0) { throw 'APK launcher failed.' }
Write-Host "Development app: $package/$activity"
Write-Host 'Emulator gateway: http://10.0.2.2:58901 (existing pairing is preserved)'
