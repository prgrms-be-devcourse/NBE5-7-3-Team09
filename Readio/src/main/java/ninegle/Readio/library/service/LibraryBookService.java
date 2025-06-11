package ninegle.Readio.library.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.repository.BookRepository;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.library.domain.Library;
import ninegle.Readio.library.domain.LibraryBook;
import ninegle.Readio.library.dto.book.LibraryBookListResponseDto;
import ninegle.Readio.library.dto.book.NewLibraryBookRequestDto;
import ninegle.Readio.library.mapper.LibraryBookMapper;
import ninegle.Readio.library.repository.LibraryBookRepository;
import ninegle.Readio.library.repository.LibraryRepository;
import ninegle.Readio.user.service.UserContextService;

@Service
@RequiredArgsConstructor
public class LibraryBookService {
	private final BookRepository bookRepository;
	private final LibraryRepository libraryRepository;
	private final LibraryBookRepository libraryBookRepository;
	private final UserContextService userContextService;
	private final LibraryBookMapper libraryBookMapper;

	//라이브러리에 책 저장
	@Transactional
	public void newLibraryBook(Long libraryId, NewLibraryBookRequestDto bookRequestDto) {

		//라이브러리 가져오기
		Library library = getLibrary(libraryId);

		//라이브러리에 추가할 책
		Long newLibraryBookId = libraryBookMapper.toNewLibraryBook(bookRequestDto);
		Optional<Book> findBook = bookRepository.findById(newLibraryBookId);
		if (findBook.isEmpty()) {
			throw new BusinessException(ErrorCode.BOOK_NOT_FOUND); //404
		}
		Book book = findBook.get();

		Optional<LibraryBook> optionalLibraryBook = libraryBookRepository.duplicateTest(library.getId(), book.getId());
		if (optionalLibraryBook.isPresent()) {
			throw new BusinessException(ErrorCode.BOOK_ALREADY_EXISTS); // 책이 이미 해당 라이브러리에 존재하는 경우 409
		}

		LibraryBook libraryBook = LibraryBook.builder()
			.book(book)
			.library(library).build();

		libraryBookRepository.save(libraryBook);

	}

	//라이브러리에 책들 불러오기
	@Transactional(readOnly = true)
	public LibraryBookListResponseDto getAllLibraryBooks(Long libraryId,
		Pageable pageable) {
		Library library = getLibrary(libraryId);

		//조회한 책들
		Page<Book> books = libraryBookRepository.findBookByLibraryId(library.getId(), pageable);
		LibraryBookListResponseDto libraryBookListResponseDto = libraryBookMapper.libraryBookListResponseDto(library,
			books);
		return libraryBookListResponseDto;
	}

	//라이브러리에 책 삭제
	@Transactional
	public void deleteLibraryBook(Long libraryId, Long BookId) {
		Library library = getLibrary(libraryId);
		Optional<LibraryBook> libraryBoook = libraryBookRepository.findLibraryBoook(libraryId, BookId);
		if (libraryBoook.isEmpty()) {
			throw new BusinessException(ErrorCode.BOOK_NOT_FOUND); //404
		}
		LibraryBook libraryBook = libraryBoook.get();
		libraryBookRepository.delete(libraryBook);

	}

	public Library getLibrary(Long libraryId) {
		Optional<Library> libraryOptional = libraryRepository.findById(libraryId);
		if (libraryOptional.isEmpty()) {
			throw new BusinessException(ErrorCode.LIBRARY_NOT_FOUND);
		}
		Library library = libraryOptional.get();
		Long currentUserId = userContextService.getCurrentUserId();
		if (!library.getUser().getId().equals(currentUserId)) {
			throw new BusinessException(ErrorCode.FORBIDDEN_ACCESS); // 403: 권한 없음
		}
		return libraryOptional.get();
	}
}
