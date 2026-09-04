use std::{
    ffi::{c_char, c_void, CStr},
    ptr,
};

use tracing::error;

use crate::handlers::REAL_GAME;

pub(crate) unsafe fn get_symbol(name: &[u8]) -> *mut c_void {
    let handle = REAL_GAME;

    if handle.is_null() {
        error!("[Proxy] Błąd: libgame_real.so nie została załadowana!");
        return ptr::null_mut();
    }

    let symbol = libc::dlsym(handle, name.as_ptr() as *const c_char);

    if symbol.is_null() {
        let error = libc::dlerror();
        if !error.is_null() {
            error!(
                "[Proxy] Błąd dlsym({}): {}",
                CStr::from_ptr(name.as_ptr() as *const c_char).to_string_lossy(),
                CStr::from_ptr(error).to_string_lossy()
            );
        } else {
            error!("[Proxy] dlsym zwrócił NULL");
        }
    }

    symbol
}
