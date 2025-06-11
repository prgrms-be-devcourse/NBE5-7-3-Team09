package ninegle.Readio.book.mapper;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import ninegle.Readio.adapter.config.NCloudStorageConfig;
import ninegle.Readio.book.domain.Author;
import ninegle.Readio.book.domain.Book;
import ninegle.Readio.category.domain.Category;
import ninegle.Readio.publisher.domain.Publisher;
import ninegle.Readio.book.dto.author.AuthorDto;
import ninegle.Readio.book.dto.booksearch.BookListResponseDto;
import ninegle.Readio.book.dto.BookRequestDto;
import ninegle.Readio.book.dto.BookResponseDto;
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto;
import ninegle.Readio.category.dto.CategoryDto;
import ninegle.Readio.book.dto.PaginationDto;
import ninegle.Readio.publisher.dto.PublisherDto;

@Component
@RequiredArgsConstructor
public class BookMapper {

	private final NCloudStorageConfig nCloudStorageConfig;

	public BookResponseDto toDto(Book book) {
		return BookResponseDto.builder()
			.id(book.getId())
			.name(book.getName())
			.description(book.getDescription())
			.image(nCloudStorageConfig.toImageUrl(book.getImage()))
			.isbn(book.getIsbn())
			.ecn(book.getEcn())
			.pubDate(book.getPubDate())
			.category(toCategoryDto(book.getCategory()))
			.publisher(toPublisherDto(book.getPublisher()))
			.author(toAuthorDto(book.getAuthor()))
			.build();
	}

	public CategoryDto toCategoryDto(Category category) {
		return CategoryDto.builder()
			.id(category.getId())
			.major(category.getMajor())
			.sub(category.getSub())
			.build();
	}

	public PublisherDto toPublisherDto(Publisher publisher) {
		return PublisherDto.builder()
			.id(publisher.getId())
			.name(publisher.getName())
			.build();
	}

	public AuthorDto toAuthorDto(Author author) {
		return AuthorDto.builder()
			.id(author.getId())
			.name(author.getName())
			.build();
	}

	public Book toEntity (BookRequestDto dto, Publisher publisher,
		Author author, Category category, String imageUrl) {
		return Book.builder()
			.name(dto.getName())
			.description(dto.getDescription())
			.image(imageUrl)
			.isbn(dto.getIsbn())
			.ecn(dto.getEcn())
			.pubDate(dto.getPubDate())
			.category(category)
			.publisher(publisher)
			.author(author)
			.build();
	}

	public PaginationDto toPaginationDto(Long count,int page,int size){
		return PaginationDto.builder()
			.totalPages((count.intValue()/size)+1)
			.size(size)
			.currentPage(page)
			.totalElements(count)
			.build();
	}

	public BookSearchResponseDto toSearchResponseDto(Book book) {
		return BookSearchResponseDto.builder()
			.id(book.getId())
			.name(book.getName())
			.image(book.getImage())
			.categoryMajor(book.getCategory().getMajor())
			.categorySub(book.getCategory().getSub())
			.authorName(book.getAuthor().getName())
			.build();
	}

	public List<BookSearchResponseDto> toResponseDto(List<Book> books){
		ArrayList<BookSearchResponseDto> bookResponseDtos = new ArrayList<>();
		for (Book book : books) {
			bookResponseDtos.add(toSearchResponseDto(book));
		}
		return bookResponseDtos;
	}

	public BookListResponseDto toBookListResponseDto(List<BookSearchResponseDto> bookList, PaginationDto paginationDto) {
		return BookListResponseDto.builder()
			.books(bookList)
			.pagination(paginationDto)
			.build();
	}

}
