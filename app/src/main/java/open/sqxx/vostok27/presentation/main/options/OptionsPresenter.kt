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
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion.extractCommand
import open.sqxx.vostok27.presentation.main.BluetoothFragmentPresenter

@ExperimentalUnsignedTypes
@InjectViewState
class OptionsPresenter(btFront: BluetoothFront) :
	BluetoothFragmentPresenter<OptionsView>(btFront) {

	companion object {
		private val GET_TIME_COMMANDS = ubyteArrayOf(
			_P_GET_TIME,
			_P_GET_DAY_TIME,
			_P_GET_NIGHT_TIME
		)
	}

	private var latestPackage: UByteArray = ubyteArrayOf()

	init {
		viewState.initialize()
	}

	private fun requestTime() =
		GET_TIME_COMMANDS.forEach { requestValue(it) }

	//region Обработка команды reset

	override fun actionAfterReset(btFront: BluetoothFront) {
		super.actionAfterReset(btFront)

		// Если сбой произошёл при опросе состояния систем...
		if (extractCommand(latestPackage) in GET_TIME_COMMANDS)
			requestTime()

		// В противном случае повторяем прошлый пакет вновь
		else
			btFront.sender.value = latestPackage
	}

	override fun validateReset(data: UByteArray): Boolean {
		val command = extractCommand(data)

		return if (command == GET_TIME_COMMANDS[0])
			true
		else extractCommand(latestPackage) == command
	}

	//endregion

	override fun onBluetoothConnected() {
		super.onBluetoothConnected()
		requestTime()
	}

	override fun onAttachFragmentToReality() {
		super.onAttachFragmentToReality()
		if (isBluetoothConnected) {
			requestTime()
		}
	}

	override fun handleData(data: UByteArray): Boolean {
		if (!super.handleData(data)) return false

		BluetoothModel.let {

			val command = it.extractCommand(data)
			val value = it.extractValue(data).toInt()

			val minute = (value / 60 % 60)
			val hour = ((value - (minute * 60)) % 86400 / 3600)

			when (command) {
				_P_GET_TIME       -> viewState.updateCurrentTime(hour, minute)
				_P_GET_DAY_TIME   -> viewState.updateDayTime(hour, minute)
				_P_GET_NIGHT_TIME -> viewState.updateNightTime(hour, minute)
			}

			if (command == GET_TIME_COMMANDS[0] && isFragmentInReality)
				requestTime()
		}

		return true
	}

	fun setCurrentTime(hour: UInt, minute: UInt) =
		setValue(_P_SET_TIME, hour, minute)

	fun setDayTime(hour: UInt, minute: UInt) =
		setValue(_P_SET_DAY_TIME, hour, minute)

	fun setNightTime(hour: UInt, minute: UInt) =
		setValue(_P_SET_NIGHT_TIME, hour, minute)

	private fun setValue(command: UByte, hour: UInt, minute: UInt) {
		val timestamp = (hour * 60u * 60u) + (minute * 60u)
		latestPackage = BluetoothModel.request(btFront, command, timestamp)
	}

	private fun requestValue(command: UByte) {
		latestPackage = BluetoothModel.requestData(btFront, command)
	}
}