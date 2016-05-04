-dontwarn java.lang.invoke.*
-keepattributes Signature

-keepattributes *Annotation*
-keep class trikita.*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

-dontwarn com.fasterxml.jackson.**
-dontwarn sun.reflect.*
-dontwarn javax.annotation.**
-dontwarn javax.ws.rs.**

