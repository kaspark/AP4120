# Build and start the tea register in Docker: Postgres, API, and UI.
# Usage: .\scripts\deploy.ps1
$ErrorActionPreference = "Stop"
$Root = Split-Path -Parent $PSScriptRoot
$SecretDir = Join-Path $Root ".docker-secrets"
$GradleDest = Join-Path $SecretDir "gradle.properties"
$NpmrcDest = Join-Path $SecretDir "npmrc"

function Fail-MissingToken {
    Write-Host @"
The images need a GitHub classic token with read:packages to download the Helex libraries.

Create one:
  https://github.com/settings/tokens/new?description=ITE4120+Helex+packages+read-only&scopes=read:packages

Then either save it the way the README describes
  (%USERPROFILE%\.gradle\gradle.properties and npm config),
or set `$env:GITHUB_ACTOR and `$env:GITHUB_TOKEN in this terminal and run this script again.
"@
    exit 1
}

function Write-Utf8([string]$Path, [string]$Text) {
    $utf8 = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($Path, $Text, $utf8)
}

function Read-Prop([string]$Path, [string]$Name) {
    foreach ($line in [System.IO.File]::ReadAllLines($Path)) {
        if ($line -match "^$Name=(.*)$") { return $Matches[1].Trim() }
    }
    return $null
}

New-Item -ItemType Directory -Force -Path $SecretDir | Out-Null

$userGradle = Join-Path $env:USERPROFILE ".gradle\gradle.properties"
$userNpmrc = Join-Path $env:USERPROFILE ".npmrc"
$actor = $null
$token = $null

if (Test-Path $userGradle) {
    $actor = Read-Prop $userGradle "gpr.user"
    $token = Read-Prop $userGradle "gpr.key"
}
if (-not $actor) { $actor = $env:GITHUB_ACTOR }
if (-not $token) { $token = $env:GITHUB_TOKEN }
if (-not $actor -or -not $token) { Fail-MissingToken }

Write-Utf8 $GradleDest "gpr.user=$actor`ngpr.key=$token`n"

if ((Test-Path $userNpmrc) -and (Select-String -Path $userNpmrc -Pattern "npm\.pkg\.github\.com/:_authToken=\S+" -Quiet)) {
    Copy-Item $userNpmrc $NpmrcDest -Force
} else {
    Write-Utf8 $NpmrcDest "//npm.pkg.github.com/:_authToken=$token`n"
}

docker compose -f "$Root/docker/compose.app.yaml" up -d --build

Write-Host ""
Write-Host "Application   http://localhost:18640"
Write-Host "Swagger UI    http://localhost:18440/swagger-ui.html"
Write-Host "PostgreSQL    localhost:18520, database tea"
