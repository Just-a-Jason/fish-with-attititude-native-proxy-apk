use std::{ffi::c_void, ptr};

pub(crate) static mut REAL_GAME: *mut c_void = ptr::null_mut();
