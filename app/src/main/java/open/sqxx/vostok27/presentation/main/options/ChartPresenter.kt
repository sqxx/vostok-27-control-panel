package open.sqxx.vostok27.presentation.main.options

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import open.sqxx.vostok27.model.repository.BluetoothFront

@InjectViewState
class ChartPresenter(private val btFront: BluetoothFront) : MvpPresenter<ChartView>()