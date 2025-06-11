package ninegle.Readio.book.service;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import ninegle.Readio.book.dto.preferencedto.PreferenceResponseDto;
import ninegle.Readio.book.dto.preferencedto.PreferenceListResponseDto;
import ninegle.Readio.book.dto.PaginationDto;
import ninegle.Readio.book.mapper.PreferenceMapper;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.Preference;
import ninegle.Readio.book.dto.BookIdRequestDto;
import ninegle.Readio.book.repository.PreferencesRepository;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.global.unit.BaseResponse;
import ninegle.Readio.user.domain.User;
import ninegle.Readio.user.service.UserContextService;
import ninegle.Readio.user.service.UserService;

/**
 * Readio - PreferenceService
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PreferenceService {

	private final PreferencesRepository preferencesRepository;
	private final PreferenceMapper preferenceMapper;
	private final BookService bookService;
	private final UserService userService;
	private final UserContextService userContextService;

	public Preference getPreferenceByBookAndUser(Book book,User user){
		return preferencesRepository.findPreferenceByBookAndUser(book,user)
			.orElseThrow(() -> new BusinessException(ErrorCode.PREFERENCE_NOT_FOUND));
	}


	@Transactional
	public PreferenceResponseDto save(Long userId,BookIdRequestDto dto) {

		Book book = bookService.getBookById(dto.getId());
		User user = userService.getById(userId);

		preferencesRepository.findPreferenceByBookAndUser(book,user).ifPresent((preference) -> {
			throw new BusinessException(ErrorCode.BOOK_ALREADY_IN_PREFERENCE);
		});

		Preference preference = preferenceMapper.toEntity(user, book);
		preferencesRepository.save(preference);
		return preferenceMapper.toPreferenceDto(preference);
	}

	@Transactional
	public ResponseEntity<BaseResponse<Void>> delete(Long userId,Long bookId) {
		Book book =bookService.getBookById(bookId);
		User user = userService.getById(userId);
		Preference preference = getPreferenceByBookAndUser(book, user);

		preferencesRepository.delete(preference);
		return BaseResponse.ok("삭제가 성공적으로 수행되었습니다.",null,HttpStatus.OK);
	}

	public PreferenceListResponseDto getPreferenceList(Long userId,int page, int size) {
		User user = userService.getById(userId);
		Pageable pageable = PageRequest.of(page-1,size);
		long count = preferencesRepository.countByUser(user);

		List<Preference> preferences = preferencesRepository.findPreferencesByUser(user, pageable).getContent();
		List<PreferenceResponseDto> preferenceList = preferenceMapper.toPreferenceDto(preferences);

		PaginationDto paginationDto = preferenceMapper.toPaginationDto(count, page, size);

		return preferenceMapper.toPreferenceListDto(preferenceList, paginationDto);
	}
}
