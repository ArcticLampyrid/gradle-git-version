-ignorewarnings
-keep class * implements org.gradle.api.Plugin {
    public <methods>;
}
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-repackageclasses com/github/arcticlampyrid/gradle/gitversion/internal