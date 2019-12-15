package open.sqxx.vostok27.presentation.main.switchers

import com.arellomobile.mvp.InjectViewState
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_GET_LIGHT_LEVEL
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SET_LIGHT_LEVEL
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_AUTO_LIGHT
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_PRES_RELIEF_VALVE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_PROD_CO2
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_PUMP_VALVE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_AUTO_LIGHT
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_PRES_RELIEF_VALVE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_PROD_CO2
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_PUMP_VALVE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SYSTEM_DISABLED
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SYSTEM_ENABLED
import open.sqxx.vostok27.presentation.main.BluetoothFragmentPresenter

@ExperimentalUnsignedTypes
@InjectViewState
class SwitchersPresenter(btFront: BluetoothFront) :
	BluetoothFragmentPresenter<SwitchersView>(btFront) {

	init {
		viewState.initialize()
	}

	override fun onBluetoothConnected() {
		requestPressureReliefValveState()
		requestPumpValveState()
		requestProdCO2State()
		requestAutoLightState()
	}

	override fun handleData(data: UByteArray): Boolean {
		if (!super.handleData(data)) return false

		BluetoothModel.let {

			val command = data[1]
			val value = it.extractValue(data)

			val isEnabled = (value.toUByte() == _P_SYSTEM_ENABLED)

			when (command) {
				_P_SWITCH_PRES_RELIEF_VALVE, _P_STATUS_PRES_RELIEF_VALVE -> {
					viewState.updatePressureReliefValveState(isEnabled)
				}
				_P_SWITCH_PUMP_VALVE, _P_STATUS_PUMP_VALVE               -> {
					viewState.updatePumpValveState(isEnabled)
				}
				_P_SWITCH_PROD_CO2, _P_STATUS_PROD_CO2                   -> {
					viewState.updateProdCO2State(isEnabled)
				}
				_P_SWITCH_AUTO_LIGHT, _P_STATUS_AUTO_LIGHT               -> {
					viewState.updateAutoLightState(isEnabled)
				}
				_P_GET_LIGHT_LEVEL                                       -> {
					viewState.updateLightLevel(value.toInt())
				}
			}
		}

		return true
	}

	fun setPressureReliefValveState(isEnabled: Boolean) =
		setState(_P_SWITCH_PRES_RELIEF_VALVE, isEnabled)

	private fun requestPressureReliefValveState() =
		requestState(_P_STATUS_PRES_RELIEF_VALVE)

	fun setPumpValveState(isEnabled: Boolean) =
		setState(_P_SWITCH_PUMP_VALVE, isEnabled)

	private fun requestPumpValveState() =
		requestState(_P_STATUS_PUMP_VALVE)

	fun setProdCO2State(isEnabled: Boolean) =
		setState(_P_SWITCH_PROD_CO2, isEnabled)

	private fun requestProdCO2State() =
		requestState(_P_STATUS_PROD_CO2)

	fun setAutoLightState(isEnabled: Boolean) =
		setState(_P_SWITCH_AUTO_LIGHT, isEnabled)

	private fun requestAutoLightState() =
		BluetoothModel.requestData(btFront, _P_STATUS_AUTO_LIGHT)

	fun getLightLevel() =
		requestState(_P_GET_LIGHT_LEVEL)

	fun setLightLevel(percent: UInt) {
		BluetoothModel.request(
			btFront,
			_P_SET_LIGHT_LEVEL,
			percent
		)
	}

	private fun setState(command: UByte, isEnabled: Boolean) {
		BluetoothModel.request(
			btFront,
			command,
			if (isEnabled)
				_P_SYSTEM_ENABLED.toUInt()
			else
				_P_SYSTEM_DISABLED.toUInt()
		)
	}

	private fun requestState(command: UByte) =
		BluetoothModel.requestData(btFront, command)
}