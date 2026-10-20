$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path (Split-Path $PSScriptRoot -Parent) -Parent
$executable = Join-Path $projectRoot 'bin\gravigo-emulator-gateway.exe'
$legacyExecutable = Join-Path $projectRoot 'bin\mgy-management.exe'
$legacyData = Join-Path $projectRoot 'android\design\.verification\management-gateway'
$dataDirectory = if (Test-Path -LiteralPath (Join-Path $legacyData 'auth_store.json')) {
    $legacyData
} else { Join-Path $projectRoot '.local\emulator-gateway' }
$authStore = Join-Path $dataDirectory 'auth_store.json'
$healthUrl = 'http://127.0.0.1:58901/healthz'

$listener = Get-NetTCPConnection -LocalPort 58901 -State Listen -ErrorAction SilentlyContinue
if ($listener) {
    foreach ($ownerId in ($listener.OwningProcess | Select-Object -Unique)) {
        $owner = Get-Process -Id $ownerId
        if ($owner.Path -notin @($executable, $legacyExecutable)) {
            throw "Port 58901 belongs to another process (PID $ownerId); nothing was stopped."
        }
    }
    $health = Invoke-RestMethod -Uri $healthUrl -TimeoutSec 5
    if ($health.status -ne 'ok') { throw 'The existing gateway failed its health check.' }
    $ready = $false
    try { $ready = (Invoke-RestMethod -Uri 'http://127.0.0.1:58901/readyz' -TimeoutSec 3).status -eq 'ready' } catch { }
    [pscustomobject]@{ Port = 58901; PID = $listener[0].OwningProcess; Started = $false; Health = 'ok'; DesktopReady = $ready; EmulatorUrl = 'http://10.0.2.2:58901' }
    return
}

$logDirectory = Join-Path $projectRoot '.verification\emulator-gateway'
New-Item -ItemType Directory -Force -Path $logDirectory | Out-Null
New-Item -ItemType Directory -Force -Path (Split-Path $executable), $dataDirectory, (Join-Path $logDirectory 'go-tmp') | Out-Null
$logPrefix = Join-Path $logDirectory (Get-Date -Format 'yyyyMMdd-HHmmss-fff')
$overrides = @{
    MULTIGRAVITY_DATA_DIR = $dataDirectory
    AUTH_STORE_PATH = $authStore
    CF_TUNNEL_ENABLED = 'false'
    MULTIGRAVITY_AUTH_STORE_PATH = $null
    MULTIGRAVITY_ADMIN_TOKEN = $null
    MULTIGRAVITY_AUTH_DISABLED = '0'
    NOTIFICATION_ENABLE = '0'
    FCM_ENABLE = '0'
    BARK_ENABLE = '0'
    GOCACHE = (Join-Path $projectRoot '.go-cache')
    GOMODCACHE = (Join-Path $projectRoot '.go-mod')
    GOTMPDIR = (Join-Path $logDirectory 'go-tmp')
    GOTOOLCHAIN = 'go1.26.9'
    CGO_ENABLED = '0'
}
$previous = @{}
try {
    foreach ($name in $overrides.Keys) {
        $previous[$name] = [Environment]::GetEnvironmentVariable($name, 'Process')
        [Environment]::SetEnvironmentVariable($name, $overrides[$name], 'Process')
    }
    Push-Location $projectRoot
    try {
        & go build -trimpath '-ldflags=-s -w -X main.Version=1.0.0' -o $executable ./cmd/gateway
        if ($LASTEXITCODE -ne 0) { throw 'Gateway build failed; no gateway was started.' }
    } finally { Pop-Location }
    $gateway = Start-Process -FilePath $executable -ArgumentList 'run', '-host', '127.0.0.1', '-port', '58901', '-qr=false' `
        -WorkingDirectory $projectRoot -WindowStyle Hidden -PassThru `
        -RedirectStandardOutput "$logPrefix.stdout.log" -RedirectStandardError "$logPrefix.stderr.log"
}
finally {
    foreach ($name in $previous.Keys) {
        [Environment]::SetEnvironmentVariable($name, $previous[$name], 'Process')
    }
}

for ($attempt = 0; $attempt -lt 30; $attempt++) {
    $gateway.Refresh()
    if ($gateway.HasExited) { throw "Gateway exited. Check $logPrefix.stderr.log" }
    try {
        $health = Invoke-RestMethod -Uri $healthUrl -TimeoutSec 1
        if ($health.status -eq 'ok') {
            $ready = $false
            try { $ready = (Invoke-RestMethod -Uri 'http://127.0.0.1:58901/readyz' -TimeoutSec 3).status -eq 'ready' } catch { }
            [pscustomobject]@{ Port = 58901; PID = $gateway.Id; Started = $true; Health = 'ok'; DesktopReady = $ready; EmulatorUrl = 'http://10.0.2.2:58901'; Logs = $logDirectory }
            return
        }
    }
    catch { $health = $null }
    Start-Sleep -Milliseconds 250
}
throw "Gateway PID $($gateway.Id) started but health is not ready. Check $logPrefix.stderr.log"
