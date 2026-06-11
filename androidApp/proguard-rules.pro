# R8 / ProGuard rules for the release build.
#
# Compose and Kotlin ship their own rules automatically (via META-INF/proguard
# in their artifacts), so this file stays empty until the app gains something
# reflection-based (JSON serialization, etc.). Add keep rules here if a release
# build ever crashes with ClassNotFoundException/NoSuchMethodException.
