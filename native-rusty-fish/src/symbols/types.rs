use std::ffi::c_void;

use jni::sys::{
    jboolean, jbyteArray, jclass, jfloat, jfloatArray, jint, jintArray, jlong, jstring,
    JNIEnv as RawJNIEnv, JavaVM as RawJavaVM,
};

pub(crate) type FnInitBitmapDC =
    unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint, jint, jbyteArray);
pub(crate) type NativeInit = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint, jint);
pub(crate) type NativeAppInitJNI = unsafe extern "system" fn(*mut RawJNIEnv, jclass);
pub(crate) type NativeSetPaths = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jstring);
pub(crate) type RealJNIOnLoad = unsafe extern "system" fn(*mut RawJavaVM, *mut c_void) -> i32;
pub(crate) type NativeOnResume = unsafe extern "system" fn(*mut RawJNIEnv, jclass);
pub(crate) type FnVoid = unsafe extern "system" fn(*mut RawJNIEnv, jclass);
pub(crate) type FnInsertText = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jstring);
pub(crate) type FnGetContentText = unsafe extern "system" fn(*mut RawJNIEnv, jclass) -> jstring;
pub(crate) type FnKeyDown = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint) -> jboolean;
pub(crate) type FnTouchSingle =
    unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint, jfloat, jfloat, jlong);
pub(crate) type FnTouchArray =
    unsafe extern "system" fn(*mut RawJNIEnv, jclass, jintArray, jfloatArray, jfloatArray, jlong);
pub(crate) type FnConnectedToIntarweb = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jboolean);
