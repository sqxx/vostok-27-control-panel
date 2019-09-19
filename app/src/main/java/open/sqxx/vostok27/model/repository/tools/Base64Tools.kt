package open.sqxx.vostok27.model.repository.tools

import android.util.Base64

class Base64Tools {
	fun decode(input: String) = String(Base64.decode(input.toByteArray(), Base64.DEFAULT))
	fun encode(input: String) = String(Base64.encode(input.toByteArray(), Base64.DEFAULT))
}