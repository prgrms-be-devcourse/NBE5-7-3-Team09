package ninegle.Readio.mypage.controller

import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.mypage.dto.request.UserUpdateRequestDto
import ninegle.Readio.mypage.dto.response.UserInfoDto
import ninegle.Readio.mypage.service.MyPageUserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * 마이페이지 사용자 정보 관리 컨트롤러
 */
@RestController
@RequestMapping("/user/my")
class MyPageUserController(
	private val myPageUserService: MyPageUserService
) {

	//사용자 정보 조회 API - GET /user/my
	@GetMapping
	fun getUserInfo(): ResponseEntity<BaseResponse<UserInfoDto>> {
		val data = myPageUserService.getUserInfo()
		return BaseResponse.ok("회원 정보 조회 성공", data, HttpStatus.OK)
	}

	//사용자 정보 수정 API - POST /user/my
	@PostMapping
	fun updateUserInfo(@RequestBody dto: UserUpdateRequestDto): ResponseEntity<BaseResponse<UserInfoDto>> {
		val data = myPageUserService.updateUserInfo(dto)
		return BaseResponse.ok("회원 정보 수정 성공", data, HttpStatus.OK)
	}
}