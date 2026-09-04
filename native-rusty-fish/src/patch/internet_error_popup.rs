use std::ffi::c_void;

use tracing::{error, info};

use crate::symbols::NATIVE_APP_INIT;

const INTERNET_ERROR_POPUP_OFFSET: usize = 0x004e10ac;

pub(crate) unsafe fn patch_internet_error_popup(real_game_handle: *mut c_void) {
    if real_game_handle.is_null() {
        error!("[Patch] Error: real_game_handle is NULL!");
        return;
    }

    let known_symbol = libc::dlsym(
        real_game_handle,
        NATIVE_APP_INIT.as_ptr() as *const libc::c_char,
    );
    if known_symbol.is_null() {
        error!("[Patch] Helper address is missing.");
        return;
    }

    let native_app_init_offset: usize = 0x004dc4f7;
    let base_addr = (known_symbol as usize) - native_app_init_offset;
    let target_addr = (base_addr + INTERNET_ERROR_POPUP_OFFSET) as *mut u8;

    info!("[Patch] Base memory address: 0x{:x}", base_addr);
    info!("[Patch] Patching at memory address: {:p}", target_addr);

    let page_size = libc::sysconf(libc::_SC_PAGESIZE) as usize;
    let page_start = (target_addr as usize) & !(page_size - 1);

    if libc::mprotect(
        page_start as *mut c_void,
        page_size,
        libc::PROT_READ | libc::PROT_WRITE | libc::PROT_EXEC,
    ) == 0
    {
        *target_addr = 0x70;
        *(target_addr.add(1)) = 0x47;

        libc::mprotect(
            page_start as *mut c_void,
            page_size,
            libc::PROT_READ | libc::PROT_EXEC,
        );

        info!("[Patch] Successfully patched internet error popup.");
    } else {
        error!("[Patch] Błąd mprotect: nie udało się zmienić praw dostępu do pamięci!");
    }
}
