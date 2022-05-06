# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/sumesh/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

############################################################################
################################ HOCKEY APP ################################
############################################################################
-keep public class javax.net.ssl.**
-keepclassmembers public class javax.net.ssl.** {
  *;
}

-keep public class org.apache.http.**
-keepclassmembers public class org.apache.http.** {
  *;
}

-keepclassmembers class net.hockeyapp.android.UpdateFragment {
  *;
}

############################################################################
################################ LEAK CANARY ###############################
############################################################################
-keep class org.eclipse.mat.** { *; }
-keep class com.squareup.leakcanary.** { *; }

############################################################################
################################## STETHO #################################
############################################################################
-keep class com.facebook.stetho.** { *; }

############################################################################
######## Retrolambda - https://github.com/evant/gradle-retrolambda #########
############################################################################
-dontwarn java.lang.invoke.*
-dontwarn **$$Lambda$*

############################################################################
######## Picasso - https://github.com/square/picasso #######################
############################################################################
-dontwarn com.squareup.okhttp.**

############################################################################
########### Bouncy Castle - https://github.com/bcgit/bc-java ###############
############################################################################
-dontwarn javax.naming.**

############################################################################
######## Instabug - https://github.com/Instabug/Instabug-Android ###########
############################################################################
-dontwarn com.instabug.**
-dontwarn org.jacoco.agent.rt.**

############################################################################
################################### MISC ###################################
############################################################################
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

############################################################################
################################## RAYGUN ##################################
############################################################################
-keep class com.raygun.raygun4android.** { *; }
-keepattributes Exceptions, Signature, InnerClasses, SourceFile, LineNumberTable
-renamesourcefileattribute SourceFile

############################################################
######################   Log   #############################
############################################################

-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}


############################################################################
################################# Swagger ##################################
############################################################################

-keep class io.swagger.annotations.** { *; }
-dontwarn io.swagger.annotations.**

############################################################################
############################ Kotlin Coroutine ##############################
############################################################################
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

############################################################################
############################ Material Design ###############################
############################################################################
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

############################################################################
############################ Joda Time ###############################
############################################################################
-keep class org.joda.convert.** { *; }
-dontwarn org.joda.convert.**



############################################################################
############################ TEST TEST TEST ###############################
############################################################################
-keep class retrofit2.** { *; }
-keep class okio.** { *; }
-keep class okhttp3.** { *; }
-keep class io.reactivex.** { *; }
-keep class org.reactivestreams.** { *; }
-keep class kotlin.Metadata { *; }
-keep @org.parceler.Parcel class * { *; }
-keep class **$$Parcelable { *; }
-keep interface org.parceler.Parcel
-keep public class * extends java.lang.Exception
-keep @com.squareup.moshi.JsonQualifier interface *
-keep interface kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoader
-keep class kotlin.reflect.jvm.internal.impl.builtins.BuiltInsLoaderImpl
-keep class kotlin.reflect.jvm.internal.impl.serialization.deserialization.builtins.BuiltInsLoaderImpl
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-dontwarn okio.**
-dontwarn javax.annotation.**
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}

-keepclassmembers class * {
    @com.squareup.moshi.FromJson <methods>;
    @com.squareup.moshi.ToJson <methods>;
}

-keep @com.squareup.moshi.JsonQualifier interface *

-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

-keep class **JsonAdapter {
    <init>(...);
    <fields>;
}

-keepnames @com.squareup.moshi.JsonClass class *


-optimizations !class/unboxing/enum



# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# Enum field names are used by the integrated EnumJsonAdapter.
# values() is synthesized by the Kotlin compiler and is used by EnumJsonAdapter indirectly
# Annotate enums with @JsonClass(generateAdapter = false) to use them with Moshi.
-keepclassmembers @com.squareup.moshi.JsonClass class * extends java.lang.Enum {
    <fields>;
    **[] values();
}

# The name of @JsonClass types is used to look up the generated adapter.
-keepnames @com.squareup.moshi.JsonClass class *

# Retain generated target class's synthetic defaults constructor and keep DefaultConstructorMarker's
# name. We will look this up reflectively to invoke the type's constructor.
#
# We can't _just_ keep the defaults constructor because Proguard/R8's spec doesn't allow wildcard
# matching preceding parameters.
-keepnames class kotlin.jvm.internal.DefaultConstructorMarker
-keepclassmembers @com.squareup.moshi.JsonClass @kotlin.Metadata class * {
    synthetic <init>(...);
}



-keep public class * extends com.squareup.moshi.JsonAdapter {
    <init>(...);
    <fields>;
}


-keepnames @kotlin.Metadata class com.sohohouse.seven.network.core.models.**
-keep class com.sohohouse.seven.network.core.models.** { *; }
-keepclassmembers class com.sohohouse.seven.network.core.models.** { *; }

-keepnames @kotlin.Metadata class com.sohohouse.seven.network.sitecore.models.**
-keep class com.sohohouse.seven.network.sitecore.models.** { *; }
-keepclassmembers class com.sohohouse.seven.network.sitecore.models.** { *; }

-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

#UXCam rules
-keep class com.uxcam.** { *; }
-dontwarn com.uxcam.**