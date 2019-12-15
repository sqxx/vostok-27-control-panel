package open.sqxx.vostok27.presentation.main.options

import com.arellomobile.mvp.MvpView

interface OptionsView : MvpView {
	fun initialize()

	fun updateCurrentTime(hour: Int, minute: Int)
	fun updateDayTime(hour: Int, minute: Int)
	fun updateNightTime(hour: Int, minute: Int)
}