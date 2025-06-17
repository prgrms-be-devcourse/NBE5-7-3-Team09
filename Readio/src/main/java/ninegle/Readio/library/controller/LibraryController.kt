package ninegle.Readio.library.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import ninegle.Readio.global.unit.BaseResponse
import ninegle.Readio.library.dto.library.*
import ninegle.Readio.library.service.LibraryService
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@Validated
class LibraryController (
    private val libraryService: LibraryService)
{

    //라이브러리 생성
    @PostMapping("/library")
    fun newlibrary(
        @RequestBody @Valid libraryDto: NewLibraryRequestDto): ResponseEntity<BaseResponse<NewLibraryResponseDto>> {
        val response = libraryService.newLibrary(libraryDto)
        return BaseResponse.ok("라이브러리 생성 완료", response, HttpStatus.CREATED) //201
    }

    //라이브러리 전체 목록 조회
    @GetMapping("/library")
    fun getAllLibrary(
        @RequestParam(defaultValue = "0") @Max(500) @Min(0) page: Int,
        @RequestParam(defaultValue = "10") @Max(500) @Min(1) size: Int): ResponseEntity<BaseResponse<LibraryListResponseDto>> {
        val pageable: Pageable = PageRequest.of(page, size)
        val response = libraryService.getAllLibraries(pageable)
        return BaseResponse.ok("라이브러리 조회 완료", response, HttpStatus.OK) //200
    }

    //특정 라이브러리 삭제
    @DeleteMapping("/library/{libraryId}")
    fun deleteLibrary(
        @PathVariable("libraryId")  @NotNull @Max(5000) @Min(1) libraryId: Long): ResponseEntity<BaseResponse<Void>> {
        libraryService.deleteLibrary(libraryId)
        return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT) //204
    }

    //라이브러리 이름 수정
    @PutMapping("/library/{libraryId}")
    fun updateLibrary(
        @PathVariable("libraryId") @NotNull @Max(5000) @Min(1) libraryId: Long,
        @RequestBody @Valid requestDto: UpdateLibraryRequestDto): ResponseEntity<BaseResponse<UpdateLibraryResponseDto>> {
        val response = libraryService.updateLibrary(libraryId, requestDto)
        return BaseResponse.ok("라이브러리 이름 수정 완료", response, HttpStatus.OK)
    }
}
