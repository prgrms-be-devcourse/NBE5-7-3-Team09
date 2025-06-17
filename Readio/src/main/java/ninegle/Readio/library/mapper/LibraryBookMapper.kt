package ninegle.Readio.library.mapper

import ninegle.Readio.adapter.config.NCloudStorageConfig
import ninegle.Readio.book.domain.Book
import ninegle.Readio.library.domain.Library
import ninegle.Readio.library.dto.book.AllLibraryBooksDto
import ninegle.Readio.library.dto.book.LibraryBookListResponseDto
import ninegle.Readio.library.dto.book.LibraryDto
import ninegle.Readio.library.dto.book.NewLibraryBookRequestDto
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component


@Component

class LibraryBookMapper (
    private val nCloudStorageConfig: NCloudStorageConfig? = null)
{

    //라이브러리에 책 추가
    fun toNewLibraryBook(libraryBookRequestDto: NewLibraryBookRequestDto): Long {
        return libraryBookRequestDto.bookId
    }

    //라이브러리에 책 목록 가져오기
    fun libraryBookListResponseDto(library: Library, books: Page<Book>): LibraryBookListResponseDto {
        val allLibraryBooksDto  = books.content.map { book: Book ->
                AllLibraryBooksDto(
                    bookId = book.id,
                    bookName = book.name,
                    bookImage = nCloudStorageConfig!!.toImageUrl(book.image),
                    bookIsbn = book.isbn,
                    bookEcn = book.ecn,
                    bookPubDate = book.pubDate,
                    bookUpdateAt = book.updatedAt,
                    rating = book.rating)
            }

        val libraryDto = LibraryDto(
            libraryId = library.id,
            libraryName = library.libraryName,
            createdAt = library.createdAt,
            updatedAt = library.updatedAt)


        val libraryBookResponseDto = LibraryBookListResponseDto(
            allLibraryBooks = allLibraryBooksDto,
            libraryDto = libraryDto,
            totalCount = books.totalElements,
            size = books.size.toLong(),
            page = books.number.toLong() + 1)



        return libraryBookResponseDto
    }
}
