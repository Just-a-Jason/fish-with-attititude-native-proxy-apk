use std::sync::Once;

use tracing_subscriber::{layer::SubscriberExt, Registry};

pub(crate) static INIT_LOGGER: Once = Once::new();
pub(crate) static LOAD_REAL: Once = Once::new();

pub(crate) static mut REAL_JNI_ON_LOAD_CALLED: bool = false;

pub(crate) fn init_tracing() {
    INIT_LOGGER.call_once(|| {
        let android_layer =
            tracing_android::layer("ProxyHook").expect("Failed to create android tracing layer.");

        let subscriber = Registry::default().with(android_layer);

        tracing::subscriber::set_global_default(subscriber)
            .expect("Failed to create global android tracing subscriber.");
    });
}
