use jni::{
    objects::{JClass, JString},
    sys::{jclass, jstring, JNINativeInterface_, JNI_VERSION_1_6},
    JNIEnv,
};

use std::{
    ffi::{c_char, c_void, CStr},
    ptr,
    sync::Once,
};

const REAL_LIB: &[u8] = b"libgame_real.so\0";

const NATIVE_APP_INIT: &[u8] = b"Java_com_crowdstar_aquarium_Aquarium_nativeAppInitJNI\0";

const NATIVE_SET_PATHS: &[u8] = b"Java_org_cocos2dx_lib_Cocos2dxActivity_nativeSetPaths\0";

static LOAD_REAL: Once = Once::new();

static mut REAL_GAME: *mut c_void = ptr::null_mut();

type NativeAppInitJNI = unsafe extern "system" fn(*mut *const JNINativeInterface_, jclass);

type NativeSetPaths = unsafe extern "system" fn(*mut *const JNINativeInterface_, jclass, jstring);

unsafe fn load_real_game() -> *mut c_void {
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
                    "[Proxy] dlopen failed: {}",
                    CStr::from_ptr(error).to_string_lossy()
                );
            } else {
                eprintln!("[Proxy] dlopen failed: unknown error");
            }

            return;
        }

        REAL_GAME = handle;

        println!("[Proxy] libgame_real.so loaded @ {:p}", handle);
    });

    REAL_GAME
}

unsafe fn get_symbol(name: &[u8]) -> *mut c_void {
    let handle = load_real_game();

    if handle.is_null() {
        eprintln!("[Proxy] libgame_real.so nie jest załadowane!");

        return ptr::null_mut();
    }

    let symbol = libc::dlsym(handle, name.as_ptr() as *const c_char);

    if symbol.is_null() {
        let error = libc::dlerror();

        if !error.is_null() {
            eprintln!(
                "[Proxy] dlsym failed dla {}: {}",
                CStr::from_ptr(name.as_ptr() as *const c_char).to_string_lossy(),
                CStr::from_ptr(error).to_string_lossy()
            );
        }
    }

    symbol
}

#[no_mangle]
pub unsafe extern "system" fn Java_com_crowdstar_aquarium_Aquarium_nativeAppInitJNI(
    env: JNIEnv,
    class: JClass,
) {
    println!("[Proxy] nativeAppInitJNI()");

    let symbol = get_symbol(NATIVE_APP_INIT);

    if symbol.is_null() {
        eprintln!("[Proxy] Nie znaleziono symbolu nativeAppInitJNI!");

        return;
    }

    let real_init: NativeAppInitJNI = std::mem::transmute(symbol);

    println!("[Proxy] Wywołuję oryginalne nativeAppInitJNI()...");

    real_init(env.get_native_interface(), class.into_raw());

    println!("[Proxy] original nativeAppInitJNI() zakończony");
}

#[no_mangle]
pub unsafe extern "system" fn Java_org_cocos2dx_lib_Cocos2dxActivity_nativeSetPaths(
    env: JNIEnv,
    class: JClass,
    path: JString,
) {
    println!("[Proxy] nativeSetPaths()");

    let symbol = get_symbol(NATIVE_SET_PATHS);

    if symbol.is_null() {
        eprintln!("[Proxy] Nie znaleziono symbolu nativeSetPaths!");

        return;
    }

    let real_set_paths: NativeSetPaths = std::mem::transmute(symbol);

    println!("[Proxy] Wywołuję oryginalne nativeSetPaths()...");

    real_set_paths(
        env.get_native_interface(),
        class.into_raw(),
        path.into_raw(),
    );

    println!("[Proxy] original nativeSetPaths() zakończony");
}

#[no_mangle]
pub unsafe extern "system" fn JNI_OnLoad(vm: *mut c_void, _reserved: *mut c_void) -> i32 {
    println!("[Proxy] JNI_OnLoad");

    let handle = load_real_game();

    if handle.is_null() {
        eprintln!("[Proxy] UWAGA: nie udało się załadować libgame_real.so");
    } else {
        println!("[Proxy] Oryginalne libgame_real.so jest gotowe");
    }

    let _ = vm;

    JNI_VERSION_1_6
}
