# Gson - used by Room TypeConverters for SetEntry serialization
-keepattributes Signature
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
-keep class com.google.gson.stream.** { *; }

# Keep SetEntry for Gson serialization
-keep class com.gowtham.gym.SetEntry { *; }

# Keep Room entities
-keep class com.gowtham.gym.*Entity { *; }

# Prevent R8 from stripping data class fields used by Gson reflection
-keepclassmembers class com.gowtham.gym.SetEntry {
    <fields>;
    <init>(...);
}
