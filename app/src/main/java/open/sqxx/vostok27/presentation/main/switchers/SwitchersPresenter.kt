package open.sqxx.vostok27.presentation.main.switchers

import com.arellomobile.mvp.InjectViewState
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_GET_LIGHT_LEVEL
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SET_LIGHT_LEVEL
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_AUTO_LIGHT
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_AUTO_PRES
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_CAMERAS
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_CO2_NUTRALIZATION
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_FAN
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_HEAT_MODULE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_PRES_RELIEF_VALVE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_PROD_CO2
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STATUS_PUMP_VALVE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_AUTO_LIGHT
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_AUTO_PRES
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_CAMERAS
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_CO2_NUTRALIZATION
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_FAN
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SWITCH_HEAT_MODULE
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

	companion object {
		private val SWITCHERS_STATUS_COMMANDS = ubyteArrayOf(
			_P_STATUS_CAMERAS,

			_P_STATUS_PRES_RELIEF_VALVE,
			_P_STATUS_PUMP_VALVE,
			_P_STATUS_AUTO_PRES,

			_P_STATUS_PROD_CO2,
			_P_STATUS_CO2_NUTRALIZATION,
			_P_STATUS_FAN,
			_P_STATUS_HEAT_MODULE,

			_P_STATUS_AUTO_LIGHT,
			_P_GET_LIGHT_LEVEL
		)
	}

	private var latestPackage: UByteArray? = null

	init {
		viewState.initialize()
	}

	fun requestValues() =
		SWITCHERS_STATUS_COMMANDS.forEach { requestState(it) }

	override fun onBluetoothConnected() {
		requestValues()
	}

	override fun onAttachFragmentToReality() {
		super.onAttachFragmentToReality()
		if (isBluetoothConnected) {
			requestValues()
		}
	}

	override fun handleData(data: UByteArray): Boolean {
		if (!super.handleData(data)) return false

		BluetoothModel.let {

			val command = it.extractCommand(data)
			val value = it.extractValue(data)

			val isEnabled = (value.toUByte() != _P_SYSTEM_DISABLED)

			when (command) {
				_P_STATUS_CAMERAS           -> {
					viewState.updateCamerasState(isEnabled)
				}
				_P_STATUS_AUTO_PRES         -> {
					viewState.updateAutoPresState(isEnabled)
				}
				_P_STATUS_PRES_RELIEF_VALVE -> {
					viewState.updatePressureReliefValveState(isEnabled)
				}
				_P_STATUS_PUMP_VALVE        -> {
					viewState.updatePumpValveState(isEnabled)
				}
				_P_STATUS_PROD_CO2          -> {
					viewState.updateProdCO2State(isEnabled)
				}
				_P_STATUS_CO2_NUTRALIZATION -> {
					viewState.updateNeutCO2State(isEnabled)
				}
				_P_STATUS_FAN               -> {
					viewState.updateFanState(isEnabled)
				}
				_P_STATUS_HEAT_MODULE       -> {
					viewState.updateHeatState(isEnabled)
				}
				_P_STATUS_AUTO_LIGHT        -> {
					viewState.updateAutoLightState(isEnabled)
				}
				_P_GET_LIGHT_LEVEL          -> {
					viewState.updateLightLevel(value.toInt())
				}
			}
		}

		return true
	}

	fun setCamerasState(isEnabled: Boolean) =
		setState(_P_SWITCH_CAMERAS, isEnabled)

	fun setAutoPressureState(isEnabled: Boolean) =
		setState(_P_SWITCH_AUTO_PRES, isEnabled)

	fun setPressureReliefValveState(isEnabled: Boolean) =
		setState(_P_SWITCH_PRES_RELIEF_VALVE, isEnabled)

	fun setPumpValveState(isEnabled: Boolean) =
		setState(_P_SWITCH_PUMP_VALVE, isEnabled)

	fun setProdCO2State(isEnabled: Boolean) =
		setState(_P_SWITCH_PROD_CO2, isEnabled)

	fun setNeutCO2State(isEnabled: Boolean) =
		setState(_P_SWITCH_CO2_NUTRALIZATION, isEnabled)

	fun setFanState(isEnabled: Boolean) =
		setState(_P_SWITCH_FAN, isEnabled)

	fun setHeatState(isEnabled: Boolean) =
		setState(_P_SWITCH_HEAT_MODULE, isEnabled)

	fun setAutoLightState(isEnabled: Boolean) =
		setState(_P_SWITCH_AUTO_LIGHT, isEnabled)

	fun setLightLevel(percent: UInt) {
		BluetoothModel.request(
			btFront,
			_P_SET_LIGHT_LEVEL,
			percent
		)
	}

	private fun setState(command: UByte, isEnabled: Boolean) {
		latestPackage = BluetoothModel.request(
			btFront,
			command,
			if (isEnabled) _P_SYSTEM_ENABLED.toUInt()
			else _P_SYSTEM_DISABLED.toUInt()
		)
	}

	private fun requestState(command: UByte) {
		latestPackage = BluetoothModel.requestData(btFront, command)
	}
}