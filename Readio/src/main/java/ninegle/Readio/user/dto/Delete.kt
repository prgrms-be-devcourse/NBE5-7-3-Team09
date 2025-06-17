package ninegle.Readio.user.dto



data class Delete(
    val refreshToken: String,
    val email: String,
    val password: String
)
