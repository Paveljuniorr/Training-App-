@echo off
:: --- Saitama Training APK Build Master for Windows ---
title Saitama Training APK Build Master

cls
echo ====================================================
echo           Saitama Training APK Build Master         
echo ====================================================
echo This script helps you build and package a production-ready
echo or testing-ready APK for the Saitama Training Android app.
echo.

:: Step 1: Detect Java and Gradle Environment
echo [1/4] Checking Environment...
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo.
    echo ERROR: Java is not installed or not in your PATH.
    echo Please install JDK 11 or higher to build this project.
    pause
    exit /b 1
)
for /f "tokens=*" %%i in ('java -version 2^>^&1') do (
    echo   - Java found: %%i
    goto :java_checked
)
:java_checked

set GRADLE_CMD=gradle
if exist "gradlew.bat" (
    set GRADLE_CMD=gradlew.bat
)
echo   - Build Engine: %GRADLE_CMD%
echo.

:: Step 2: Choose Build Variant
echo [2/4] Choose Build Type:
echo   1] Debug APK (Recommended for quick phone testing / sideloading. Installs instantly.)
echo   2] Production Release APK (Optimized, minified, signed, and ready for app stores.)
set /p BUILD_CHOICE="Enter choice (1 or 2, default is 1): "

set BUILD_VARIANT=Debug
set GRADLE_TASK=assembleDebug
if "%BUILD_CHOICE%"=="2" (
    set BUILD_VARIANT=Release
    set GRADLE_TASK=assembleRelease
)

echo Selected variant: %BUILD_VARIANT%
echo.

:: Step 3: Keystore & Signing Setup (Only for Release Variant)
if "%BUILD_VARIANT%"=="Release" (
    echo [3/4] Checking Signing Configuration...
    set KEYSTORE_FILE=my-upload-key.jks
    
    if not exist "%KEYSTORE_FILE%" (
        echo No existing release keystore found.
        set /p GEN_KEY="Generate a self-signed upload keystore (my-upload-key.jks)? [Y/N, default Y]: "
        if /i not "%GEN_KEY%"=="n" (
            echo Generating production upload key...
            set /p USER_STORE_PASS="Enter Keystore Store Password (default: saitama123): "
            if "%USER_STORE_PASS%"=="" set USER_STORE_PASS=saitama123
            
            set /p USER_KEY_PASS="Enter Key Alias Password (default: saitama123): "
            if "%USER_KEY_PASS%"=="" set USER_KEY_PASS=saitama123
            
            keytool -genkey -v -keystore %KEYSTORE_FILE% -alias upload -keyalg RSA -keysize 2048 -validity 10000 -storepass !USER_STORE_PASS! -keypass !USER_KEY_PASS! -dname "CN=Saitama Hero, OU=Development, O=Association, L=CityZ, S=Saitama, C=JP" >nul 2>nul
            
            if exist "%KEYSTORE_FILE%" (
                echo Successfully generated: %KEYSTORE_FILE%
                set KEYSTORE_PATH=%CD%\%KEYSTORE_FILE%
                set STORE_PASSWORD=!USER_STORE_PASS!
                set KEY_PASSWORD=!USER_KEY_PASS!
            ) else (
                echo Failed to generate keystore automatically.
                echo We will attempt an unsigned or debug fallback build.
            )
        )
    )
) else (
    echo [3/4] Skipping Release Signing (using default debug key)...
)
echo.

:: Step 4: Execute Gradle Build
echo [4/4] Starting Gradle Compilation Task...
echo Running: %GRADLE_CMD% clean %GRADLE_TASK%
echo.

call %GRADLE_CMD% clean %GRADLE_TASK%

if %errorlevel% equ 0 (
    echo.
    echo ====================================================
    echo           BUILD COMPLETED SUCCESSFULLY!             
    echo ====================================================
    echo.
    
    if not exist "build-outputs" mkdir build-outputs
    
    if "%BUILD_VARIANT%"=="Release" (
        copy app\build\outputs\apk\release\app-release.apk build-outputs\SaitamaTraining-Release.apk >nul
        echo Your optimized APK is ready at: build-outputs\SaitamaTraining-Release.apk
    ) else (
        copy app\build\outputs\apk\debug\app-debug.apk build-outputs\SaitamaTraining-Debug.apk >nul
        echo Your test APK is ready at: build-outputs\SaitamaTraining-Debug.apk
    )
    echo.
    echo HOW TO INSTALL ON YOUR PHONE:
    echo   1. Transfer the APK file to your phone.
    echo      (via Google Drive, USB, Email, or Local Share)
    echo   2. Open your phone's File Manager and locate the file.
    echo   3. Tap the file to install. If prompted, allow installations from
    echo      'Unknown Sources' or your File Manager app.
    echo   4. Open 'Saitama Training' and unleash your hero potential!
) else (
    echo.
    echo ====================================================
    echo                 BUILD FAILED                       
    echo ====================================================
    echo Review the logs above to identify and fix compilation errors.
)
pause
