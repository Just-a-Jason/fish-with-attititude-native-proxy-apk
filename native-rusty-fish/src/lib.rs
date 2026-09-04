mod handlers;
mod init;
#[allow(unused)]
mod patch;
mod proxy;
mod strings;
mod symbols;
mod utils;

use symbols::types::*;

use jni::sys::jbyteArray;
use jni::sys::{jboolean, jfloat, jfloatArray, jintArray, jlong};
use jni::sys::{jclass, jint, jstring, JNIEnv as RawJNIEnv, JavaVM as RawJavaVM, JNI_VERSION_1_6};
use std::ffi::c_char;
use std::{ffi::c_void, ptr};
use tracing::{error, info};

use crate::strings::PROXY_LIB_VERSION;
use crate::symbols::*;
use crate::utils::get_symbol;

#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(vm: *mut RawJavaVM, reserved: *mut c_void) -> i32 {
    init::init_tracing();
    info!("[Proxy] Invoked JNI_OnLoad (JavaVM = {:p})", vm);

    let handle = proxy::load_real_game(vm, reserved);

    if handle.is_null() {
        error!("[Proxy] Failed to load libgame_real.so");
    } else {
        info!("[Proxy] JNI STATUS: OK");
    }

    info!(
        "[Proxy] Proxy (rusty_fish) successfully loaded. Proxy version: {}",
        PROXY_LIB_VERSION
    );

    JNI_VERSION_1_6
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_crowdstar_aquarium_Aquarium_getProxyVersion(
    env: jni::JNIEnv,
    _class: jclass,
) -> jstring {
    info!("[Proxy] getProxyVersion() called");

    env.new_string(PROXY_LIB_VERSION)
        .expect("Couldn't create java string!")
        .into_raw()
}

#[no_mangle]
pub unsafe extern "C" fn Java_com_crowdstar_aquarium_Aquarium_nativeAppInitJNI(
    env: *mut RawJNIEnv,
    path: *const c_char,
) -> u64 {
    info!(
        "[Proxy] Hooked fn nativeAppInitJNI | env: {:p}, path: {:p}",
        env, path
    );

    let symbol = get_symbol(NATIVE_APP_INIT);

    if symbol.is_null() {
        error!("[Proxy] Error: symbol nativeAppInitJNI was not found!");
        return 0;
    }

    let real_init: NativeAppInitJNI = std::mem::transmute(symbol);

    let result = real_init(env, path);

    info!(
        "[Proxy] nativeAppInitJNI executed successfully with result: {:#x}",
        result
    );

    result
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit(
    env: *mut RawJNIEnv,
    class: jclass,
    width: jint,
    height: jint,
) {
    info!("[Proxy] nativeInit: width={}, height={}", width, height);

    let symbol = get_symbol(NATIVE_INIT);
    if !symbol.is_null() {
        let real_fn: NativeInit = std::mem::transmute(symbol);
        real_fn(env, class, width, height);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_crowdstar_aquarium_Aquarium_nativeConnectedToIntarweb(
    env: *mut RawJNIEnv,
    class: jclass,
    _is_connected: jint,
) {
    info!("[Proxy] Hooked fn nativeConnectedToIntarweb forcing -> Online (1)");

    let symbol = get_symbol(NATIVE_CONNECTED_TO_INTARWEB);
    if !symbol.is_null() {
        let real_fn: FnConnectedToIntarweb = std::mem::transmute(symbol);
        real_fn(env, class, 1);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume(
    env: *mut RawJNIEnv,
    class: jclass,
) {
    info!("[Proxy] Hooked fn nativeOnResume()");
    let symbol = get_symbol(NATIVE_ON_RESUME);
    if !symbol.is_null() {
        let real_on_resume: NativeOnResume = std::mem::transmute(symbol);
        real_on_resume(env, class);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxActivity_nativeSetPaths(
    env: *mut RawJNIEnv,
    class: jclass,
    path: jstring,
) {
    info!("[Proxy] Hooked fn nativeSetPaths()");
    let symbol = get_symbol(NATIVE_SET_PATHS);
    if !symbol.is_null() {
        let real_set_paths: NativeSetPaths = std::mem::transmute(symbol);
        real_set_paths(env, class, path);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward(
    env: *mut RawJNIEnv,
    class: jclass,
) {
    let symbol = get_symbol(NATIVE_DELETE_BACKWARD);
    if !symbol.is_null() {
        let real_fn: FnVoid = std::mem::transmute(symbol);
        real_fn(env, class);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText(
    env: *mut RawJNIEnv,
    class: jclass,
) -> jstring {
    let symbol = get_symbol(NATIVE_GET_CONTENT_TEXT);
    if !symbol.is_null() {
        let real_fn: FnGetContentText = std::mem::transmute(symbol);
        return real_fn(env, class);
    }
    ptr::null_mut()
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText(
    env: *mut RawJNIEnv,
    class: jclass,
    text: jstring,
) {
    let symbol = get_symbol(NATIVE_INSERT_TEXT);
    if !symbol.is_null() {
        let real_fn: FnInsertText = std::mem::transmute(symbol);
        real_fn(env, class, text);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC(
    env: *mut RawJNIEnv,
    class: jclass,
    width: jint,
    height: jint,
    pixels: jbyteArray,
) {
    let symbol = get_symbol(NATIVE_INIT_BITMAP_DC_BITMAP);
    if !symbol.is_null() {
        let real_fn: FnInitBitmapDC = std::mem::transmute(symbol);
        real_fn(env, class, width, height, pixels);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown(
    env: *mut RawJNIEnv,
    class: jclass,
    key_code: jint,
) -> jboolean {
    let symbol = get_symbol(NATIVE_KEY_DOWN);
    if !symbol.is_null() {
        let real_fn: FnKeyDown = std::mem::transmute(symbol);
        return real_fn(env, class, key_code);
    }
    0
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause(
    env: *mut RawJNIEnv,
    class: jclass,
) {
    let symbol = get_symbol(NATIVE_ON_PAUSE);
    if !symbol.is_null() {
        let real_fn: FnVoid = std::mem::transmute(symbol);
        real_fn(env, class);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender(
    env: *mut RawJNIEnv,
    class: jclass,
) {
    let symbol = get_symbol(NATIVE_RENDER);
    if !symbol.is_null() {
        let real_fn: FnVoid = std::mem::transmute(symbol);
        real_fn(env, class);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin(
    env: *mut RawJNIEnv,
    class: jclass,
    id: jint,
    x: jfloat,
    y: jfloat,
    timestamp: jlong,
) {
    let symbol = get_symbol(NATIVE_TOUCHES_BEGIN);
    if !symbol.is_null() {
        let real_fn: FnTouchSingle = std::mem::transmute(symbol);
        real_fn(env, class, id, x, y, timestamp);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel(
    env: *mut RawJNIEnv,
    class: jclass,
    ids: jintArray,
    xs: jfloatArray,
    ys: jfloatArray,
    timestamp: jlong,
) {
    let symbol = get_symbol(NATIVE_TOUCHES_CANCEL);
    if !symbol.is_null() {
        let real_fn: FnTouchArray = std::mem::transmute(symbol);
        real_fn(env, class, ids, xs, ys, timestamp);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd(
    env: *mut RawJNIEnv,
    class: jclass,
    id: jint,
    x: jfloat,
    y: jfloat,
    timestamp: jlong,
) {
    let symbol = get_symbol(NATIVE_TOUCHES_END);
    if !symbol.is_null() {
        let real_fn: FnTouchSingle = std::mem::transmute(symbol);
        real_fn(env, class, id, x, y, timestamp);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove(
    env: *mut RawJNIEnv,
    class: jclass,
    ids: jintArray,
    xs: jfloatArray,
    ys: jfloatArray,
    timestamp: jlong,
) {
    let symbol = get_symbol(NATIVE_TOUCHES_MOVE);
    if !symbol.is_null() {
        let real_fn: FnTouchArray = std::mem::transmute(symbol);
        real_fn(env, class, ids, xs, ys, timestamp);
    }
}
