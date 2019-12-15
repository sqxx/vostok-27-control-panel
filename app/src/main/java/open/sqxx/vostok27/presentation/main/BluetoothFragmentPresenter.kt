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
	protected var isViewInReality = false

	init {
		btFront.receiver.observable.subscribe {
			if (it.isEmpty())
				return@subscribe

			handleData(it)
		}

		btFront.status.observable.subscribe {
			if (it != BluetoothStatus.CONNECTED) {
				isBluetoothConnected = false
				return@subscribe
			}
			onBluetoothConnected()
		}
	}

	protected open fun onBluetoothConnected() {
		isBluetoothConnected = true
	}

	open fun onAttachViewToReality() {
		isViewInReality = true
	}

	open fun onDetachViewFromReality() {
		isViewInReality = false
	}

	protected open fun handleData(data: UByteArray): Boolean {
		BluetoothModel.let {

			// Обработка команды сброса
			// После отправки reset будут ещё некоторое время приходить битые пакеты, игнорируем их
			it.handleReset(data)
			if (it.isResetRequested())
				return false

			// Проверка пакета
			when (val status = it.checkPackage(data)) {
				BluetoothPackageStatus.INCORRECT_SIZE       -> {
					it.handleIncorrectSize(btFront, status)
					handleIncorrectSize(data)
					return false
				}
				BluetoothPackageStatus.INCORRECT_MAGIC_BYTE -> {
					it.handleMagicByteError(btFront, status)
					handleIncorrectMagicByte(data)
					return false
				}
				BluetoothPackageStatus.INCORRECT_CRC        -> {
					it.handleCRCError(btFront, status)
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

	protected open fun handleIncorrectSize(data: UByteArray) {
		Timber.e("Повреждённый пакет")
	}

	protected open fun handleIncorrectMagicByte(data: UByteArray) {
		Timber.e("Несовпадение магического числа")
	}

	protected open fun handleIncorrectCRC(data: UByteArray) {
		Timber.e("Несовпадение контрольной суммы")
	}
}