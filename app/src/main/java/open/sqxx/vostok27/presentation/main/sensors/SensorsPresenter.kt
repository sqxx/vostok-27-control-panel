package open.sqxx.vostok27.presentation.main.sensors

import android.annotation.SuppressLint
import app.akexorcist.bluetotohspp.library.BluetoothState
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import open.sqxx.vostok27.extension.rx.Variable
import open.sqxx.vostok27.model.repository.BluetoothFront

//todo Remove hardcoded strings

@SuppressLint("CheckResult")
@ExperimentalUnsignedTypes
@InjectViewState
class SensorsPresenter(private val btFront: BluetoothFront) : MvpPresenter<SensorsView>() {

	companion object {

		private const val COMMAND_LEN = 5

		private const val MAGIC_BYTE = 0xF4.toByte()
		private const val DUMMY_BYTE = 0xFF.toByte()

		private const val PROTOCOL_STARTUP = 0x01.toByte()
		private const val PROTOCOL_INIT_COMPLETE = 0x02.toByte()
		private const val PROTOCOL_NOT_READY = 0x03.toByte()

		private const val PROTOCOL_VAL_CO2 = 0xA1.toByte()
		private const val PROTOCOL_VAL_HUM = 0xA2.toByte()
		private const val PROTOCOL_VAL_TMP = 0xA3.toByte()
		private const val PROTOCOL_VAL_PRS = 0xA4.toByte()

		val COMMANDS = byteArrayOf(
			PROTOCOL_VAL_CO2,
			PROTOCOL_VAL_HUM,
			PROTOCOL_VAL_TMP,
			PROTOCOL_VAL_PRS
		)
	}

	private val btReady = Variable(false)

	init {
		btReady.observable.subscribe {
			if (it) {
				viewState.removeMessage()
			} else {
				viewState.showMessage("Ожидание Bluetooth устройства")
			}
		}

		btFront.receiver.observable.subscribe {

			if (it.isEmpty())
				return@subscribe

			if (it.size != COMMAND_LEN) {
				try {
					viewState.showMessage(String(it))
				} catch (e: Exception) {
					return@subscribe
				}
			}

			handleData(it)
		}

		btFront.status.observable.subscribe {
			if (it == BluetoothState.STATE_CONNECTED) {
				requestAllSensorsData()
			} else {
				viewState.showMessage("Ожидание Bluetooth устройства")
			}
		}
	}

	private fun handleData(data: ByteArray) {
		if (data.first() != MAGIC_BYTE) {
			viewState.showMessage("Пакет повреждён")
			return
		}

		val crc = calculateCrc(data)
		if (data.last() != crc) {
			viewState.showMessage(
				"Несовпадает контрольная сумма\n" +
					"${data.last().toUByte()} получено, ожидается $crc"
			)
			return
		}

		/*
		 * Конвертация в UByte необходима!
		 * Иначе при конвертации в Int сохраняется знак типа Byte,
		 * что ведёт к потере оригинальных данных
		 */

		val command = data[1]

		btReady.value =
			(command != PROTOCOL_STARTUP &&
				command != PROTOCOL_INIT_COMPLETE &&
				command != PROTOCOL_NOT_READY)

		if (!btReady.value) {
			return
		}

		val lowByte = data[2].toUByte().toInt()
		val highByte = data[3].toUByte().toInt()
		val value: Int = highByte.shl(8) or lowByte

		when (command) {
			PROTOCOL_VAL_CO2 -> {
				viewState.showCO2(value)
			}
			PROTOCOL_VAL_HUM -> {
				viewState.showHumidity(value)
			}
			PROTOCOL_VAL_TMP -> {
				viewState.showTemp(value)
			}
			PROTOCOL_VAL_PRS -> {
				// Преобразуем мБары в Бары
				viewState.showPressure(value / 1000f)
			}
		}

		// Пул полностью обработан. Делаем следующие запросы
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
			DUMMY_BYTE,
			DUMMY_BYTE,
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