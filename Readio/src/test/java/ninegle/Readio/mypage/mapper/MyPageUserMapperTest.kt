package ninegle.Readio.mypage.mapper

import ninegle.Readio.mypage.dto.response.UserInfoDto
import ninegle.Readio.user.domain.User
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.mockito.Mockito.*
import kotlin.test.assertNull

/**
 * 지금 User가 java코드로 되어있어서 호환성 문제로인해서
 * 자꾸 test코드에 오류가 발생하기때문에, 일단 mock 기반테스트로 진행함
 */
class MyPageUserMapperTest {

    @Test
    fun `User 객체를 UserInfoDto로 변환 테스트`() {
        val mockUser = mock(User::class.java)

        `when`(mockUser.email).thenReturn("test@example.com")
        `when`(mockUser.nickname).thenReturn("팀9글")
        `when`(mockUser.phoneNumber).thenReturn("010-1234-5678")
        `when`(mockUser.point).thenReturn(15000L)

        val dto: UserInfoDto = MyPageUserMapper.toUserInfoDto(mockUser)

        assertEquals("test@example.com", dto.email)
        assertEquals("팀9글", dto.nickname)
        assertEquals("010-1234-5678", dto.phoneNumber)
        assertEquals(15000L, dto.point)
    }


    @Test
    fun `닉네임이 null인 경우 변환 테스트`() {
        val mockUser = mock(User::class.java)
        `when`(mockUser.email).thenReturn("test@example.com")
        `when`(mockUser.nickname).thenReturn(null)  // null 테스트
        `when`(mockUser.phoneNumber).thenReturn("010-1234-5678")
        `when`(mockUser.point).thenReturn(15000L)

        val dto: UserInfoDto = MyPageUserMapper.toUserInfoDto(mockUser)

        assertAll(
            { assertEquals("test@example.com", dto.email) },
            { assertNull(dto.nickname) },
            { assertEquals("010-1234-5678", dto.phoneNumber) },
            { assertEquals(15000L, dto.point) }
        )
    }


    @Test
    fun `전화번호가 null인 경우 변환 테스트`() {
        val mockUser = mock(User::class.java)
        `when`(mockUser.email).thenReturn("test@example.com")
        `when`(mockUser.nickname).thenReturn("팀9글")
        `when`(mockUser.phoneNumber).thenReturn(null)  // null 테스트
        `when`(mockUser.point).thenReturn(15000L)

        val dto: UserInfoDto = MyPageUserMapper.toUserInfoDto(mockUser)

        assertAll(
            { assertEquals("test@example.com", dto.email) },
            { assertEquals("팀9글", dto.nickname) },
            { assertNull(dto.phoneNumber) },
            { assertEquals(15000L, dto.point) }
        )
    }

    @Test
    fun `포인트가 변경된 경우 변환 테스트`() {
        // 포인트 사용 후 상황
        val mockUser = mock(User::class.java)
        `when`(mockUser.email).thenReturn("test@example.com")
        `when`(mockUser.nickname).thenReturn("팀9글")
        `when`(mockUser.phoneNumber).thenReturn("010-1234-5678")
        `when`(mockUser.point).thenReturn(100L)  // 14900원 사용 후

        val dto: UserInfoDto = MyPageUserMapper.toUserInfoDto(mockUser)

        assertAll(
            { assertEquals("test@example.com", dto.email) },
            { assertEquals("팀9글", dto.nickname) },
            { assertEquals("010-1234-5678", dto.phoneNumber) },
            { assertEquals(100L, dto.point) }
        )
    }
}
