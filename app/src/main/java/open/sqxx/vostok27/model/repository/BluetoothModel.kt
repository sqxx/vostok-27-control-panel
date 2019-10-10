package open.sqxx.vostok27.model.repository

class BluetoothModel {
	@ExperimentalUnsignedTypes
	companion object {

		const val PACKAGE_SIZE = 8

		const val MAGIC_BYTE = 0xF4.toByte()

		const val _P_STARTUP = 0x01.toByte()
		const val _P_INIT_COMPLETE = 0x02.toByte()
		const val _P_NOT_READY = 0x03.toByte()
		const val _P_UNKNOWN_CMD = 0x0F.toByte()

		const val _P_REQ_CO2 = 0xA1.toByte()
		const val _P_REQ_HUM = 0xA2.toByte()
		const val _P_REQ_TEMP = 0xA3.toByte()
		const val _P_REQ_PRES = 0xA4.toByte()
		const val _P_REQ_SOLAR_PANELS_EF = 0xA5.toByte()

		val VALUES_COMMANDS = byteArrayOf(
			_P_REQ_CO2,
			_P_REQ_HUM,
			_P_REQ_TEMP,
			_P_REQ_PRES
			//_P_REQ_SOLAR_PANELS_EF
		)

		fun calculateCrc(data: ByteArray): Int {
			var crc: Int = 0x00

			for (i in 1..data.size - 3) {
				crc += data[i].toUByte().toInt()
			}

			return crc
		}

		fun requestData(btFront: BluetoothFront, cmd: Byte) {
			btFront.sender.value = byteArrayOf(
				MAGIC_BYTE,
				cmd,
				0, 0, 0, 0,
				cmd, 0
			)
		}

		fun extractCrc(data: ByteArray): Int {

			/*
			 * Конвертация в UByte необходима!
			 * Иначе при конвертации в Int сохраняется знак типа Byte,
			 * что ведёт к потере оригинальных данных
			 */

			val lowByte = data[PACKAGE_SIZE - 2].toUByte().toInt()
			val highByte = data[PACKAGE_SIZE - 1].toUByte().toInt()

			return highByte.shl(8) or lowByte
		}

		fun extractValue(data: ByteArray): Int {
			val b1 = data[2].toUByte().toInt()
			val b2 = data[3].toUByte().toInt()
			val b3 = data[4].toUByte().toInt()
			val b4 = data[5].toUByte().toInt()

			return b1 or
				(b2.shl(8)) or
				(b3.shl(16)) or
				(b4.shl(24))
		}
	}
}