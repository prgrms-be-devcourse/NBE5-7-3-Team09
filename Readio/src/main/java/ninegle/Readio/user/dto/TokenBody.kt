package ninegle.Readio.user.dto

import ninegle.Readio.user.domain.Role


data class TokenBody(
    var userId: Long?,
    var email: String,
    var role: Role
)
