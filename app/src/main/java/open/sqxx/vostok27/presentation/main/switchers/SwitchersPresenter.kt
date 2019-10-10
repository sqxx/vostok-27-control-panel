package open.sqxx.vostok27.presentation.main.switchers

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import open.sqxx.vostok27.model.repository.BluetoothFront

@InjectViewState
class SwitchersPresenter(private val btFront: BluetoothFront) : MvpPresenter<SwitchersView>()