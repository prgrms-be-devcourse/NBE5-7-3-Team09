package ninegle.Readio.global.exception.dto


data class ErrorResponse(val status: Int, val code: String, val message: String, val path: String)
