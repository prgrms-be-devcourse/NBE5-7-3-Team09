package ninegle.Readio.book.mapper


import ninegle.Readio.adapter.config.NCloudStorageConfig
import ninegle.Readio.adapter.util.NCloudStorageUtils
import ninegle.Readio.book.domain.Author
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.dto.BookRequestDto
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.dto.author.AuthorDto
import ninegle.Readio.book.dto.booksearch.BookListResponseDto
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto
import ninegle.Readio.category.domain.Category
import ninegle.Readio.category.dto.CategoryDto
import ninegle.Readio.publisher.domain.Publisher
import ninegle.Readio.publisher.dto.PublisherDto
import org.springframework.stereotype.Component
import kotlin.text.ifEmpty

@Component
class BookMapper {

    fun toDto(book: Book): BookResponseDto {
        return BookResponseDto(
            id = book.id!!,
            name = book.name,
            description = book.description,
            image = NCloudStorageUtils.toImageUrl(book.image),
            isbn = book.isbn,
            ecn = book.ecn,
            pubDate = book.pubDate,
            category = book.category.toCategoryDto(),
            publisher = book.publisher.toPublisherDto(),
            author = book.author.toAuthorDto()
        )
    }


    fun toPaginationDto(count: Long, page: Int, size: Int): PaginationDto {
        return PaginationDto(
            totalPages = (count.toInt() / size) + 1,
            size = size,
            currentPage = page,
            totalElements = count
        )
    }
}
fun Publisher.toPublisherDto(): PublisherDto {
    return PublisherDto(
        id = this.id!!,
        name = this.name
    )
}


fun Category.toCategoryDto(): CategoryDto {
    return CategoryDto(
        id = this.id,
        major = this.major,
        sub = this.sub
    )
}


fun Author.toAuthorDto(): AuthorDto {
    return AuthorDto(
        id = this.id!!,
        name = this.name
    )
}


fun BookRequestDto.toEntity(publisher: Publisher, author: Author, category: Category, imageUrl: String): Book {
    return Book(
        name = this.name,
        description = this.description,
        image = imageUrl,
        isbn = this.isbn,
        ecn = this.ecn.ifEmpty { null },
        pubDate = this.pubDate,
        category = category,
        publisher = publisher,
        author = author
    )
}


fun MutableList<Book>.toResponseDto() = this.map { it.toSearchResponseDto() }.toMutableList()

fun Book.toSearchResponseDto(): BookSearchResponseDto {
    return BookSearchResponseDto(
        id = this.id!!,
        name = this.name,
        image = this.image,
        categoryMajor = this.category.major,
        categorySub = this.category.sub,
        authorName = this.author.name,
        rating = this.rating
    )
}

fun MutableList<BookSearchResponseDto>.toBookListResponseDto(paginationDto: PaginationDto): BookListResponseDto {
    return BookListResponseDto(
        books = this,
        pagination = paginationDto
    )
}
