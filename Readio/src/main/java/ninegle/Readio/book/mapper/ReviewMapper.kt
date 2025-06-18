package ninegle.Readio.book.mapper

import ninegle.Readio.book.domain.Book
import ninegle.Readio.book.domain.Review
import ninegle.Readio.book.dto.PaginationDto
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto
import ninegle.Readio.book.dto.reviewdto.ReviewResponseDto
import ninegle.Readio.book.dto.reviewdto.ReviewSummaryDto
import ninegle.Readio.user.domain.User
import org.springframework.stereotype.Component
import java.math.BigDecimal


@Component
class ReviewMapper {
    fun toEntity(dto: ReviewRequestDto, user: User, book: Book): Review {
        return Review(
            rating = dto.rating,
            text = dto.text,
            user = user,
            book = book)
    }

    fun toResponseDto(reviews: List<Review>): List<ReviewResponseDto> {
        val reviewResponseDtos = ArrayList<ReviewResponseDto>()
        for (review in reviews) {
            reviewResponseDtos.add(review.toResponseDto())
        }
        return reviewResponseDtos
    }

    fun toPaginationDto(count: Long, page: Int, size: Int): PaginationDto {
        return PaginationDto(
            count,
            (count.toInt() / size) + 1,
            page,
            size
        )
    }

    fun toSummaryDto(count: Long, avg: BigDecimal): ReviewSummaryDto {
        return ReviewSummaryDto(avg,count.toInt())
    }

    fun toReviewListResponseDto(
        reviewList: List<ReviewResponseDto>,
        paginationDto: PaginationDto,
        summaryDto: ReviewSummaryDto
    ): ReviewListResponseDto {
        return ReviewListResponseDto(reviewList,paginationDto,summaryDto)
    }
}

fun Review.toResponseDto() : ReviewResponseDto{
    return ReviewResponseDto(
        id = this.id!!,
        email = this.user.email,
        rating = this.rating,
        text = this.text,
        createdAt = this.createdAt!!,
        updatedAt = this.updatedAt!!
    )
}
