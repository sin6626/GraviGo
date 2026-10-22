param([Parameter(Mandatory = $true)][string]$BrowserExecutable, [ValidateSet('zh','en')][string]$Language = 'zh')
$ErrorActionPreference = 'Stop'
if (-not (Test-Path -LiteralPath $BrowserExecutable -PathType Leaf)) { throw 'Chrome Headless Shell was not found.' }
$root = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot '../../../..'))
$work = Join-Path $root '.verification/gravigo-film-v3'
$delivery = Join-Path $work $(if ($Language -eq 'en') { 'versions/v2-en' } else { 'versions/v2' })
$composition = if ($Language -eq 'en') { 'GraviGo-FullFilm-EN' } else { 'GraviGo-FullFilm-V2' }
$filename = if ($Language -eq 'en') { 'GraviGo-v2-en-1080x1920-60fps.mp4' } else { 'GraviGo-v2-1080x1920-60fps.mp4' }
$log = Join-Path $work ('render-v2-' + $Language + '.log')
New-Item -ItemType Directory -Force -Path $delivery,(Join-Path $work 'tmp') | Out-Null
$env:TEMP = Join-Path $work 'tmp'
$env:TMP = $env:TEMP
$env:TMPDIR = $env:TEMP
$env:npm_config_cache = Join-Path $root '.verification/gravigo-promo-remotion-v2/npm-cache'
Push-Location $PSScriptRoot
try {
    & ./node_modules/.bin/remotion.cmd render src/index.ts $composition (Join-Path $delivery $filename) `
        --codec=h264 --crf=17 --pixel-format=yuv420p --color-space=bt709 --audio-codec=aac --audio-bitrate=192k `
        --jpeg-quality=95 --concurrency=4 ('--browser-executable=' + $BrowserExecutable) > $log 2>&1
    if ($LASTEXITCODE -ne 0) { Get-Content $log -Tail 30; throw 'Full film export failed.' }
    Write-Output 'Full film export completed.'
} finally { Pop-Location }
