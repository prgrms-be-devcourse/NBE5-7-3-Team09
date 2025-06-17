package ninegle.Readio.user.mapper

import ninegle.Readio.user.domain.User
import ninegle.Readio.user.dto.Delete
import ninegle.Readio.user.dto.DeleteUserRequestDto
import ninegle.Readio.user.dto.SignUpRequestDto
import org.springframework.security.crypto.password.PasswordEncoder

object UserMapper {

	fun toUser(dto: SignUpRequestDto, passwordEncoder: PasswordEncoder): User {
        return User (
            email = dto.email,
            password = passwordEncoder.encode(dto.password),  // 암호화
            nickname = dto.nickname,
            phoneNumber = dto.phoneNumber
        )
    }


	fun toDelete(deleteUserRequestDto: DeleteUserRequestDto): Delete {
        return Delete(
            email = deleteUserRequestDto.email,
            password = deleteUserRequestDto.password,
            refreshToken = deleteUserRequestDto.refreshToken
        )
    }

}
