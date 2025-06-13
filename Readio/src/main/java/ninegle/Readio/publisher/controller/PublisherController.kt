package ninegle.Readio.publisher.controller

import lombok.RequiredArgsConstructor
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.publisher.dto.PublisherListResponseDto
import ninegle.Readio.publisher.dto.PublisherRequestDto
import ninegle.Readio.publisher.dto.PublisherResponseDto
import ninegle.Readio.publisher.service.PublisherService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/publishers")
class PublisherController(
    private val publisherService: PublisherService
) {

    @PostMapping
    fun save(
        @RequestBody request: PublisherRequestDto
    ): ResponseEntity<BaseResponse<PublisherResponseDto>> {
        val response = publisherService.save(request)
        return BaseResponse.ok("출판사 등록이 정상적으로 등록되었습니다.", response, HttpStatus.CREATED)
    }

    @GetMapping
    fun getPublishers(): ResponseEntity<BaseResponse<PublisherListResponseDto>> {
        val response = publisherService.getPublisherAll()
        return BaseResponse.ok("출판사 조회가 정상적으로 수행되었습니다.", response, HttpStatus.OK)
    }
}
