package open.sqxx.vostok27.presentation.main

import android.annotation.SuppressLint
import com.arellomobile.mvp.MvpPresenter
import com.arellomobile.mvp.MvpView
import open.sqxx.vostok27.model.repository.BluetoothFront
import open.sqxx.vostok27.model.repository.BluetoothModel
import open.sqxx.vostok27.model.repository.BluetoothStatus

@ExperimentalUnsignedTypes
@SuppressLint("CheckResult")
abstract class BluetoothFragmentPresenter<T : MvpView>(val btFront: BluetoothFront) :
	MvpPresenter<T>() {

	init {
		btFront.receiver.observable.subscribe {
			if (it.isEmpty())
				return@subscribe

			handleData(it)
		}

		btFront.status.observable.subscribe {
			if (it != BluetoothStatus.CONNECTED) return@subscribe
			BluetoothModel.requestAllSensorsData(btFront)
		}
	}

	abstract fun handleData(data: UByteArray)
}