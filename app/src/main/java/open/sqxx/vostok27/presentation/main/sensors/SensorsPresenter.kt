package open.sqxx.vostok27.presentation.main.sensors

import com.arellomobile.mvp.InjectViewState
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion.extractCommand
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion.requestData
import open.sqxx.vostok27.presentation.main.BluetoothFragmentPresenter

@ExperimentalUnsignedTypes
@InjectViewState
class SensorsPresenter(btFront: BluetoothFront) :
	BluetoothFragmentPresenter<SensorsView>(btFront) {

	companion object {
		private val VALUES_COMMANDS = ubyteArrayOf(
			BluetoothModel._P_REQ_CO2,
			BluetoothModel._P_REQ_HUM,
			BluetoothModel._P_REQ_TEMP,
			BluetoothModel._P_REQ_PRES,
			BluetoothModel._P_BATTERY_VOLTAGE,
			BluetoothModel._P_ENERGY_USAGE,
			BluetoothModel._P_ENERGY_GEN
		)
	}

	private fun requestAllSensorsData(btFront: BluetoothFront) =
		VALUES_COMMANDS.forEach { requestData(btFront, it) }

	//region Обработка команды reset

	override fun actionAfterReset(btFront: BluetoothFront) {
		super.actionAfterReset(btFront)
		requestAllSensorsData(btFront)
	}

	override fun validateReset(data: UByteArray): Boolean {
		return extractCommand(data) == VALUES_COMMANDS[0]
	}

	//endregion

	override fun onBluetoothConnected() {
		super.onBluetoothConnected()
		requestAllSensorsData(btFront)
	}

	override fun onAttachFragmentToReality() {
		super.onAttachFragmentToReality()
		if (isBluetoothConnected) {
			requestAllSensorsData(btFront)
		}
	}

	override fun handleData(data: UByteArray): Boolean {
		if (!super.handleData(data)) return false

		BluetoothModel.let {

			val command = it.extractCommand(data)
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
					viewState.showVoltage(value.toInt())
				}
				it._P_ENERGY_USAGE    -> {
					viewState.showEnergyUsage(value.toInt())
				}
				it._P_ENERGY_GEN      -> {
					viewState.showEnergyGen(value.toInt())
				}
			}

			// Пул полностью обработан и фрагмент присутствует на экране
			if (command == VALUES_COMMANDS.last() && isFragmentInReality)
				requestAllSensorsData(btFront)
		}

		return true
	}
}