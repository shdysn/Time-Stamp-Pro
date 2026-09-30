# Project specific ProGuard rules

# Preserve Room database entities, DAOs, and Database classes
-keep class com.example.database.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# Preserve Data Models
-keep class com.example.data.model.** { *; }

# CameraX
-dontwarn androidx.camera.**
-keep class androidx.camera.** { *; }

# Jetpack Compose
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
