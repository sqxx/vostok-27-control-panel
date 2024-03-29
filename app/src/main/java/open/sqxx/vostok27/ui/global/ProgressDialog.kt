package open.sqxx.vostok27.ui.global

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import open.sqxx.vostok27.R

class ProgressDialog : DialogFragment() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NO_FRAME, R.style.ProgressDialogTheme)
		isCancelable = false
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	) =
		LayoutInflater.from(context).inflate(R.layout.fragment_progress, null)
}