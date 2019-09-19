package open.sqxx.vostok27.model.system.message

data class SystemMessage(
	val text: String,
	val type: SystemMessageType = SystemMessageType.ALERT
)