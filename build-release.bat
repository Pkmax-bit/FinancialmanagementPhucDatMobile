@echo off
echo ========================================
echo   Building Release APK
echo ========================================
echo.

REM Check if gradlew exists
if not exist "gradlew.bat" (
    echo ERROR: gradlew.bat not found!
    echo Please run this script from the project root directory.
    pause
    exit /b 1
)

echo [1/3] Cleaning previous builds...
call gradlew.bat clean
if errorlevel 1 (
    echo ERROR: Clean failed!
    pause
    exit /b 1
)

echo.
echo [2/3] Building Release APK...
call gradlew.bat assembleRelease
if errorlevel 1 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3/3] Checking APK location...
if exist "app\build\outputs\apk\release\app-release.apk" (
    echo.
    echo ========================================
    echo   BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo APK Location:
    echo %CD%\app\build\outputs\apk\release\app-release.apk
    echo.
    echo Do you want to open the folder? (Y/N)
    set /p openFolder=
    if /i "%openFolder%"=="Y" (
        explorer "app\build\outputs\apk\release"
    )
) else (
    echo ERROR: APK not found at expected location!
    pause
    exit /b 1
)

echo.
pause



