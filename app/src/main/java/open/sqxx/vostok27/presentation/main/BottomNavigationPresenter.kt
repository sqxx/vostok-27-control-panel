package open.sqxx.vostok27.presentation.main

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter

import open.sqxx.vostok27.ui.main.view.BottomNavigationView
import ru.terrakok.cicerone.Router

@InjectViewState
class BottomNavigationPresenter(private val router: Router) : MvpPresenter<BottomNavigationView>() {

	fun onBackPressed() {
		router.exit()
	}
}
