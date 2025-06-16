package ninegle.Readio.book.util

import ninegle.Readio.book.domain.Author
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.domain.Review
import ninegle.Readio.book.dto.BookRequestDto
import ninegle.Readio.book.dto.BookResponseDto
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.dto.author.AuthorDto
import ninegle.Readio.book.dto.booksearch.BookListResponseDto
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto
import ninegle.Readio.book.mapper.toAuthorDto
import ninegle.Readio.book.mapper.toCategoryDto
import ninegle.Readio.book.mapper.toPublisherDto
import ninegle.Readio.book.mapper.toSearchResponseDto
import ninegle.Readio.category.domain.Category
import ninegle.Readio.publisher.domain.Publisher
import ninegle.Readio.user.domain.User
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import java.math.BigDecimal
import java.time.LocalDate


// Book
fun genBook(id: Long?, req: BookRequestDto, pub: Publisher, author: Author, category: Category, imageUrl: String) = Book(
        id = id,
        name = req.name,
        description = req.description,
        image = imageUrl,
        isbn = req.isbn,
        ecn = req.ecn,
        pubDate = req.pubDate,
        category = category,
        publisher = pub,
        author = author
    )

fun genBookRespDto(book: Book) = BookResponseDto(
    id = book.id!!,
    name = book.name,
    description = book.description,
    image = book.image,
    isbn = book.isbn,
    ecn = book.ecn,
    pubDate = book.pubDate,
    category = book.category.toCategoryDto(),
    publisher = book.publisher.toPublisherDto(),
    author = book.author.toAuthorDto()
)

fun genBookReq(
    name: String,
    description: String,
    image: MultipartFile,
    isbn: String,
    ecn: String,
    pubDate: LocalDate,
    epubFile: MultipartFile,
    categorySub: String,
    publisherName: String,
    authorName: String
) = BookRequestDto(
    name = name,
    description = description,
    image = image,
    isbn = isbn,
    ecn = ecn,
    pubDate = pubDate,
    epubFile = epubFile,
    categorySub = categorySub,
    publisherName = publisherName,
    authorName = authorName
)


// BookSearch
fun genBookSearch(book: Book) = BookSearch(
    id = book.id,
    name = book.name,
    image = book.image,
    categorySub = book.category.sub,
    categoryMajor = book.category.major,
    author = book.author.name,
    expired = false,
    rating = book.rating
)

fun genBookSearchRespDto(book: Book) = book.toSearchResponseDto()

fun BookSearch.toSearchResponseDto() =
    BookSearchResponseDto(
        id = this.id!!,
        name = this.name,
        image = this.image,
        categoryMajor = this.categoryMajor,
        categorySub = this.categorySub,
        authorName = this.author,
        rating = this.rating
    )

fun genBookSearchListRespDto(bookList: MutableList<BookSearch>): MutableList<BookSearchResponseDto> = bookList.map { it.toSearchResponseDto() }.toMutableList()

fun genBookListResponseDto(bookSearchRespDtos: MutableList<BookSearchResponseDto>, paginationDto: PaginationDto) =
    BookListResponseDto(
        books = bookSearchRespDtos,
        pagination = paginationDto)


// Author
fun genAuthor(id: Long, name: String) = Author(id, name)

fun genAuthorDto(id: Long, name: String) = AuthorDto(id, name)


// pagination
fun genPaginationDto(count: Long, page: Int, size: Int) = PaginationDto(
    totalPages = (count.toInt() / size) +1,
    size = size,
    currentPage = page,
    totalElements = count
)

// MultipartFile

fun genMockMultipartFile(name: String, originalFilename: String, contentType: String, content: ByteArray) =
    MockMultipartFile(name, originalFilename, contentType, content)

// Review
fun genReview(rating: BigDecimal, text : String, user : User, book : Book) = Review(
    rating = rating,
    text = text,
    user = user,
    book = book
)
//User
fun genUser(
    email: String ,
    password: String ,
    nickname: String ,
    phoneNumber: String
): User {
    return User(email, password, nickname, phoneNumber)
}


