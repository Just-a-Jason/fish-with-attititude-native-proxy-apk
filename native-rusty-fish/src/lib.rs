use jni::sys::jbyteArray;
use jni::sys::{jboolean, jfloat, jfloatArray, jintArray, jlong};
use jni::sys::{jclass, jint, jstring, JNIEnv as RawJNIEnv, JavaVM as RawJavaVM, JNI_VERSION_1_6};
use std::{
    ffi::{c_char, c_void, CStr},
    ptr,
    sync::Once,
};

const REAL_LIB: &[u8] = b"libgame_real.so\0";
const NATIVE_APP_INIT: &[u8] = b"Java_com_crowdstar_aquarium_Aquarium_nativeAppInitJNI\0";
const NATIVE_SET_PATHS: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxActivity_nativeSetPaths\0";
const REAL_JNI_ON_LOAD: &[u8] = b"JNI_OnLoad\0";
const NATIVE_ON_RESUME: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume\0";
const NATIVE_INIT: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit\0";
const NATIVE_DELETE_BACKWARD: &[u8] =
    b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeDeleteBackward\0";
const NATIVE_GET_CONTENT_TEXT: &[u8] =
    b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeGetContentText\0";
const NATIVE_INSERT_TEXT: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInsertText\0";
const NATIVE_KEY_DOWN: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeKeyDown\0";
const NATIVE_ON_PAUSE: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnPause\0";
const NATIVE_RENDER: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeRender\0";
const NATIVE_TOUCHES_BEGIN: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesBegin\0";
const NATIVE_TOUCHES_CANCEL: &[u8] =
    b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesCancel\0";
const NATIVE_TOUCHES_END: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesEnd\0";
const NATIVE_TOUCHES_MOVE: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeTouchesMove\0";
const NATIVE_INIT_BITMAP_DC_BITMAP: &[u8] =
    b"Java_org_cocos2dx_lib_Cocos2dxBitmap_nativeInitBitmapDC\0";

static LOAD_REAL: Once = Once::new();
static mut REAL_GAME: *mut c_void = ptr::null_mut();
static mut REAL_JNI_ON_LOAD_CALLED: bool = false;

type FnInitBitmapDC = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint, jint, jbyteArray);
type NativeInit = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint, jint);
type NativeAppInitJNI = unsafe extern "system" fn(*mut RawJNIEnv, jclass);
type NativeSetPaths = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jstring);
type RealJNIOnLoad = unsafe extern "system" fn(*mut RawJavaVM, *mut c_void) -> i32;
type NativeOnResume = unsafe extern "system" fn(*mut RawJNIEnv, jclass);
type FnVoid = unsafe extern "system" fn(*mut RawJNIEnv, jclass);
type FnInsertText = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jstring);
type FnGetContentText = unsafe extern "system" fn(*mut RawJNIEnv, jclass) -> jstring;
type FnKeyDown = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint) -> jboolean;
type FnTouchSingle = unsafe extern "system" fn(*mut RawJNIEnv, jclass, jint, jfloat, jfloat, jlong);
type FnTouchArray =
    unsafe extern "system" fn(*mut RawJNIEnv, jclass, jintArray, jfloatArray, jfloatArray, jlong);

unsafe fn load_real_game(vm: *mut RawJavaVM, reserved: *mut c_void) -> *mut c_void {
    LOAD_REAL.call_once(|| {
        println!("[Proxy] Ładowanie libgame_real.so...");

        let handle = libc::dlopen(
            REAL_LIB.as_ptr() as *const c_char,
            libc::RTLD_NOW | libc::RTLD_GLOBAL,
        );

        if handle.is_null() {
            let error = libc::dlerror();
            if !error.is_null() {
                eprintln!(
                    "[Proxy] Błąd dlopen: {}",
                    CStr::from_ptr(error).to_string_lossy()
                );
            } else {
                eprintln!("[Proxy] Błąd dlopen: nieznany błąd");
            }
            return;
        }

        REAL_GAME = handle;
        println!(
            "[Proxy] libgame_real.so załadowane pod adresem {:p}",
            handle
        );

        let on_load_symbol = libc::dlsym(handle, REAL_JNI_ON_LOAD.as_ptr() as *const c_char);

        if on_load_symbol.is_null() {
            let error = libc::dlerror();
            if !error.is_null() {
                eprintln!(
                    "[Proxy] Nie znaleziono JNI_OnLoad: {}",
                    CStr::from_ptr(error).to_string_lossy()
                );
            } else {
                eprintln!("[Proxy] Nie znaleziono oryginalnego JNI_OnLoad");
            }
            return;
        }

        println!("[Proxy] Oryginalny JNI_OnLoad @ {:p}", on_load_symbol);

        let real_on_load: RealJNIOnLoad = std::mem::transmute(on_load_symbol);

        println!("[Proxy] Wywoływanie oryginalnego JNI_OnLoad()...");
        let version = real_on_load(vm, reserved);
        println!("[Proxy] Oryginalny JNI_OnLoad() zwrócił: 0x{:x}", version);

        if version != JNI_VERSION_1_6 {
            eprintln!("[Proxy] Oczekiwano innej wersji JNI: 0x{:x}", version);
        }

        REAL_JNI_ON_LOAD_CALLED = true;
        println!("[Proxy] Inicjalizacja JNI_OnLoad zakończona sukcesem");
    });

    REAL_GAME
}

unsafe fn get_symbol(name: &[u8]) -> *mut c_void {
    let handle = REAL_GAME;

    if handle.is_null() {
        eprintln!("[Proxy] Błąd: libgame_real.so nie została załadowana!");
        return ptr::null_mut();
    }

    let symbol = libc::dlsym(handle, name.as_ptr() as *const c_char);

    if symbol.is_null() {
        let error = libc::dlerror();
        if !error.is_null() {
            eprintln!(
                "[Proxy] Błąd dlsym({}): {}",
                CStr::from_ptr(name.as_ptr() as *const c_char).to_string_lossy(),
                CStr::from_ptr(error).to_string_lossy()
            );
        } else {
            eprintln!("[Proxy] dlsym zwrócił NULL");
        }
    }

    symbol
}

#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(vm: *mut RawJavaVM, reserved: *mut c_void) -> i32 {
    println!("[Proxy] Wywołano JNI_OnLoad (JavaVM = {:p})", vm);

    let handle = load_real_game(vm, reserved);

    if handle.is_null() {
        eprintln!("[Proxy] Nie udało się załadować libgame_real.so");
    } else {
        println!("[Proxy] Statut ładowania JNI: OK");
    }

    JNI_VERSION_1_6
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_crowdstar_aquarium_Aquarium_nativeAppInitJNI(
    env: *mut RawJNIEnv,
    class: jclass,
) {
    println!(
        "[Proxy] Przechwycono nativeAppInitJNI() | env: {:p}, class: {:p}",
        env, class
    );

    let symbol = get_symbol(NATIVE_APP_INIT);

    if symbol.is_null() {
        eprintln!("[Proxy] Nie znaleziono symbolu nativeAppInitJNI!");
        return;
    }

    println!(
        "[Proxy] Przekazywanie wywołania do oryginału @ {:p}",
        symbol
    );
    let real_init: NativeAppInitJNI = std::mem::transmute(symbol);
    real_init(env, class);
    println!("[Proxy] Oryginalne nativeAppInitJNI() zakończone");
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeInit(
    env: *mut RawJNIEnv,
    class: jclass,
    width: jint,
    height: jint,
) {
    println!("[Proxy] nativeInit: width={}, height={}", width, height);

    let symbol = get_symbol(NATIVE_INIT);
    if !symbol.is_null() {
        let real_fn: NativeInit = std::mem::transmute(symbol);
        real_fn(env, class, width, height);
    }
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxRenderer_nativeOnResume(
    env: *mut RawJNIEnv,
    class: jclass,
) {
    println!(
        "[Proxy] Przechwycono nativeOnResume() | env: {:p}, class: {:p}",
        env, class
    );

    let symbol = get_symbol(NATIVE_ON_RESUME);

    if symbol.is_null() {
        eprintln!("[Proxy] Nie znaleziono symbolu nativeOnResume!");
        return;
    }

    println!(
        "[Proxy] Przekazywanie wywołania nativeOnResume do oryginału @ {:p}",
        symbol
    );
    let real_on_resume: NativeOnResume = std::mem::transmute(symbol);
    real_on_resume(env, class);
    println!("[Proxy] Oryginalne nativeOnResume() zakończone");
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxActivity_nativeSetPaths(
    env: *mut RawJNIEnv,
    class: jclass,
    path: jstring,
) {
    println!(
        "[Proxy] Przechwycono nativeSetPaths() | env: {:p}, class: {:p}, path: {:p}",
        env, class, path
    );

    let symbol = get_symbol(NATIVE_SET_PATHS);

    if symbol.is_null() {
        eprintln!("[Proxy] Nie znaleziono symbolu nativeSetPaths!");
        return;
    }

    println!(
        "[Proxy] Przekazywanie wywołania do oryginału @ {:p}",
        symbol
    );
    let real_set_paths: NativeSetPaths = std::mem::transmute(symbol);
    real_set_paths(env, class, path);
    println!("[Proxy] Oryginalne nativeSetPaths() zakończone");
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
