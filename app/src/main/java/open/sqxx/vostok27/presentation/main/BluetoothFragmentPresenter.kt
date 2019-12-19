package open.sqxx.vostok27.presentation.main

import android.annotation.SuppressLint
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import open.sqxx.vostok27.model.repository.*
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._PE_PACKAGE_CRC
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._PE_PACKAGE_ERROR
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._PE_UNKNOWN_CMD
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_INIT_COMPLETE
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_NOT_READY
import open.sqxx.vostok27.model.repository.BluetoothModel.Companion._P_STARTUP
import timber.log.Timber

@ExperimentalUnsignedTypes
@SuppressLint("CheckResult")
abstract class BluetoothFragmentPresenter<T : MvpView>(val btFront: BluetoothFront) :
	MvpPresenter<T>() {

	protected val isBluetoothConnected: Boolean
		get() {
			val status = btFront.status.value
			return status == BluetoothStatus.CONNECTED || status == BluetoothStatus.READY
		}

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

	protected open fun onBluetoothConnected() {}

	protected open fun onBluetoothDisconnected() {}

	open fun onAttachFragmentToReality() {
		isFragmentInReality = true
	}

	open fun onDetachFragmentFromReality() {
		isFragmentInReality = false
	}

	//endregion

	protected open fun handleData(data: UByteArray): Boolean {
		var result: Boolean
		BluetoothModel.let {

			// Проверка валидности пакета
			result = checkPackage(data)
			if (!result) return false

			// Проверка исключений по протоколу
			result = checkProtocolExceptions(data)
			if (!result) return false

			// Проверка статус кодов по протоколу
			result = checkProtocolStatuses(data)
			if (!result) return false

			return true
		}
	}

	private fun checkPackage(data: UByteArray): Boolean {
		BluetoothModel.let {
			return when (it.checkPackage(data)) {
				BluetoothPackageStatus.INCORRECT_SIZE       -> {
					handleIncorrectSize(data)
					false
				}
				BluetoothPackageStatus.INCORRECT_MAGIC_BYTE -> {
					handleIncorrectMagicByte(data)
					false
				}
				BluetoothPackageStatus.INCORRECT_CRC        -> {
					handleIncorrectCRC(data)
					false
				}
				BluetoothPackageStatus.VALID                -> {
					true
				}
			}
		}
	}

	private fun checkProtocolExceptions(data: UByteArray): Boolean {
		BluetoothModel.let {
			return when (val command = it.extractCommand(data)) {
				_PE_PACKAGE_ERROR,
				_PE_PACKAGE_CRC -> {
					Timber.e("Исключение на стороне slave")
					false
				}
				_PE_UNKNOWN_CMD -> {
					Timber.e("Unimplemented ${Integer.toHexString(command.toInt())}")
					false
				}
				else            ->
					true
			}
		}
	}

	private fun checkProtocolStatuses(data: UByteArray): Boolean {
		BluetoothModel.let {
			return when (it.extractCommand(data)) {
				_P_STARTUP,
				_P_INIT_COMPLETE,
				_P_NOT_READY -> {
					Timber.d("Станция ещё не готова")
					false
				}
				else         ->
					true
			}
		}
	}

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