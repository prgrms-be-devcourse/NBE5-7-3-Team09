package ninegle.Readio.library.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.library.dto.library.LibraryListResponseDto;
import ninegle.Readio.library.dto.library.NewLibraryRequestDto;
import ninegle.Readio.library.dto.library.NewLibraryResponseDto;
import ninegle.Readio.library.dto.library.UpdateLibraryRequestDto;
import ninegle.Readio.library.dto.library.UpdateLibraryResponseDto;
import ninegle.Readio.library.service.LibraryService;

@RestController
@RequiredArgsConstructor
@Validated
public class LibraryController {
	private final LibraryService libraryService;

	//라이브러리 생성
	@PostMapping("/library")
	public ResponseEntity<BaseResponse<NewLibraryResponseDto>> newlibrary(
		@RequestBody @Valid NewLibraryRequestDto libraryDto) {
		NewLibraryResponseDto response = libraryService.newLibrary(libraryDto);
		return BaseResponse.ok("라이브러리 생성 완료", response, HttpStatus.CREATED); //201
	}

	//라이브러리 전체 목록 조회
	@GetMapping("/library")
	public ResponseEntity<BaseResponse<LibraryListResponseDto>> getAllLibrary(
		@RequestParam(defaultValue = "0") @Max(500) @Min(0) int page,
		@RequestParam(defaultValue = "10") @Max(500) @Min(1) int size) {
		Pageable pageable = PageRequest.of(page, size);
		LibraryListResponseDto response = libraryService.getAllLibraries(pageable);
		return BaseResponse.ok("라이브러리 조회 완료", response, HttpStatus.OK); //200
	}

	//특정 라이브러리 삭제
	@DeleteMapping("/library/{libraryId}")
	public ResponseEntity<BaseResponse<Void>> deleteLibrary(
		@PathVariable("libraryId") @NotNull @Max(5000) @Min(1) Long libraryId) {
		libraryService.deleteLibrary(libraryId);
		return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT); //204
	}

	//라이브러리 이름 수정
	@PutMapping("/library/{libraryId}")
	public ResponseEntity<BaseResponse<UpdateLibraryResponseDto>> updateLibrary(
		@PathVariable("libraryId") @NotNull @Max(5000) @Min(1) Long libraryId,
		@RequestBody @Valid UpdateLibraryRequestDto requestDto) {
		UpdateLibraryResponseDto response = libraryService.updateLibrary(libraryId, requestDto);
		return BaseResponse.ok("라이브러리 이름 수정 완료", response, HttpStatus.OK);
	}
}
