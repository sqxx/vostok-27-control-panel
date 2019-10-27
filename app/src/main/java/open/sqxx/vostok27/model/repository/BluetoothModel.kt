package open.sqxx.vostok27.model.repository

class BluetoothModel {
	@ExperimentalUnsignedTypes
	companion object {

		const val PACKAGE_SIZE = 10 - 2

		const val START_MAGIC = 0xF4.toByte()
		const val END_CR = 0x0D.toByte()
		const val END_LF = 0x0A.toByte()

		const val _P_STARTUP = 0x01.toByte()
		const val _P_INIT_COMPLETE = 0x02.toByte()
		const val _P_NOT_READY = 0x03.toByte()

		const val _P_REQ_CO2 = 0xA1.toByte()
		const val _P_REQ_HUM = 0xA2.toByte()
		const val _P_REQ_TEMP = 0xA3.toByte()
		const val _P_REQ_PRES = 0xA4.toByte()
		const val _P_REQ_SOLAR_PANELS_EF = 0xA5.toByte()

		const val _P_CODE_SUCCESS = 0x00.toByte()
		const val _P_CODE_FAILURE = 0xFF.toByte()

		const val _P_SYSTEM_ENABLED = 0x00.toByte()
		const val _P_SYSTEM_DISABLED = 0xFF.toByte()

		const val _P_SWITCH_PRES_RELIEF_VALVE = 0xB1.toByte()
		const val _P_STATUS_PRES_RELIEF_VALVE = 0xB2.toByte()
		const val _P_SWITCH_PUMP_VALVE = 0xB3.toByte()
		const val _P_STATUS_PUMP_VALVE = 0xB4.toByte()
		const val _P_SWITCH_PROD_CO2 = 0xB5.toByte()
		const val _P_STATUS_PROD_CO2 = 0xB6.toByte()

		const val _P_SET_TIME = 0xD1.toByte()
		const val _P_SET_DAY_TIME = 0xD2.toByte()
		const val _P_SET_NIGHT_TIME = 0xD3.toByte()
		const val _P_GET_TIME = 0xD4.toByte()
		const val _P_GET_DAY_TIME = 0xD5.toByte()
		const val _P_GET_NIGHT_TIME = 0xD6.toByte()
		const val _P_SERIAL_RESET = 0xEA.toByte()

		const val _PE_GAS_LEAK = 0xE1.toByte()
		const val _PE_PACKAGE_ERR = 0xE3.toByte()
		const val _PE_PACKAGE_CRC = 0xE4.toByte()
		const val _PE_UNKNOWN_CMD = 0xE5.toByte()
		const val _PE_PACKAGE_ERR_CRLF = 0xDA.toByte()
		const val _PE_PACKAGE_ERR_MAGIC = 0xF4.toByte()

		val VALUES_COMMANDS = byteArrayOf(
			_P_REQ_CO2,
			_P_REQ_HUM,
			_P_REQ_TEMP,
			_P_REQ_PRES,
			_P_REQ_SOLAR_PANELS_EF
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
				START_MAGIC,
				cmd,
				0, 0, 0, 0,
				0, cmd
			)
		}

		fun extractCrc(data: ByteArray): Int {

			/*
			 * Конвертация в UByte необходима!
			 * Иначе при конвертации в Int сохраняется знак типа Byte,
			 * что ведёт к потере оригинальных данных
			 */

			val highByte = data[PACKAGE_SIZE - 2].toUByte().toInt()
			val lowByte = data[PACKAGE_SIZE - 1].toUByte().toInt()

			return highByte.shl(8) or lowByte
		}

		fun extractValue(data: ByteArray): Int {
			val b1 = data[2].toUByte().toInt()
			val b2 = data[3].toUByte().toInt()
			val b3 = data[4].toUByte().toInt()
			val b4 = data[5].toUByte().toInt()

			return b4 or
				(b3.shl(8)) or
				(b2.shl(16)) or
				(b1.shl(24))
		}
	}
}