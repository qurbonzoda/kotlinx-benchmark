package kotlinx.benchmark

actual fun String.toByteArrayUtf8(): ByteArray = encodeToByteArray()
