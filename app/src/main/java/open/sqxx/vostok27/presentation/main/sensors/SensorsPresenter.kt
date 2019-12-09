package open.sqxx.vostok27.presentation.main.sensors

import com.arellomobile.mvp.InjectViewState
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.presentation.main.BluetoothFragmentPresenter

@ExperimentalUnsignedTypes
@InjectViewState
class SensorsPresenter(btFront: BluetoothFront) :
	BluetoothFragmentPresenter<SensorsView>(btFront) {

	override fun handleData(data: UByteArray) {
		BluetoothModel.let {

			val command = data[1]

			// Обработка команды сброса
			// После отправки reset будут ещё некоторое время приходить битые пакеты
			it.handleReset(btFront, data)
			if (it.isResetRequested())
				return

			val status = it.checkPackage(data)

			// Обработка несоответствия магического числа
			if (it.handleMagicByteError(btFront, status)) {
				viewState.showMessage("Несовпадение магического числа")
				return
			}

			// Обработка несоответствия контрольной суммы
			if (it.handleCRCError(btFront, status)) {
				viewState.showMessage("Несовпадение контрольной суммы")
				return
			}

			// Обработка исключений по протоколу
			if (it.handleProtocolExceptions(btFront, data)) {
				viewState.showMessage("Ошибки на стороне slave")
				return
			}

			// Обработка ответов
			val value = it.extractValue(data)
			when (command) {
				it._P_REQ_CO2 -> {
					viewState.showCO2(value.toInt())
				}
				it._P_REQ_HUM -> {
					viewState.showHumidity(value.toInt())
				}
				it._P_REQ_TEMP -> {
					viewState.showTemp(value.toInt())
				}
				it._P_REQ_PRES -> {
					// Преобразуем мБары в Бары
					viewState.showPressure(value.toFloat() / 1000f)
				}
			}

			// Пул полностью обработан
			if (command == it.VALUES_COMMANDS.last())
				it.requestAllSensorsData(btFront)
		}
	}
}