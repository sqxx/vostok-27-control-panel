package open.sqxx.vostok27.presentation.main.sensors

import android.annotation.SuppressLint
import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel

@SuppressLint("CheckResult")
@ExperimentalUnsignedTypes
@InjectViewState
class SensorsPresenter(private val btFront: BluetoothFront) : MvpPresenter<SensorsView>() {

	init {
		btFront.receiver.observable.subscribe {
			if (it.isEmpty())
				return@subscribe

			if (it.size != BluetoothModel.PACKAGE_SIZE) {
				try {
					viewState.showMessage(String(it))
				} catch (e: Exception) {
					return@subscribe
				}
			}

			handleData(it)
		}

		btFront.status.observable.subscribe {
			requestAllSensorsData()
		}
	}

	private fun handleData(data: ByteArray) {
		if (data.first() != BluetoothModel.MAGIC_BYTE) {
			viewState.showMessage("Пакет повреждён")
			return
		}

		val givenCRC = BluetoothModel.extractCrc(data)
		val calcCRC = BluetoothModel.calculateCrc(data)

		if (givenCRC != calcCRC) {
			viewState.showMessage(
				"Несовпадает контрольная сумма\n" +
					"$givenCRC получено, ожидается $calcCRC"
			)
			return
		}

		val command = data[1]
		val value = BluetoothModel.extractValue(data)

		when (command) {
			BluetoothModel._P_REQ_CO2 -> {
				viewState.showCO2(value)
			}
			BluetoothModel._P_REQ_HUM -> {
				viewState.showHumidity(value)
			}
			BluetoothModel._P_REQ_TEMP -> {
				viewState.showTemp(value)
			}
			BluetoothModel._P_REQ_PRES -> {
				// Преобразуем мБары в Бары
				viewState.showPressure(value / 1000f)
			}
		}

		// Пул полностью обработан. Делаем следующие запросы
		if (command == BluetoothModel.VALUES_COMMANDS.last()) {
			requestAllSensorsData()
		}
	}

	private fun requestAllSensorsData() {
		BluetoothModel.VALUES_COMMANDS.forEach {
			BluetoothModel.requestData(btFront, it)
		}
	}
}