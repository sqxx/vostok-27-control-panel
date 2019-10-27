package open.sqxx.vostok27.ui.main.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.view_option_time.view.*
import open.sqxx.vostok27.R

class OptionTimeView(
	context: Context,
	private val attrs: AttributeSet
) : LinearLayout(context, attrs) {

	private var layout: View = LayoutInflater
		.from(context)
		.inflate(R.layout.view_option_time, this, true)

	private fun collectAttributes() {
		val attributes = context.obtainStyledAttributes(attrs, R.styleable.OptionTimeView)

		try {
			setTitle(attributes.getString(R.styleable.OptionTimeView_title)!!)
			setValue(attributes.getString(R.styleable.OptionTimeView_value)!!)
		} finally {
			attributes.recycle()
		}
	}

	private fun setTitle(title: String) {
		layout.title.text = title
	}

	private fun setValue(value: String) {
		layout.time.setIs24HourView(true)

		try {
			layout.time.currentMinute = value.split(":")[0].toInt()
			layout.time.currentHour = value.split(":")[1].toInt()
		} catch (e: Exception) {
			throw IllegalArgumentException("Argument value needs time format HH:mm")
		}
	}
}