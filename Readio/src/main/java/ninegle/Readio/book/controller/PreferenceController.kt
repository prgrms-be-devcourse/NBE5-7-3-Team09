package ninegle.Readio.book.controller

import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import ninegle.Readio.book.dto.BookIdRequestDto
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto
import ninegle.Readio.book.service.PreferenceService
import ninegle.Readio.global.exception.BusinessException
import ninegle.Readio.global.exception.domain.ErrorCode
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.user.service.UserContextService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/preferences")
class PreferenceController(
    val userContextService: UserContextService,
    val preferenceService: PreferenceService
) {
    @PostMapping
    fun save(@RequestBody dto: @Valid BookIdRequestDto): ResponseEntity<BaseResponse<PreferenceResponseDto>> {
        val userId = userContextService!!.currentUserId

        val saved = preferenceService!!.save(userId, dto)
        return BaseResponse.ok("데이터가 성공적으로 저장되었습니다.", saved, HttpStatus.CREATED)
    }

    @DeleteMapping("/{book_id}")
    fun delete(@PathVariable("book_id") bookId: Long): ResponseEntity<BaseResponse<Void>> {
        val userId = userContextService.currentUserId
        preferenceService.delete(userId, bookId)
        return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT)
    }

    @GetMapping
    fun getPreferences(
        @RequestParam(defaultValue = "1") page: Int,
        @RequestParam(defaultValue = "3") size: Int
    ): ResponseEntity<BaseResponse<PreferenceListResponseDto>> {
        if (page < 1 || size < 1 || size > 50) {
            throw BusinessException(ErrorCode.INVALID_PAGINATION_PARAMETER)
        }
        val userId = userContextService.currentUserId

        val result = preferenceService.getPreferenceList(userId, page, size)
        return BaseResponse.ok("관심도서 조회가 성공적으로 수행되었습니다.", result, HttpStatus.OK)
    }
}
