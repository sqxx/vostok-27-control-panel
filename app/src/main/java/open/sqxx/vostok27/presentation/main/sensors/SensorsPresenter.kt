package open.sqxx.vostok27.presentation.main.sensors

import android.annotation.SuppressLint
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import open.sqxx.vostok27.model.repository.BluetoothFront

@ExperimentalUnsignedTypes
@SuppressLint("CheckResult")
@InjectViewState
class SensorsPresenter(val btFront: BluetoothFront) : MvpPresenter<SensorsView>() {

	companion object {
		private const val COMMAND_LEN = 5
		private const val MAGIC_BYTE: Byte = 0xF4.toByte()
		private const val REQST_BYTE: Byte = 0xFF.toByte()

		private const val CMD_CO2_VALUE: Byte = 0xA1.toByte()
		private const val CMD_HUM_VALUE: Byte = 0xA2.toByte()
		private const val CMD_TEMP_VALUE: Byte = 0xA3.toByte()

		val COMMANDS = byteArrayOf(
			CMD_CO2_VALUE,
			CMD_HUM_VALUE,
			CMD_TEMP_VALUE
		)
	}

	init {
		btFront.receiver.observable.subscribe {
			if (it.size != COMMAND_LEN) return@subscribe

			//todo Handle error messages from arduino
			handleData(it)
		}

		btFront.status.observable.subscribe {
			if (it == BluetoothState.STATE_CONNECTED) {
				requestAllSensorsData()
			}
		}
	}

	private fun handleData(data: ByteArray) {
		if (data.first() != MAGIC_BYTE) {
			//todo handle magic byte error
			return
		}

		if (data.last() != calculateCrc(data)) {
			//todo handle crc error
			return
		}

		/*
		 * Конвертация в UByte необходима!
		 * Иначе при конвертации в Int сохраняется знак типа Byte,
		 * что ведёт к потере оригинальных данных
		 */

		val command = data[1]

		val lowByte = data[2].toUByte().toInt()
		val highByte = data[3].toUByte().toInt()
		val value: Int = highByte.shl(8) or lowByte

		when (command) {
			CMD_CO2_VALUE -> {
				viewState.showCO2(value)
			}
			CMD_HUM_VALUE -> {
				viewState.showHumidity(value)
			}
			CMD_TEMP_VALUE -> {
				viewState.showTemp(value)
			}
			else -> {
				//todo handle unknown command answer
			}
		}

		// Пул полностью реализован. Делаем следующие запросы
		if (command == COMMANDS.last()) {
			requestAllSensorsData()
		}
	}

	private fun requestAllSensorsData() {
		COMMANDS.forEach {
			requestData(it)
		}
	}

	private fun requestData(command: Byte) {
		val d = byteArrayOf(
			MAGIC_BYTE,
			command,
			REQST_BYTE,
			REQST_BYTE,
			0x00
		)

		d[COMMAND_LEN - 1] = calculateCrc(d)

		btFront.sender.value = d
	}

	private fun calculateCrc(cmd: ByteArray): Byte {
		var crc: Byte = 0x00

		for (i in 1..cmd.size - 2) {
			crc = crc.plus(cmd[i]).toByte()
		}

		return crc
	}
}