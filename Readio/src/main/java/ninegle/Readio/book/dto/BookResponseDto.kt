package ninegle.Readio.book.dto

import lombok.Builder
import ninegle.Readio.book.dto.author.AuthorDto
import ninegle.Readio.category.dto.CategoryDto
import ninegle.Readio.publisher.dto.PublisherDto
import java.time.LocalDate

@Builder
data class BookResponseDto(
    val id: Long,
    val name: String,
    val description: String,
    val image: String,
    val isbn: String?,
    val ecn: String?,
    val pubDate: LocalDate,
    val category: CategoryDto,
    val publisher: PublisherDto,
    val author: AuthorDto
)
