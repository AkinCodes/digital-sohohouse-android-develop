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
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFileZ


###########################################################################
################################## OKHTTP ##################################
############################################################################
# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn okhttp3.internal.platform.ConscryptPlatform

# SJ: Added to ignore some proguard issues
-dontnote okhttp3.**
-dontnote org.apache.http.**
-dontnote android.net.http.**
-dontnote com.google.gson.internal.UnsafeAllocator
-dontnote com.squareup.picasso.Utils
-dontwarn okhttp3.internal.**

############################################################################
################################# RETROFIT #################################
############################################################################
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

############################################################################
################################# SWAGGER ##################################
############################################################################

# Not relying on Swagger's OAuth stuff...which is what this is complaining about
-dontwarn com.sohohouse.seven.network.auth.ApiClient
-dontwarn com.sohohouse.seven.network.auth.auth.OAuth
-dontwarn com.sohohouse.seven.network.auth.auth.OAuthOkHttpClient
-dontwarn com.sohohouse.seven.network.auth.auth.OAuth$AccessTokenListener

############################################################################
################################# GSON #####################################
############################################################################
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class com.sohohouse.seven.network.auth.model.** { *; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-dontwarn com.google.gson.Gson$6

############################################################################
############################# MOSHI-JSONAPI ################################
############################################################################

-keepattributes Signature
-keepclassmembers public abstract class moe.banana.jsonapi2.** {
    *;
}

-keep class moe.banana.jsonapi2.** {
    *;
}

-keepclassmembers class ** {
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}

-keep class com.sohohouse.seven.network.vimeo.model.** { *; }

-keep class com.sohohouse.seven.network.core.models.** { *; }

############################################################################
################################# THREETEN #################################
############################################################################
-keep class org.threeten.bp.** { *; }
-dontwarn org.threeten.bp.**


############################################################################
################################### MISC ###################################
############################################################################
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

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

-assumenosideeffects class com.sohohouse.seven.common.utils.DLog {
    public static int e(...);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
}

-optimizations !class/unboxing/enum
