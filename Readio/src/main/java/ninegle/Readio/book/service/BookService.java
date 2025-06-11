package ninegle.Readio.book.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ninegle.Readio.adapter.service.NCloudStorageService;
import ninegle.Readio.book.domain.Author;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.BookSearch;
import ninegle.Readio.category.domain.Category;
import ninegle.Readio.publisher.domain.Publisher;
import ninegle.Readio.book.dto.booksearch.BookListResponseDto;
import ninegle.Readio.book.dto.BookRequestDto;
import ninegle.Readio.book.dto.BookResponseDto;
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto;
import ninegle.Readio.book.dto.viewer.ViewerResponseDto;
import ninegle.Readio.book.mapper.BookMapper;
import ninegle.Readio.book.mapper.BookSearchMapper;
import ninegle.Readio.book.repository.AuthorRepository;
import ninegle.Readio.book.repository.BookRepository;
import ninegle.Readio.book.repository.BookSearchRepository;
import ninegle.Readio.category.repository.CategoryRepository;
import ninegle.Readio.publisher.repository.PublisherRepository;
import ninegle.Readio.global.exception.BusinessException;
import ninegle.Readio.global.exception.domain.ErrorCode;
import ninegle.Readio.book.dto.PaginationDto;

/**
 * Readio - BookService
 * create date:    25. 5. 8.
 * last update:    25. 5. 8.
 * author:  gigol
 * purpose:
 */
@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class BookService {

	private final BookRepository bookRepository;
	private final AuthorRepository authorRepository;
	private final CategoryRepository categoryRepository;
	private final PublisherRepository publisherRepository;
	private final BookSearchRepository bookSearchRepository;
	private final NCloudStorageService nCloudStorageService;
	private final BookSearchMapper bookSearchMapper;
	private final BookMapper bookMapper;

	@Transactional(readOnly = true)
	public BookListResponseDto searchBooks(String keyword, int page, int size) {

		List<BookSearchResponseDto> findBooks = bookSearchMapper.toResponseDto(getBookSearchList(keyword, page, size));

		long totalElements = findBooks.size();
		PaginationDto paginationDto = bookMapper.toPaginationDto(totalElements, page, size);

		return bookMapper.toBookListResponseDto(findBooks, paginationDto);
	}

	private List<BookSearch> getBookSearchList(String keyword, int page, int size) {
		Set<BookSearch> result = new LinkedHashSet<>();
		Pageable pageable = PageRequest.of(page - 1, size);

		result.addAll(bookSearchRepository.findByExpiredFalseAndAuthorContaining(keyword, pageable).getContent());
		result.addAll(bookSearchRepository.findByExpiredFalseAndNameContaining(keyword, pageable).getContent());

		return new ArrayList<>(result);
	}

	@Transactional
	public void save(BookRequestDto request) throws IOException {

		validateSavedBook(request);

		// 1. S3에 업로드할 파일명 생성 ( 책 제목 기반 )
		String fileKey = "epub/" + request.getName() + ".epub";
		String imageKey = "image/" + request.getName() + ".jpg";

		// 2. S3에 해당 파일 존재 여부 확인
		if (!nCloudStorageService.fileExists(fileKey)) {
			nCloudStorageService.uploadFile(fileKey, request.getEpubFile());
		}
		if (!nCloudStorageService.fileExists(imageKey)) {
			nCloudStorageService.uploadFile(imageKey, request.getImage());
		}

		// 3. 연관 엔티티 조회
		Category category = getCategory(request.getCategorySub());
		Author author = getAuthor(request.getAuthorName());
		Publisher publisher = getPublisher(request.getPublisherName());

		// 4. Book 저장
		Book savedBook = bookRepository.save(bookMapper.toEntity(request, publisher, author, category, imageKey));

		// 5. ElasticSearch Repository에 저장
		bookSearchRepository.save(bookSearchMapper.toEntity(savedBook));
	}

	private void validateSavedBook(BookRequestDto request) {
		if (existsByIsbn(request.getIsbn())) {
			throw new BusinessException(ErrorCode.DUPLICATE_ISBN);
		}
		if (existsByEcn(request.getEcn())) {
			throw new BusinessException(ErrorCode.DUPLICATE_ECN);
		}
	}

	private Boolean existsByIsbn(String isbn) {
		return bookRepository.existsByIsbn(isbn);
	}

	private Boolean existsByEcn(String ecn) {
		return bookRepository.existsByEcn(ecn);
	}

	// 카테고리가 존재하면 Get, 존재하지 않다면 Throw 발생
	private Category getCategory(String categorySub) {
		return categoryRepository.findBySub(categorySub)
			.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
	}

	// 작가가 존재하면 해당 작가를 Get, 존재하지 않다면 새로운 작가 생성 후 Get
	private Author getAuthor(String authorName) {
		return authorRepository.findByName(authorName)
			.orElseGet(() -> authorRepository.save(new Author(authorName)));
	}

	// 출판사가 존재하면 해당 출판사 Get, 존재하지 않다면 새로운 출판사 생성 후 Get
	private Publisher getPublisher(String publisherName) {
		return publisherRepository.findByName(publisherName)
			.orElseGet(() -> publisherRepository.save(new Publisher(publisherName)));
	}

	@Transactional(readOnly = true)
	public BookResponseDto getBookDetail(Long id) {

		Book findBook = getBookByIdAndExpired(id);
		if (findBook.getExpired() == true) {
			throw new BusinessException(ErrorCode.BOOK_NOT_FOUND);
		}

		return bookMapper.toDto(findBook);
	}

	private void validateUpdatedBook(BookRequestDto request, Long bookId) {
		if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), bookId)) {
			throw new BusinessException(ErrorCode.DUPLICATE_ISBN);
		}
		if (request.getEcn() != null && bookRepository.existsByEcnAndIdNot(request.getEcn(), bookId)) {
			throw new BusinessException(ErrorCode.DUPLICATE_ECN);
		}
	}

	@Transactional
	public BookResponseDto updateBook(Long id, BookRequestDto request) {
		Book targetBook = getBookById(id);
		if (!targetBook.getIsbn().equals(request.getIsbn()) ||
			!Objects.equals(targetBook.getEcn(), request.getEcn())) {
			validateUpdatedBook(request, id);
		}

		BookSearch targetBookSearch = bookSearchRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

		String beforeName = targetBook.getName();
		String afterName = request.getName();

		// 이름이 바뀌었으면 epub, image 파일 rename
		if (!beforeName.equals(afterName)) {
			nCloudStorageService.renameFileOnCloud(beforeName, afterName, "epub", ".epub"); // epub 파일명 변경
			nCloudStorageService.renameFileOnCloud(beforeName, afterName, "image", ".jpg");
		}
		String updatedImageUrl = "image/" + afterName + ".jpg";

		Category category = getCategory(request.getCategorySub());
		Author author = getAuthor(request.getAuthorName());
		Publisher publisher = getPublisher(request.getPublisherName());

		Book updatedBook = targetBook.update(request, category, author, publisher, updatedImageUrl);
		BookSearch updatedBookSearch = targetBookSearch.update(request, category, author, updatedImageUrl);

		bookRepository.save(updatedBook);
		bookSearchRepository.save(updatedBookSearch);

		return bookMapper.toDto(updatedBook);
	}

	// 만료 되지 않은 책 get
	private Book getBookByIdAndExpired(Long id) {
		return bookRepository.findByIdAndExpiredFalse(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
	}

	public Book getBookById(long id) {
		return bookRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));
	}

	@Transactional
	public void deleteBook(Long id) {

		Book findBook = getBookByIdAndExpired(id);
		bookRepository.delete(findBook);

		BookSearch findBookSearch = bookSearchRepository.findById(id)
			.orElseThrow(() -> new BusinessException(ErrorCode.BOOK_NOT_FOUND));

		if (findBookSearch.getExpired() == false) {
			findBookSearch.softDelete();
			bookSearchRepository.save(findBookSearch);
		}
	}

	// 0시 0분 기준 만료된지 7일 지난 도서 삭제
	@Scheduled(cron = "0 0 0 * * ?")
	public void deleteExpiredBooks() {

		LocalDateTime threshold = LocalDateTime.now().minusDays(7);

		// 삭제 id 목록 조회
		List<Book> expiredBooks = bookRepository.findByExpiredTrueAndExpiredAtBefore(threshold);
		if (expiredBooks.isEmpty()) {
			return;
		}
		List<Long> ids = expiredBooks.stream().map(Book::getId).toList();

		// JPA 물리 삭제
		int deleteCount = bookRepository.deleteExpiredBefore(threshold);

		// ES 물리 삭제
		bookSearchRepository.deleteAllById(ids);

		// ncp 파일 삭제
		expiredBooks.forEach(book -> {
			nCloudStorageService.deleteFileOnCloud(book.getName(), "epub", ".epub");
			nCloudStorageService.deleteFileOnCloud(book.getName(), "image", ".jpg");
		});
		log.debug("deleteCount = {}", deleteCount);
		log.debug("deleteBookIds = {}", ids);
	}

	@Transactional(readOnly = true)
	public BookListResponseDto getBookByCategory(String categoryMajor, int page, int size) {

		Pageable pageable = PageRequest.of(page - 1, size);
		Page<BookSearch> findBooks = categoryMajor.equals("null")
			? bookSearchRepository.findByExpiredFalse(pageable)
			: bookSearchRepository.findByExpiredFalseAndCategoryMajor(categoryMajor, pageable);

		// 총 책의 개수
		long totalElements = findBooks.getTotalElements();

		List<BookSearch> books = findBooks.getContent();
		List<BookSearchResponseDto> responseDtos = bookSearchMapper.toResponseDto(books);
		PaginationDto paginationDto = bookMapper.toPaginationDto(totalElements, page, size);

		return bookMapper.toBookListResponseDto(responseDtos, paginationDto);
	}

	@Transactional(readOnly = true)
	public ViewerResponseDto getViewerBook(Long id) {
		Book findBook = getBookByIdAndExpired(id);

		String epubUri = nCloudStorageService.generateObjectUrl("epub/" + findBook.getName() + ".epub");

		return ViewerResponseDto.builder().epubUri(epubUri).build();
	}
}