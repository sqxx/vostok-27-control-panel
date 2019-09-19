package open.sqxx.vostok27.di

// See: https://youtrack.jetbrains.com/issue/KT-18918
data class PrimitiveWrapper<out T>(val value: T)