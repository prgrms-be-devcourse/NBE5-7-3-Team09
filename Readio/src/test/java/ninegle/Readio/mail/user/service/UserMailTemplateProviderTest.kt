package ninegle.Readio.mail.user.service

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

class UserMailTemplateProviderTest {

    private lateinit var templateProvider: UserMailTemplateProvider

    @BeforeEach
    fun setUp() {
        templateProvider = UserMailTemplateProvider()
    }

    @Test
    fun `회원가입 메일 템플릿 생성 테스트 - 정상 닉네임`() {
        val nickname = "홍길동"

        val result = templateProvider.buildSignupMailBody(nickname)

        assertThat(result).isNotNull()
        assertThat(result).contains("안녕하세요, 홍길동님!")
        assertThat(result).contains("Readio 회원가입을 진심으로 환영합니다!")
        assertThat(result).contains("가입 축하 포인트 15,000P")
        assertThat(result).contains("Readio 팀 드림")
    }

    @Test
    fun `회원가입 메일 템플릿 생성 테스트- 빈 닉네임`() {
        val emptyNickname = ""

        val result = templateProvider.buildSignupMailBody(emptyNickname)

        assertThat(result).contains("안녕하세요, 회원님!")
    }

    @Test
    fun `회원가입 메일 템플릿 생성 테스트 - 공백 닉네임`() {
        val whitespaceNickname = "   "

        val result = templateProvider.buildSignupMailBody(whitespaceNickname)

        assertThat(result).contains("안녕하세요, 회원님!")
    }

    @Test
    fun `템플릿에 필수 내용 포함 확인 테스트`() {
        val nickname = "테스터"

        val result = templateProvider.buildSignupMailBody(nickname)

        assertThat(result).contains("✨ Readio 회원가입을 진심으로 환영합니다! ✨")
        assertThat(result).contains("🎁 신규 회원 특별 혜택")
        assertThat(result).contains("💡 Readio에서 즐길 수 있는 서비스")
        assertThat(result).contains("© 2025. Readio, Inc. All rights reserved.")
        assertThat(result).contains("본 메일은 발신 전용입니다.")
        assertThat(result).contains("주식회사 Readio")
        assertThat(result).contains("전화번호: 02-123-456")
    }

    @Test
    fun `특수문자 닉네임 처리 테스트`() {
        val specialNickname = "테스터@#$%"

        val result = templateProvider.buildSignupMailBody(specialNickname)

        assertThat(result).contains("안녕하세요, ${specialNickname}님!")
    }
}