# Requires Windows PowerShell 5.1 or PowerShell 7 on Windows x64.
[CmdletBinding()]
param(
    [string]$InstallDir = (Join-Path $env:LOCALAPPDATA 'GraviGo\Gateway')
)

& {
    $ErrorActionPreference = 'Stop'
    $ProgressPreference = 'SilentlyContinue'
    if ($env:OS -ne 'Windows_NT' -or
        ($env:PROCESSOR_ARCHITECTURE -ne 'AMD64' -and $env:PROCESSOR_ARCHITEW6432 -ne 'AMD64')) {
        throw 'This installer supports Windows x64 only.'
    }

    $release = Invoke-RestMethod -Uri 'https://api.github.com/repos/sin6626/GraviGo/releases/latest' -TimeoutSec 60
    if ($release.draft -or $release.prerelease -or $release.tag_name -notmatch '^v([0-9]+\.[0-9]+\.[0-9]+)$') {
        throw 'The latest stable release has an unsupported version.'
    }
    $version = $Matches[1]
    $assetName = "GraviGo-Gateway-$version-windows-amd64.zip"
    $baseUrl = "https://github.com/sin6626/GraviGo/releases/download/v$version/"
    foreach ($name in @($assetName, 'SHA256SUMS.txt')) {
        $assets = @($release.assets | Where-Object { $_.name -ceq $name })
        if ($assets.Count -ne 1 -or $assets[0].browser_download_url -cne ($baseUrl + $name)) {
            throw "Missing or unexpected release asset: $name"
        }
    }

    $checksums = Invoke-RestMethod -Uri ($baseUrl + 'SHA256SUMS.txt') -TimeoutSec 60
    $lines = @(([string]$checksums -split '\r?\n') | Where-Object {
        $_ -cmatch ('^([0-9a-fA-F]{64})\s+\*?' + [regex]::Escape($assetName) + '$')
    })
    if ($lines.Count -ne 1) { throw 'Missing or ambiguous gateway SHA-256 checksum.' }
    $expectedHash = ($lines[0] -split '\s+')[0]

    $root = [IO.Path]::GetFullPath($InstallDir)
    $destination = Join-Path $root $version
    $archivePath = Join-Path $root ('.download-' + [guid]::NewGuid().ToString('N') + '.zip')
    New-Item -ItemType Directory -Path $root -Force | Out-Null
    Write-Host "Downloading GraviGo Gateway $version..."
    try {
        Invoke-WebRequest -UseBasicParsing -Uri ($baseUrl + $assetName) -OutFile $archivePath -TimeoutSec 180
        if ((Get-FileHash -LiteralPath $archivePath -Algorithm SHA256).Hash -ine $expectedHash) {
            throw 'Gateway SHA-256 mismatch. Nothing was installed.'
        }

        Add-Type -AssemblyName System.IO.Compression
        Add-Type -AssemblyName System.IO.Compression.FileSystem
        $archive = [IO.Compression.ZipFile]::OpenRead($archivePath)
        try {
            $prefix = $destination.TrimEnd('\') + '\'
            foreach ($entry in $archive.Entries) {
                $target = [IO.Path]::GetFullPath((Join-Path $destination $entry.FullName))
                if ([IO.Path]::IsPathRooted($entry.FullName) -or $entry.FullName.Contains(':') -or
                    -not $target.StartsWith($prefix, [StringComparison]::OrdinalIgnoreCase)) {
                    throw "Unsafe archive entry: $($entry.FullName)"
                }
            }
            foreach ($required in @('gravigo-gateway.exe', 'README.md', 'LICENSE', 'NOTICE.md', 'THIRD_PARTY_NOTICES.md', 'SOURCE.txt')) {
                if (-not $archive.GetEntry($required)) { throw "Incomplete gateway archive: $required" }
            }
            if (-not @($archive.Entries | Where-Object { $_.FullName.StartsWith('licenses/') }).Count) {
                throw 'The gateway archive is missing dependency licenses.'
            }

            if (Test-Path -LiteralPath $destination) {
                # Compare with the verified ZIP so repeat installs never overwrite a running executable.
                $sha = [Security.Cryptography.SHA256]::Create()
                try {
                    foreach ($entry in $archive.Entries) {
                        if (-not $entry.Name) { continue }
                        $path = Join-Path $destination $entry.FullName
                        if (-not (Test-Path -LiteralPath $path -PathType Leaf)) {
                            throw "Existing installation is incomplete. Choose another -InstallDir: $destination"
                        }
                        $stream = $entry.Open()
                        try { $hash = [BitConverter]::ToString($sha.ComputeHash($stream)).Replace('-', '') }
                        finally { $stream.Dispose() }
                        if ((Get-FileHash -LiteralPath $path -Algorithm SHA256).Hash -cne $hash) {
                            throw "Existing installation differs. Choose another -InstallDir: $destination"
                        }
                    }
                }
                finally { $sha.Dispose() }
                Write-Host 'Existing installation verified; no files replaced.'
            }
            else {
                [IO.Compression.ZipFile]::ExtractToDirectory($archivePath, $destination)
            }
        }
        finally { $archive.Dispose() }
    }
    finally {
        if (Test-Path -LiteralPath $archivePath -PathType Leaf) {
            Remove-Item -LiteralPath $archivePath
        }
    }

    $quotedDirectory = $destination.Replace("'", "''")
    Write-Host "Installed GraviGo Gateway $version in $destination"
    Write-Host "Open Antigravity, then run:"
    Write-Host "Set-Location -LiteralPath '$quotedDirectory'"
    Write-Host '.\gravigo-gateway.exe run -port 58900'
    Write-Host 'In another terminal in that folder: .\gravigo-gateway.exe pair -port 58900'
    Write-Host 'Keep the gateway terminal open while using the app. Existing pairing data is preserved.'
}
