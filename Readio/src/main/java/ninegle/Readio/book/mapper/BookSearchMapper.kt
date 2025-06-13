package ninegle.Readio.book.mapper


import ninegle.Readio.adapter.config.NCloudStorageConfig
import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.BookSearch
import ninegle.Readio.book.dto.booksearch.BookSearchResponseDto
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
class BookSearchMapper(
    private val nCloudStorageConfig: NCloudStorageConfig
) {

    fun BookSearch.toDto(): BookSearchResponseDto {
        return BookSearchResponseDto(
            id = this.id,
            name = this.name,
            image = nCloudStorageConfig.toImageUrl(this.image),
            categoryMajor = this.categoryMajor,
            categorySub = this.categorySub,
            authorName = this.author,
            rating = this.rating
        )
    }

    fun toResponseDto(bookList: MutableList<BookSearch>)= bookList.map { it.toDto() }.toMutableList()
}

fun Book.toEntity(): BookSearch {
    return BookSearch(
        id = this.id,
        name = this.name,
        image = this.image,
        expired = false,
        categoryMajor = this.category.major,
        categorySub = this.category.sub,
        author = this.author.name,
        rating = BigDecimal.ZERO
    )
}