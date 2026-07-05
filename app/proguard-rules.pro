# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- Production ProGuard Rules for Saitama Training App ---

# Retain Room database classes and interfaces
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.RoomDatabase$Callback
-dontwarn androidx.room.paging.**

# Retain models and network data classes from serialization issues
-keep class com.example.data.** { *; }

# Retain Retrofit and OkHttp annotations/classes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Retain Moshi elements (including generated JSON adapters)
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
-keep class *JsonAdapter { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}

