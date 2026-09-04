use std::{ffi::c_void, ptr};

pub(crate) static mut GAME_LIB_HANDLE: *mut c_void = ptr::null_mut();
