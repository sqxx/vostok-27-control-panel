package open.sqxx.vostok27.model.repository

import open.sqxx.vostok27.extension.rx.Variable

@ExperimentalUnsignedTypes
class BluetoothFront {

	val receiver = Variable(ubyteArrayOf())
	val sender = Variable(ubyteArrayOf())
	val status = Variable(BluetoothStatus.NONE)
}