@echo off
"E:\\AndroidStudio\\ASjdk\\cmake\\3.22.1\\bin\\cmake.exe" ^
  "-HE:\\STUDIA\\QuickyScan\\opencv\\libcxx_helper" ^
  "-DCMAKE_SYSTEM_NAME=Android" ^
  "-DCMAKE_EXPORT_COMPILE_COMMANDS=ON" ^
  "-DCMAKE_SYSTEM_VERSION=21" ^
  "-DANDROID_PLATFORM=android-21" ^
  "-DANDROID_ABI=x86" ^
  "-DCMAKE_ANDROID_ARCH_ABI=x86" ^
  "-DANDROID_NDK=E:\\AndroidStudio\\ASjdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_ANDROID_NDK=E:\\AndroidStudio\\ASjdk\\ndk\\25.1.8937393" ^
  "-DCMAKE_TOOLCHAIN_FILE=E:\\AndroidStudio\\ASjdk\\ndk\\25.1.8937393\\build\\cmake\\android.toolchain.cmake" ^
  "-DCMAKE_MAKE_PROGRAM=E:\\AndroidStudio\\ASjdk\\cmake\\3.22.1\\bin\\ninja.exe" ^
  "-DCMAKE_LIBRARY_OUTPUT_DIRECTORY=E:\\STUDIA\\QuickyScan\\opencv\\build\\intermediates\\cxx\\Debug\\59164pc1\\obj\\x86" ^
  "-DCMAKE_RUNTIME_OUTPUT_DIRECTORY=E:\\STUDIA\\QuickyScan\\opencv\\build\\intermediates\\cxx\\Debug\\59164pc1\\obj\\x86" ^
  "-DCMAKE_BUILD_TYPE=Debug" ^
  "-BE:\\STUDIA\\QuickyScan\\opencv\\.cxx\\Debug\\59164pc1\\x86" ^
  -GNinja ^
  "-DANDROID_STL=c++_shared"
