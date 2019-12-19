package open.sqxx.vostok27.ui.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_option_switcher.view.*
import open.sqxx.vostok27.R

class OptionSwitcherView(
	context: Context,
	private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

	private var layout: View = LayoutInflater
		.from(context)
		.inflate(R.layout.view_option_switcher, this, true)

	val label: String
		get() = layout.switcher.text.toString()

	var onToggleListener: ((isChecked: Boolean) -> Unit)? = null

	private var onCheckedChangeListener =
		CompoundButton.OnCheckedChangeListener { _, isChecked: Boolean ->
			onToggleListener?.invoke(isChecked)
		}

	init {
		bindListeners()
		collectAttributes()
	}

	private fun bindListeners() {
		layout.switcher.setOnCheckedChangeListener(onCheckedChangeListener)
	}

	private fun collectAttributes() {
		val attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionSwitcherView)

		try {
			updateLabel(attributes.getString(R.styleable.OptionSwitcherView_label)!!)
			updateDescription(attributes.getString(R.styleable.OptionSwitcherView_description))
			updateCheckedState(
				attributes.getBoolean(
					R.styleable.OptionSwitcherView_switcher_state,
					false
				)
			)
		} finally {
			attributes.recycle()
		}
	}

	fun updateLabel(label: String) {
		layout.switcher.text = label
	}

	fun updateDescription(description: String?) {
		if (description.isNullOrEmpty()) {
			layout.description.visibility = View.GONE
			return
		}

		layout.description.text = description
	}

	fun updateCheckedState(isChecked: Boolean) {
		layout.switcher.setOnCheckedChangeListener(null)
		layout.switcher.isChecked = isChecked
		bindListeners()
	}

	fun updateState(isEnabled: Boolean) {
		layout.switcher.isEnabled = isEnabled
	}
}