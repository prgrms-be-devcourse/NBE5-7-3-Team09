package ninegle.Readio.mypage.controller

import lombok.RequiredArgsConstructor
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.mypage.dto.response.PointResponseDto
import ninegle.Readio.mypage.service.PointService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * 포인트 관리 컨트롤러
 */
@RestController
@RequestMapping("/user/my")
class PointController(
    private val pointService: PointService
) {
    //사용자 포인트 조회 API - GET /user/my/points
    @GetMapping("/points")
    fun getPoints(): ResponseEntity<BaseResponse<PointResponseDto>> {
        val responseDto = pointService.getUserPoints()
        return BaseResponse.ok("포인트 조회 성공", responseDto, HttpStatus.OK)
    }
}