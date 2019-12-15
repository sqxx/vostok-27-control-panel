package open.sqxx.vostok27.presentation.main.sensors

import com.arellomobile.mvp.InjectViewState
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.presentation.main.BluetoothFragmentPresenter

@ExperimentalUnsignedTypes
@InjectViewState
class SensorsPresenter(btFront: BluetoothFront) :
	BluetoothFragmentPresenter<SensorsView>(btFront) {

	override fun handleData(data: UByteArray): Boolean {
		if (!super.handleData(data)) return false

		BluetoothModel.let {

			val command = data[1]
			val value = it.extractValue(data)

			when (command) {
				it._P_REQ_CO2         -> {
					viewState.showCO2(value.toInt())
				}
				it._P_REQ_HUM         -> {
					viewState.showHumidity(value.toInt())
				}
				it._P_REQ_TEMP        -> {
					viewState.showTemp(value.toInt())
				}
				it._P_REQ_PRES        -> {
					// Преобразуем мБары в Бары
					viewState.showPressure(value.toFloat() / 1000f)
				}
				it._P_BATTERY_VOLTAGE -> {
					viewState.showVoltage(value.toFloat() / 100f)
				}
				it._P_ENERGY_USAGE    -> {
					viewState.showEnergyUsage(value.toFloat() / 100f)
				}
				it._P_ENERGY_GEN      -> {
					viewState.showEnergyGen(value.toInt())
				}
			}

			// Пул полностью обработан
			if (command == it.VALUES_COMMANDS.last())
				it.requestAllSensorsData(btFront)
		}

		return true
	}
}