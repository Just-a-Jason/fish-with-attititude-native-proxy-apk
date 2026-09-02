use jni::sys::jclass;

#[no_mangle]
pub extern "system" fn Java_com_helloworld_HelloWorld_hello(
    env: jni::JNIEnv,
    _class: jclass,
) -> jni::sys::jstring {
    let msg = "Hello from rust arm!".to_owned();

    env.new_string(msg)
    .expect("Couldn't create java string!")
        .into_raw()
}

