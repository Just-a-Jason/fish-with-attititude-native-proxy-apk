use std::ffi::{c_char, c_void, CStr};

use jni::sys::{JavaVM as RawJavaVM, JNI_VERSION_1_6};
use tracing::{error, info};

use crate::{
    handlers::REAL_GAME,
    init::{LOAD_REAL, REAL_JNI_ON_LOAD_CALLED},
    strings::ORG_LIB_NAME,
    symbols::{types::RealJNIOnLoad, REAL_JNI_ON_LOAD},
};

pub(crate) unsafe fn load_real_game(vm: *mut RawJavaVM, reserved: *mut c_void) -> *mut c_void {
    LOAD_REAL.call_once(|| {
        info!("Loading libgame_real.so...");

        let handle = libc::dlopen(
            ORG_LIB_NAME.as_ptr() as *const c_char,
            libc::RTLD_NOW | libc::RTLD_GLOBAL,
        );

        if handle.is_null() {
            let error = libc::dlerror();
            if !error.is_null() {
                error!("Dlopen Error: {}", CStr::from_ptr(error).to_string_lossy());
            } else {
                error!("Unknown dlopen error");
            }
            return;
        }

        REAL_GAME = handle;
        info!(
            "libgame_real.so successfully loaded at address: {:p}",
            handle
        );

        let on_load_symbol = libc::dlsym(handle, REAL_JNI_ON_LOAD.as_ptr() as *const c_char);

        if on_load_symbol.is_null() {
            let error = libc::dlerror();
            if !error.is_null() {
                error!(
                    "[Proxy] Nie znaleziono JNI_OnLoad: {}",
                    CStr::from_ptr(error).to_string_lossy()
                );
            } else {
                error!("[Proxy] Nie znaleziono oryginalnego JNI_OnLoad");
            }
            return;
        }

        let real_on_load: RealJNIOnLoad = std::mem::transmute(on_load_symbol);

        let version = real_on_load(vm, reserved);
        info!("Library JNI version: 0x{:x}", version);

        if version != JNI_VERSION_1_6 {
            error!("Expected JNI version: 0x{:x}", version);
        }

        REAL_JNI_ON_LOAD_CALLED = true;
        info!("Successfully ran native init");

        // To do: Fix patch
        // patch_internet_error_popup(handle);
    });

    REAL_GAME
}
