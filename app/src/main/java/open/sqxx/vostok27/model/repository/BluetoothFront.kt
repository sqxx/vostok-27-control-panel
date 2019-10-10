package open.sqxx.vostok27.model.repository

import open.sqxx.vostok27.extension.rx.Variable

class BluetoothFront {

	val receiver = Variable(byteArrayOf())
	val sender = Variable(byteArrayOf())
	val status = Variable(BluetoothStatus.NONE)
}