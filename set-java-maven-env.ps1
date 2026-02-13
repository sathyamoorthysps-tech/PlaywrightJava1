# Set JAVA_HOME and MAVEN_HOME and add them to PATH (system-wide).
# Run PowerShell as Administrator for system variables, or run as normal user for user variables.

param(
    [switch]$UserOnly  # If set, set variables for current user only (no admin needed)
)

$scope = if ($UserOnly) { "User" } else { "Machine" }
if ($scope -eq "Machine" -and -NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    Write-Host "For system-wide (Machine) variables, run PowerShell as Administrator." -ForegroundColor Yellow
    Write-Host "Or run with -UserOnly for current user only." -ForegroundColor Yellow
    $scope = "User"
}

# --- Edit these if you want fixed paths; otherwise script will try to auto-detect ---
$javaHome = $null
$mavenHome = $null

# Auto-detect JDK under C:\Program Files
if (-not $javaHome -or -not (Test-Path $javaHome)) {
    $javaCandidates = @(
        "C:\Program Files\Eclipse Adoptium\jdk-*-hotspot",
        "C:\Program Files\Microsoft\jdk-*",
        "C:\Program Files\Java\jdk-*",
        "C:\Program Files\Amazon Corretto\jdk*"
    )
    foreach ($pattern in $javaCandidates) {
        $found = Get-Item $pattern -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($found) { $javaHome = $found.FullName; break }
    }
}
if (-not $javaHome) { $javaHome = "C:\Program Files\Eclipse Adoptium\jdk-17.0.13.11-hotspot" }

# Auto-detect Maven under C:\Program Files
if (-not $mavenHome -or -not (Test-Path $mavenHome)) {
    $mavenCandidates = @(
        "C:\Program Files\Apache\Maven\apache-maven-*",
        "C:\Program Files\Maven\apache-maven-*"
    )
    foreach ($pattern in $mavenCandidates) {
        $found = Get-Item $pattern -ErrorAction SilentlyContinue | Sort-Object Name -Descending | Select-Object -First 1
        if ($found) { $mavenHome = $found.FullName; break }
    }
}
if (-not $mavenHome) { $mavenHome = "C:\Program Files\Apache\Maven\apache-maven-3.9.9" }

if (-not (Test-Path $javaHome)) {
    Write-Host "JAVA_HOME path not found: $javaHome" -ForegroundColor Red
    Write-Host "Edit this script and set `$javaHome to your JDK install path (e.g. C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot)" -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path $mavenHome)) {
    Write-Host "MAVEN_HOME path not found: $mavenHome" -ForegroundColor Red
    Write-Host "Edit this script and set `$mavenHome to your Maven folder (e.g. C:\Program Files\Apache\Maven\apache-maven-3.9.9)" -ForegroundColor Yellow
    exit 1
}

[Environment]::SetEnvironmentVariable("JAVA_HOME", $javaHome, $scope)
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, $scope)

$currentPath = [Environment]::GetEnvironmentVariable("Path", $scope)
$addPaths = @(
    "%JAVA_HOME%\bin",
    "%MAVEN_HOME%\bin"
)
$pathParts = $currentPath -split ";" | Where-Object { $_ -and $_ -notmatch "^\s*$" }
foreach ($p in $addPaths) {
    if ($pathParts -notcontains $p) {
        $pathParts += $p
    }
}
[Environment]::SetEnvironmentVariable("Path", ($pathParts -join ";"), $scope)

Write-Host "Environment variables set ($scope scope):" -ForegroundColor Green
Write-Host "  JAVA_HOME = $javaHome"
Write-Host "  MAVEN_HOME = $mavenHome"
Write-Host "  Path updated with %JAVA_HOME%\bin and %MAVEN_HOME%\bin"
Write-Host ""
Write-Host "Close and reopen your terminal (or IDE), then run: java -version  and  mvn -version" -ForegroundColor Cyan
