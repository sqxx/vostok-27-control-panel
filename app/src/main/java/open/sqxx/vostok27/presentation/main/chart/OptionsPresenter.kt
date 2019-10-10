package open.sqxx.vostok27.presentation.main.chart

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import open.sqxx.vostok27.model.repository.BluetoothFront

@InjectViewState
class OptionsPresenter(private val btFront: BluetoothFront) : MvpPresenter<OptionsView>()