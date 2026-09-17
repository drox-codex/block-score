# Proguard & R8 Rules for BLOCK SCORE by DROX STUDIO

# Optimize and strip dead code aggressively
-allowaccessmodification

# Strip all android.util.Log calls in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve Compose runtime essentials
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}

# Keep the main activity entry point
-keep class com.example.MainActivity {
    <init>(...);
}

# Keep ViewModel constructor for Android ViewModelProvider factory
-keepclassmembers class com.example.viewmodel.GameViewModel {
    <init>(android.app.Application);
}

# Obfuscate internal game mechanics, state, and models
# (classes not explicitly kept will have their names obfuscated by R8)

# Obfuscate file names in stack traces
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
