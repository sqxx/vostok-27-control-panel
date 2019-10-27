package open.sqxx.vostok27.ui.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_option_state.view.*
import open.sqxx.vostok27.R

class OptionStateView(
	context: Context,
	private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

	private var layout: View = LayoutInflater
		.from(context)
		.inflate(R.layout.view_option_state, this, true)

	private fun collectAttributes() {
		val attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionStateView)

		try {
			setLabel(attributes.getString(R.styleable.OptionStateView_label)!!)
			setDescription(attributes.getString(R.styleable.OptionStateView_description)!!)
			setState(attributes.getBoolean(R.styleable.OptionStateView_switcher_state, false))
		} finally {
			attributes.recycle()
		}
	}

	private fun setLabel(label: String) {
		layout.switcher.text = label
	}

	private fun setDescription(description: String) {
		layout.description.text = description
	}

	fun setState(state: Boolean) {
		layout.switcher.isChecked = state
	}
}