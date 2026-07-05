#!/bin/bash

# --- Color Definitions for Premium Visual Output ---
RED='\033[0;31m'
GREEN='\033[0;32m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
GOLD='\033[1;33m'
RESET='\033[0m'

clear
echo -e "${GOLD}====================================================${RESET}"
echo -e "${GOLD}          Saitama Training APK Build Master         ${RESET}"
echo -e "${GOLD}====================================================${RESET}"
echo -e "This script helps you build and package a production-ready"
echo -e "or testing-ready APK for the Saitama Training Android app."
echo ""

# Ensure we are in the project root
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Step 1: Detect Java and Gradle Environment
echo -e "${BLUE}[1/4] Checking Environment...${RESET}"
if ! command -v java &> /dev/null; then
    echo -e "${RED}Error: Java is not installed or not in your PATH.${RESET}"
    echo -e "Please install JDK 11 or higher to build this project."
    exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -n 1)
echo -e "  - Java found: ${CYAN}${JAVA_VERSION}${RESET}"

# Use system gradle or local wrapper if available
GRADLE_CMD="gradle"
if [ -f "./gradlew" ]; then
    GRADLE_CMD="./gradlew"
    chmod +x ./gradlew
fi
echo -e "  - Build Engine: ${CYAN}${GRADLE_CMD}${RESET}"

# Step 2: Choose Build Variant
echo ""
echo -e "${BLUE}[2/4] Choose Build Type:${RESET}"
echo -e "  ${GOLD}1)${RESET} ${GREEN}Debug APK${RESET} (Recommended for quick phone testing & sideloading. Installs instantly.)"
echo -e "  ${GOLD}2)${RESET} ${CYAN}Production Release APK${RESET} (Optimized, minified, signed, and ready for app stores.)"
read -p "Enter choice (1 or 2, default is 1): " BUILD_CHOICE

if [ "$BUILD_CHOICE" == "2" ]; then
    BUILD_VARIANT="Release"
    GRADLE_TASK="assembleRelease"
else
    BUILD_VARIANT="Debug"
    GRADLE_TASK="assembleDebug"
fi

echo -e "Selected variant: ${GOLD}${BUILD_VARIANT}${RESET}"

# Step 3: Keystore & Signing Setup (Only for Release Variant)
if [ "$BUILD_VARIANT" == "Release" ]; then
    echo ""
    echo -e "${BLUE}[3/4] Checking Signing Configuration...${RESET}"
    KEYSTORE_FILE="my-upload-key.jks"
    
    # If the user has a custom keystore path or env, use those, else setup default
    if [ ! -f "$KEYSTORE_FILE" ] && [ -z "$STORE_PASSWORD" ]; then
        echo -e "${GOLD}No existing release keystore found.${RESET}"
        echo -e "Would you like to automatically generate a self-signed upload keystore"
        echo -e "(${CYAN}my-upload-key.jks${RESET}) for this production build?"
        read -p "Generate new keystore? (y/n, default is y): " GEN_KEY
        
        if [ "$GEN_KEY" != "n" ] && [ "$GEN_KEY" != "N" ]; then
            echo -e "Generating production upload key..."
            
            # Use safe standard default passwords if none entered, ensuring success
            read -p "Enter Keystore Store Password (default: saitama123): " USER_STORE_PASS
            USER_STORE_PASS=${USER_STORE_PASS:-saitama123}
            
            read -p "Enter Key Alias Password (default: saitama123): " USER_KEY_PASS
            USER_KEY_PASS=${USER_KEY_PASS:-saitama123}
            
            # Generate keystore using keytool
            keytool -genkey -v -keystore "$KEYSTORE_FILE" \
                -alias upload -keyalg RSA -keysize 2048 -validity 10000 \
                -storepass "$USER_STORE_PASS" -keypass "$USER_KEY_PASS" \
                -dname "CN=Saitama Hero, OU=Development, O=Association, L=CityZ, S=Saitama, C=JP" \
                &> /dev/null
                
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}Successfully generated: ${KEYSTORE_FILE}${RESET}"
                export KEYSTORE_PATH="$SCRIPT_DIR/$KEYSTORE_FILE"
                export STORE_PASSWORD="$USER_STORE_PASS"
                export KEY_PASSWORD="$USER_KEY_PASS"
                
                # Persist to local .env if possible so future builds remember it
                echo "KEYSTORE_PATH=$KEYSTORE_PATH" >> .env
                echo "STORE_PASSWORD=$STORE_PASSWORD" >> .env
                echo "KEY_PASSWORD=$KEY_PASSWORD" >> .env
            else
                echo -e "${RED}Failed to generate keystore automatically.${RESET}"
                echo -e "We will attempt an unsigned or debug fallback build."
            fi
        else
            echo -e "Proceeding with environment credentials..."
        fi
    fi
else
    echo ""
    echo -e "${BLUE}[3/4] Skipping Release Signing (using default debug key)...${RESET}"
fi

# Step 4: Execute Gradle Build
echo ""
echo -e "${BLUE}[4/4] Starting Gradle Compilation Task...${RESET}"
echo -e "Running: ${GOLD}${GRADLE_CMD} clean ${GRADLE_TASK}${RESET}"
echo ""

# Execute build
$GRADLE_CMD clean $GRADLE_TASK

# Check build outcome
if [ $? -eq 0 ]; then
    echo ""
    echo -e "${GREEN}====================================================${RESET}"
    echo -e "${GREEN}          BUILD COMPLETED SUCCESSFULLY!             ${RESET}"
    echo -e "${GREEN}====================================================${RESET}"
    echo ""
    
    # Locate generated APK
    OUTPUT_DIR="build-outputs"
    mkdir -p "$OUTPUT_DIR"
    
    if [ "$BUILD_VARIANT" == "Release" ]; then
        APK_SRC="app/build/outputs/apk/release/app-release.apk"
        APK_DEST="$OUTPUT_DIR/SaitamaTraining-Release.apk"
    else
        APK_SRC="app/build/outputs/apk/debug/app-debug.apk"
        APK_DEST="$OUTPUT_DIR/SaitamaTraining-Debug.apk"
    fi
    
    if [ -f "$APK_SRC" ]; then
        cp "$APK_SRC" "$APK_DEST"
        echo -e "Your optimized APK is ready at: ${GOLD}${APK_DEST}${RESET}"
        echo -e "File size: ${CYAN}$(du -sh "$APK_DEST" | cut -f1)${RESET}"
        echo ""
        echo -e "${GREEN}HOW TO INSTALL ON YOUR PHONE:${RESET}"
        echo -e "  1. Transfer the ${GOLD}$(basename "$APK_DEST")${RESET} file to your phone."
        echo -e "     (via Google Drive, USB, Email, or Local Share)"
        echo -e "  2. Open your phone's File Manager and locate the file."
        echo -e "  3. Tap the file to install. If prompted, allow installations from"
        echo -e "     'Unknown Sources' or your File Manager app."
        echo -e "  4. Open 'Saitama Training' and unleash your hero potential!"
    else
        echo -e "${RED}Error: Build succeeded but the APK file could not be found at:${RESET}"
        echo -e "  $APK_SRC"
    fi
else
    echo ""
    echo -e "${RED}====================================================${RESET}"
    echo -e "${RED}                 BUILD FAILED                       ${RESET}"
    echo -e "${RED}====================================================${RESET}"
    echo -e "Review the logs above to identify and fix compilation errors."
fi
