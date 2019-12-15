package open.sqxx.vostok27.model.repository

enum class BluetoothPackageStatus {
	VALID,
	INCORRECT_SIZE,
	INCORRECT_MAGIC_BYTE,
	INCORRECT_CRC,
}