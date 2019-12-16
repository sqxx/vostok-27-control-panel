package open.sqxx.vostok27.presentation.main

import android.annotation.SuppressLint
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import open.sqxx.vostok27.model.repository.*
import timber.log.Timber

@ExperimentalUnsignedTypes
@SuppressLint("CheckResult")
abstract class BluetoothFragmentPresenter<T : MvpView>(val btFront: BluetoothFront) :
	MvpPresenter<T>() {

	protected var isBluetoothConnected = false
	protected var isFragmentInReality = false

	init {
		btFront.receiver.observable.subscribe {
			if (it.isEmpty())
				return@subscribe

			handleData(it)
		}

		btFront.status.observable.subscribe {
			if (it != BluetoothStatus.CONNECTED) {
				onBluetoothDisconnected()
				return@subscribe
			}
			onBluetoothConnected()
		}
	}

	//region События

	protected open fun onBluetoothConnected() {
		isBluetoothConnected = true
	}

	protected open fun onBluetoothDisconnected() {
		isBluetoothConnected = false
	}

	open fun onAttachFragmentToReality() {
		isFragmentInReality = true
	}

	open fun onDetachFragmentFromReality() {
		isFragmentInReality = false
	}

	//endregion

	protected open fun handleData(data: UByteArray): Boolean {
		BluetoothModel.let {

			// Обработка команды сброса
			it.handleReset(data) { d -> validateReset(d) }
			if (it.isResetRequested()) {
				it.resetBluetoothPull(btFront) { bt -> actionAfterReset(bt) }
				return false
			}

			// Проверка пакета
			when (it.checkPackage(data)) {
				BluetoothPackageStatus.INCORRECT_SIZE       -> {
					it.handleIncorrectSize(btFront) { bt -> actionAfterReset(bt) }
					handleIncorrectSize(data)
					return false
				}
				BluetoothPackageStatus.INCORRECT_MAGIC_BYTE -> {
					it.handleIncorrectMagicByte(btFront) { bt -> actionAfterReset(bt) }
					handleIncorrectMagicByte(data)
					return false
				}
				BluetoothPackageStatus.INCORRECT_CRC        -> {
					it.handleIncorrectCRC(btFront) { bt -> actionAfterReset(bt) }
					handleIncorrectCRC(data)
					return false
				}
				BluetoothPackageStatus.VALID                -> {
					// nothing to do
				}
			}

		}

		return true
	}

	//region Обработка команды reset

	protected open fun validateReset(data: UByteArray): Boolean = true

	protected open fun actionAfterReset(btFront: BluetoothFront) {}

	//endregion

	//region Обработка исключений

	protected open fun handleIncorrectSize(data: UByteArray) {
		Timber.e("Повреждённый пакет")
	}

	protected open fun handleIncorrectMagicByte(data: UByteArray) {
		Timber.e("Несовпадение магического числа")
	}

	protected open fun handleIncorrectCRC(data: UByteArray) {
		Timber.e("Несовпадение контрольной суммы")
	}

	//endregion
}