package ninegle.Readio.book.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.dto.BookIdRequestDto;
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto;
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto;
import ninegle.Readio.book.service.PreferenceService;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.user.service.UserContextService;

/**
 * Readio - PreferenceController
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
@RestController
@RequestMapping("/preferences")
@RequiredArgsConstructor
public class PreferenceController {

	private final PreferenceService preferenceService;
	private final UserContextService userContextService;

	@PostMapping
	public ResponseEntity<BaseResponse<PreferenceResponseDto>> save(@RequestBody @Valid BookIdRequestDto dto){
		Long userId = userContextService.getCurrentUserId();

		PreferenceResponseDto saved = preferenceService.save(userId, dto);
		return BaseResponse.ok("데이터가 성공적으로 저장되었습니다.",saved,HttpStatus.CREATED);
	}

	@DeleteMapping("/{book_id}")
	public ResponseEntity<BaseResponse<Void>> delete(@PathVariable("book_id") Long bookId){
		Long userId = userContextService.getCurrentUserId();
		preferenceService.delete(userId, bookId);
		return BaseResponse.okOnlyStatus(HttpStatus.NO_CONTENT);
	}

	@GetMapping
	public ResponseEntity<BaseResponse<PreferenceListResponseDto>> getPreferences(
		@RequestParam(defaultValue = "1") int page,
		@RequestParam(defaultValue = "3") int size) {

		if (page < 1 || size < 1 || size > 50) {
			throw new BusinessException(ErrorCode.INVALID_PAGINATION_PARAMETER);
		}
		Long userId = userContextService.getCurrentUserId();

		PreferenceListResponseDto result = preferenceService.getPreferenceList(userId, page, size);
		return BaseResponse.ok("관심도서 조회가 성공적으로 수행되었습니다.", result, HttpStatus.OK);
	}
}
