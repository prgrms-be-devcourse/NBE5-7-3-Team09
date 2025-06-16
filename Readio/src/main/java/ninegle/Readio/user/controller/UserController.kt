package ninegle.Readio.user.controller

import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import lombok.RequiredArgsConstructor
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.user.dto.DeleteUserRequestDto
import ninegle.Readio.user.dto.LoginRequestDto
import ninegle.Readio.user.dto.RefreshTokenRequestDto
import ninegle.Readio.user.dto.SignUpRequestDto
import ninegle.Readio.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
@Validated
class UserController (
    private val userService: UserService)
{

    @PostMapping("/user/signup")
    fun signup(@RequestBody @Valid signUpRequestDto: SignUpRequestDto): ResponseEntity<BaseResponse<Void>> {
        userService.signup(signUpRequestDto)
        return BaseResponse.okOnlyStatus(HttpStatus.CREATED) //201
    }

    //반환으로 헤더에 토큰값을 넣어줘야 하니깐 HttpServletResponse
    @PostMapping("/user/login")
    fun login(@RequestBody @Valid  loginRequestDto:LoginRequestDto, response: HttpServletResponse): ResponseEntity<BaseResponse<Void>> {
        userService.login(loginRequestDto, response)
        return BaseResponse.okOnlyStatus(HttpStatus.OK) //200
    }

    //https로 사용한다 가정하에 body값으로 refresh token을 전송
    @PostMapping("/user/logout")
    fun logout( @RequestHeader("Authorization") @NotBlank accessToken: String,
        @RequestBody @Valid refreshTokenRequestDto: RefreshTokenRequestDto): ResponseEntity<BaseResponse<Void>> {
        userService.logout(accessToken, refreshTokenRequestDto)
        return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT) //204
    }

    //프론트에서 access token 만료로 인해 재발급을 요청함 (본인이 가진 refresh token을 가지고 요청합니다)
    @PostMapping("/user/reissue-token")
    fun reissueToken( @RequestHeader("Authorization")  @NotBlank refreshToken: String,
        response: HttpServletResponse): ResponseEntity<BaseResponse<Void>> {
        userService.reissue(refreshToken, response)
        return BaseResponse.okOnlyStatus(HttpStatus.OK) //200
    }

    @DeleteMapping("/user/delete")
    fun deleteUser( @RequestHeader("Authorization")  @NotBlank accessToken:String,
        @RequestBody deleteUserRequestDto: @Valid DeleteUserRequestDto): ResponseEntity<BaseResponse<Void>> {
        userService.deleteUser(accessToken, deleteUserRequestDto)
        return BaseResponse.okOnlyStatus(HttpStatus.OK)
    }
}
