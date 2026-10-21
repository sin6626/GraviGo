param([Parameter(Mandatory = $true)][string]$BrowserExecutable)

$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $BrowserExecutable -PathType Leaf)) { throw 'Browser executable was not found.' }
$projectRoot = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../../..'))
$work = Join-Path $projectRoot '.verification/gravigo-promo-remotion-v2'
$delivery = Join-Path $work 'delivery'
New-Item -ItemType Directory -Force -Path $delivery,(Join-Path $work 'tmp') | Out-Null
$env:TEMP = Join-Path $work 'tmp'
$env:TMP = $env:TEMP
$env:TMPDIR = $env:TEMP
$env:npm_config_cache = Join-Path $work 'npm-cache'
$clips = @(
    @{ Id = 'A-PalmRelay'; Name = 'GraviGo-A-PalmRelay-1080x1920.mp4' },
    @{ Id = 'B-WeightlessOrbit'; Name = 'GraviGo-B-WeightlessOrbit-1080x1920.mp4' },
    @{ Id = 'C-StopWaiting'; Name = 'GraviGo-C-StopWaiting-1080x1920.mp4' }
)
Push-Location $PSScriptRoot
try {
    foreach ($clip in $clips) {
        $log = Join-Path $work ($clip.Id + '-render.log')
        Write-Output ('Rendering ' + $clip.Id)
        & ./node_modules/.bin/remotion.cmd render src/index.ts $clip.Id (Join-Path $delivery $clip.Name) `
            --codec=h264 --crf=18 --pixel-format=yuv420p --color-space=bt709 --audio-codec=aac --audio-bitrate=192k `
            --jpeg-quality=95 --concurrency=2 ('--browser-executable=' + $BrowserExecutable) > $log 2>&1
        if ($LASTEXITCODE -ne 0) {
            Get-Content -LiteralPath $log -Tail 25
            throw ('Render failed: ' + $clip.Id)
        }
        Write-Output ('Completed ' + $clip.Name)
    }
} finally { Pop-Location }
