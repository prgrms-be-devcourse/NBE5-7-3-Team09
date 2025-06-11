package ninegle.Readio.mypage.controller;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.mypage.dto.response.PointResponseDto;
import ninegle.Readio.mypage.service.PointService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/my")
@RequiredArgsConstructor
public class PointController {

	private final PointService pointService;

	@GetMapping("/points")
	public ResponseEntity<BaseResponse<PointResponseDto>> getPoints() {
		PointResponseDto responseDto = pointService.getUserPoints();

		return BaseResponse.ok("포인트 조회 성공", responseDto, HttpStatus.OK);
	}
}