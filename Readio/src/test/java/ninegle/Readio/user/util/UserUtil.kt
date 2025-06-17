package ninegle.Readio.user.util

import ninegle.Readio.user.domain.User

object UserUtil {

    fun createTestUser(
        email: String = "test@example.com",
        password: String = "encoded",
        nickname: String = "test",
        phoneNumber: String = "010-1111-1111",
        point: Long = 15000L
    ): User {
        return User(email, password, nickname, phoneNumber, point).apply {
            this.id = 1L  // 필요 시 ID도 설정
        }
    }

}
