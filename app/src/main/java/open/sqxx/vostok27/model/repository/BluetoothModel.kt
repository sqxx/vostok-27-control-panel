package open.sqxx.vostok27.model.repository

@Suppress(
	"MemberVisibilityCanBePrivate",
	"ObjectPropertyName"
)
@ExperimentalUnsignedTypes
class BluetoothModel {
	companion object {

		//region Протокол

		/*
		 * Сделано для удобства
		 * BluetoothSPP при получении данных обрезает CR LF
		 * Не учитываем 0x0D 0x0A при работе с данными
		 */
		const val PACKAGE_SIZE = 10 - 2

		const val START_MAGIC: UByte = 0xF4u

		const val _P_CODE_SUCCESS: UByte = 0xFFu
		const val _P_CODE_FAILURE: UByte = 0x00u

		const val _P_SYSTEM_ENABLED: UByte = 0xFFu
		const val _P_SYSTEM_DISABLED: UByte = 0x00u

		//region Состояние

		const val _P_STARTUP: UByte = 0x01u
		const val _P_INIT_COMPLETE: UByte = 0x02u
		const val _P_NOT_READY: UByte = 0x03u

		const val _P_LOW_VOLTAGE_ON_SOLAR_PANELS: UByte = 0x0Au
		const val _P_LOW_PRESSURE: UByte = 0x0Bu
		const val _P_NOT_SEALED: UByte = 0x0Cu

		//endregion

		//region Показатели

		const val _P_REQ_CO2: UByte = 0xA1u
		const val _P_REQ_HUM: UByte = 0xA2u
		const val _P_REQ_TEMP: UByte = 0xA3u
		const val _P_REQ_PRES: UByte = 0xA4u
		const val _P_BATTERY_VOLTAGE: UByte = 0xA5u
		const val _P_ENERGY_USAGE: UByte = 0xA6u
		const val _P_ENERGY_GEN: UByte = 0xA7u

		//endregion

		//region Управление системами

		// Клапан накачки станции
		const val _P_SWITCH_PUMP_VALVE: UByte = 0xB0u
		const val _P_STATUS_PUMP_VALVE: UByte = 0xB1u

		// Клапан сброса давления
		const val _P_SWITCH_PRES_RELIEF_VALVE: UByte = 0xB2u
		const val _P_STATUS_PRES_RELIEF_VALVE: UByte = 0xB3u

		// Выработка CO2
		const val _P_SWITCH_PROD_CO2: UByte = 0xB4u
		const val _P_STATUS_PROD_CO2: UByte = 0xB5u

		// Нейтрализатор CO2
		const val _P_SWITCH_CO2_NUTRALIZATION: UByte = 0xB6u
		const val _P_STATUS_CO2_NUTRALIZATION: UByte = 0xB7u

		// Обогреватель
		const val _P_SWITCH_HEAT_MODULE: UByte = 0xB8u
		const val _P_STATUS_HEAT_MODULE: UByte = 0xB9u

		// Вентилятор обогревателя
		const val _P_SWITCH_FAN: UByte = 0xBAu
		const val _P_STATUS_FAN: UByte = 0xBBu

		// Камеры
		const val _P_SWITCH_CAMERAS: UByte = 0xBCu
		const val _P_STATUS_CAMERAS: UByte = 0xBDu

		// Автоподдержка давления
		const val _P_SWITCH_AUTO_PRES: UByte = 0xC1u
		const val _P_STATUS_AUTO_PRES: UByte = 0xC2u

		// Автоматическое управление освещением
		const val _P_SWITCH_AUTO_LIGHT: UByte = 0xBEu
		const val _P_STATUS_AUTO_LIGHT: UByte = 0xBFu

		// Уровень яркости
		const val _P_SET_LIGHT_LEVEL: UByte = 0xD4u
		const val _P_GET_LIGHT_LEVEL: UByte = 0xDDu

		// Время на станции
		const val _P_SET_TIME: UByte = 0xD1u
		const val _P_GET_TIME: UByte = 0xDAu

		// Время дня
		const val _P_SET_DAY_TIME: UByte = 0xD2u
		const val _P_GET_DAY_TIME: UByte = 0xDBu

		// Время ночи
		const val _P_SET_NIGHT_TIME: UByte = 0xD3u
		const val _P_GET_NIGHT_TIME: UByte = 0xDCu

		//endregion

		//region Исключения

		const val _PE_PACKAGE_ERROR: UByte = 0xE1u
		const val _PE_PACKAGE_CRC: UByte = 0xE2u
		const val _PE_UNKNOWN_CMD: UByte = 0xE3u

		const val _PE_PACKAGE_ERR_CRLF: UByte = 0xDAu
		const val _PE_PACKAGE_ERR_MAGIC: UByte = 0xF4u

		//endregion

		//endregion

		//region Сборка пакета

		fun buildPackage(cmd: UByte, value: UInt): UByteArray {

			/*
			 * Структура пакета:
			 *
			 *    0 - Магическое число
			 *    1 - Команда
			 * 2..5 - Значение
			 * 6..7 - Контрольная сумма
			 * 8..9 - CR & LF
			 *        Добавляется автоматически BluetoothSPP.
			 *
			 * Порядок байт - big-endian
			 */

			val pack = ubyteArrayOf(
				START_MAGIC,
				0u,
				0u, 0u, 0u, 0u,
				0u, 0u
			)

			packCommand(pack, cmd.toUInt())
			packValue(pack, value)
			packCrc(pack)

			return pack
		}

		fun packCommand(data: UByteArray, value: UInt) {
			data[1] = value.toUByte()
		}

		fun packValue(data: UByteArray, value: UInt) {
			data[2] = ((value and 0xFF000000U) shr 24).toUByte()
			data[3] = ((value and 0x00FF0000U) shr 16).toUByte()
			data[4] = ((value and 0x0000FF00U) shr 8).toUByte()
			data[5] = ((value and 0x000000FFU) shr 0).toUByte()
		}

		fun packCrc(data: UByteArray) {
			val crc = calculateCrc(data)

			data[PACKAGE_SIZE - 2] = ((crc and 0xFF00U) shr 8).toUByte()
			data[PACKAGE_SIZE - 1] = ((crc and 0x00FFU) shr 0).toUByte()
		}

		fun calculateCrc(data: UByteArray): UInt {
			var crc = 0x00U

			for (i in 1..data.size - 3) {
				crc += data[i].toUInt()
			}

			return crc
		}

		//endregion

		//region Извлечение данных

		fun extractCommand(data: UByteArray): UByte =
			data[1]

		fun extractValue(data: UByteArray): UInt {
			val b1 = data[2].toUInt()
			val b2 = data[3].toUInt()
			val b3 = data[4].toUInt()
			val b4 = data[5].toUInt()

			return b4 or
				(b3.shl(8)) or
				(b2.shl(16)) or
				(b1.shl(24))
		}

		fun extractCrc(data: UByteArray): UInt {
			val highByte = data[PACKAGE_SIZE - 2].toUInt()
			val lowByte = data[PACKAGE_SIZE - 1].toUInt()

			return highByte.shl(8) or lowByte
		}

		//endregion

		//region Валидация пакета

		fun checkPackage(data: UByteArray): BluetoothPackageStatus {
			return if (!isValidSize(data)) {
				BluetoothPackageStatus.INCORRECT_SIZE
			} else if (!isValidMagicByte(data)) {
				BluetoothPackageStatus.INCORRECT_MAGIC_BYTE
			} else if (!isValidCRC(data)) {
				BluetoothPackageStatus.INCORRECT_CRC
			} else {
				BluetoothPackageStatus.VALID
			}
		}

		fun isValidSize(data: UByteArray) =
			PACKAGE_SIZE == data.size

		fun isValidMagicByte(data: UByteArray) =
			START_MAGIC == data.first()

		fun isValidCRC(data: UByteArray) =
			extractCrc(data) == calculateCrc(data)

		//endregion

		//region Отправка данных

		fun request(btFront: BluetoothFront, cmd: UByte, value: UInt): UByteArray {
			buildPackage(cmd, value).let {
				btFront.sender.value = it
				return it
			}
		}

		fun requestData(btFront: BluetoothFront, cmd: UByte): UByteArray {
			buildPackage(cmd, 0u).let {
				btFront.sender.value = it
				return it
			}
		}

		//endregion
	}
}