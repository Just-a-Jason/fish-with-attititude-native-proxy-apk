use jni::sys::{jclass, jstring};
use std::ffi::c_void;

const RTLD_NEXT: *mut c_void = -1isize as *mut c_void;

type BasePopupCtor = unsafe extern "C" fn(*mut c_void, *mut c_void);
type This = *mut c_void;
type JsonNode = *mut c_void;

#[no_mangle]
pub extern "system" fn Java_com_helloworld_HelloWorld_hello(
    env: jni::JNIEnv,
    _class: jclass,
) -> jstring {
    let msg = "Hello from rust arm!".to_owned();

    env.new_string(msg)
        .expect("Couldn't create java string!")
        .into_raw()
}

const BASE_POPUP_SYMBOL_CONSTRUCTOR_1: &[u8] = b"_ZN9BasePopupC1EP8JSONNode\0";
const BASE_POPUP_SYMBOL_CONSTRUCTOR_2: &[u8] = b"_ZN9BasePopupC2EP8JSONNode\0";

unsafe fn call_original_and_hook(symbol: &[u8], this: This, json_node: JsonNode) {
    println!("[Proxy] Przechwycono BasePopup! this: {:p}", this);

    let real_func_ptr = libc::dlsym(RTLD_NEXT, symbol.as_ptr() as *const _);

    if !real_func_ptr.is_null() {
        let original_ctor: BasePopupCtor = std::mem::transmute(real_func_ptr);
        original_ctor(this, json_node);
    } else {
        eprintln!("[Proxy] BŁĄD: Nie znaleziono oryginalnego symbolu w libgame_real.so!");
    }

    println!("[Proxy] BasePopup został utworzony. Dopisuję własną logikę!");
}

#[no_mangle]
pub unsafe extern "C" fn _ZN9BasePopupC1EP8JSONNode(this: This, json_node: JsonNode) {
    call_original_and_hook(BASE_POPUP_SYMBOL_CONSTRUCTOR_1, this, json_node);
}

#[no_mangle]
pub unsafe extern "C" fn _ZN9BasePopupC2EP8JSONNode(this: This, json_node: JsonNode) {
    call_original_and_hook(BASE_POPUP_SYMBOL_CONSTRUCTOR_2, this, json_node);
}
