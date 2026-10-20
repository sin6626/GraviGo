[CmdletBinding()]
param([string]$TestRoot)

$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.IO.Compression
Add-Type -AssemblyName System.IO.Compression.FileSystem
if (-not $TestRoot) {
    $TestRoot = Join-Path $PSScriptRoot ('..\..\.verification\gateway-installer-' + [guid]::NewGuid().ToString('N'))
}
$TestRoot = [IO.Path]::GetFullPath($TestRoot)
New-Item -ItemType Directory -Path $TestRoot -Force | Out-Null
$fixture = Join-Path $TestRoot 'fixture.zip'
$zip = [IO.Compression.ZipFile]::Open($fixture, [IO.Compression.ZipArchiveMode]::Create)
try {
    foreach ($name in @('gravigo-gateway.exe', 'README.md', 'LICENSE', 'NOTICE.md', 'THIRD_PARTY_NOTICES.md', 'SOURCE.txt', 'licenses/test.txt')) {
        $writer = [IO.StreamWriter]::new($zip.CreateEntry($name).Open())
        try { $writer.Write('fixture: ' + $name) } finally { $writer.Dispose() }
    }
}
finally { $zip.Dispose() }
$global:GraviGoInstallerTest = @{
    fixtureHash = (Get-FileHash -LiteralPath $fixture -Algorithm SHA256).Hash
    mode = 'valid'; downloads = 0; fixture = $fixture
    asset = 'GraviGo-Gateway-1.0.0-windows-amd64.zip'
    url = 'https://github.com/sin6626/GraviGo/releases/download/v1.0.0/'
}

# Network mocks keep failure checks deterministic and independent of GitHub availability.
function Invoke-RestMethod {
    param($Uri, $TimeoutSec)
    $state = $global:GraviGoInstallerTest
    if ($Uri -like '*/releases/latest') {
        return @{ tag_name = 'v1.0.0'; draft = $false; prerelease = $false; assets = @(
            @{ name = $state.asset; browser_download_url = $state.url + $state.asset },
            @{ name = 'SHA256SUMS.txt'; browser_download_url = $state.url + 'SHA256SUMS.txt' }
        ) }
    }
    if ($state.mode -eq 'missing') { return '' }
    if ($state.mode -eq 'corrupt') { return ('0' * 64) + '  ' + $state.asset }
    return $state.fixtureHash + '  ' + $state.asset
}
function Invoke-WebRequest {
    param([switch]$UseBasicParsing, $Uri, $OutFile, $TimeoutSec)
    $global:GraviGoInstallerTest.downloads++
    Copy-Item -LiteralPath $global:GraviGoInstallerTest.fixture -Destination $OutFile
}
function Assert-Failure([string]$Mode, [string]$Path, [string]$Expected) {
    $global:GraviGoInstallerTest.mode = $Mode
    try { & "$PSScriptRoot\install-gateway.ps1" -InstallDir $Path }
    catch {
        if ($_.Exception.Message -notlike "*$Expected*") { throw }
        return
    }
    throw "Expected installer failure: $Expected"
}

$install = Join-Path $TestRoot "install with spaces and ' quote"
& "$PSScriptRoot\install-gateway.ps1" -InstallDir $install
$exe = Join-Path $install '1.0.0\gravigo-gateway.exe'
if (-not (Test-Path -LiteralPath $exe)) { throw 'First installation failed.' }
$before = (Get-Item -LiteralPath $exe).LastWriteTimeUtc
& "$PSScriptRoot\install-gateway.ps1" -InstallDir $install
if ((Get-Item -LiteralPath $exe).LastWriteTimeUtc -ne $before) { throw 'Repeat install replaced an existing file.' }

Assert-Failure 'corrupt' (Join-Path $TestRoot 'corrupt') 'SHA-256 mismatch'
if (Test-Path -LiteralPath (Join-Path $TestRoot 'corrupt\1.0.0')) { throw 'Corrupt ZIP was extracted.' }
$count = $global:GraviGoInstallerTest.downloads
Assert-Failure 'missing' (Join-Path $TestRoot 'missing') 'Missing or ambiguous'
if ($global:GraviGoInstallerTest.downloads -ne $count) { throw 'Missing checksum did not stop download.' }
[IO.File]::WriteAllText($exe, 'existing user file')
Assert-Failure 'valid' $install 'Existing installation differs'
if ([IO.File]::ReadAllText($exe) -ne 'existing user file') { throw 'Existing file was overwritten.' }

$zip = [IO.Compression.ZipFile]::Open($fixture, [IO.Compression.ZipArchiveMode]::Update)
try { $null = $zip.CreateEntry('../escape.txt') } finally { $zip.Dispose() }
$global:GraviGoInstallerTest.fixtureHash = (Get-FileHash -LiteralPath $fixture -Algorithm SHA256).Hash
Assert-Failure 'valid' (Join-Path $TestRoot 'unsafe') 'Unsafe archive entry'
if (Test-Path -LiteralPath (Join-Path $TestRoot 'unsafe\1.0.0')) { throw 'Unsafe ZIP was extracted.' }
if (Get-ChildItem -LiteralPath $TestRoot -Filter '.download-*.zip' -Recurse) { throw 'Download files were not cleaned up.' }
Write-Host 'PASS: install, repeat, checksum rejection, missing checksum, overwrite protection, ZIP traversal, download cleanup.'
Remove-Variable -Name GraviGoInstallerTest -Scope Global
