package open.sqxx.vostok27.model.repository

import app.akexorcist.bluetotohspp.library.BluetoothState
import open.sqxx.vostok27.extension.rx.Variable

class BluetoothFront {

	val receiver = Variable(byteArrayOf())
	val sender = Variable(byteArrayOf())
	val status = Variable(BluetoothState.STATE_NONE)
}