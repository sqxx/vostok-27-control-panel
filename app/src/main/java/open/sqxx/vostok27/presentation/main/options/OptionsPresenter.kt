package open.sqxx.vostok27.presentation.main.options

import com.arellomobile.mvp.InjectViewState
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_GET_DAY_TIME
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_GET_NIGHT_TIME
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_GET_TIME
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SET_DAY_TIME
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SET_NIGHT_TIME
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_SET_TIME
import open.sqxx.vostok27.presentation.main.BluetoothFragmentPresenter

@ExperimentalUnsignedTypes
@InjectViewState
class OptionsPresenter(btFront: BluetoothFront) :
	BluetoothFragmentPresenter<OptionsView>(btFront) {

	init {
		viewState.initialize()
	}

	private fun requestTime() {
		requestCurrentTime()
		requestDayTime()
		requestNightTime()
	}

	override fun onBluetoothConnected() {
		super.onBluetoothConnected()
		requestTime()
	}

	override fun onAttachViewToReality() {
		super.onAttachViewToReality()
		requestTime()
	}

	override fun handleData(data: UByteArray): Boolean {
		if (!super.handleData(data)) return false

		BluetoothModel.let {

			val command = data[1]
			val value = it.extractValue(data).toInt()

			val minute = (value / 60 % 60)
			val hour = ((value - (minute * 60)) % 86400 / 3600)

			when (command) {
				_P_GET_TIME       -> viewState.updateCurrentTime(hour, minute)
				_P_GET_DAY_TIME   -> viewState.updateDayTime(hour, minute)
				_P_GET_NIGHT_TIME -> viewState.updateNightTime(hour, minute)
			}

			if (command == _P_GET_NIGHT_TIME) {
				requestTime()
			}
		}

		return true
	}

	fun setCurrentTime(hour: UInt, minute: UInt) =
		setValue(_P_SET_TIME, hour, minute)

	private fun requestCurrentTime() =
		requestValue(_P_GET_TIME)

	fun setDayTime(hour: UInt, minute: UInt) =
		setValue(_P_SET_DAY_TIME, hour, minute)

	private fun requestDayTime() =
		requestValue(_P_GET_DAY_TIME)

	fun setNightTime(hour: UInt, minute: UInt) =
		setValue(_P_SET_NIGHT_TIME, hour, minute)

	private fun requestNightTime() =
		requestValue(_P_GET_NIGHT_TIME)

	private fun setValue(command: UByte, hour: UInt, minute: UInt) {
		val timestamp = (hour * 60u * 60u) + (minute * 60u)
		BluetoothModel.request(btFront, command, timestamp)
	}

	private fun requestValue(command: UByte) =
		BluetoothModel.requestData(btFront, command)
}