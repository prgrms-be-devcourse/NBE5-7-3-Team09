package ninegle.Readio.book.mapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


import ninegle.Readio.book.domain.Book;
import ninegle.Readio.book.domain.Review;
import ninegle.Readio.book.dto.PaginationDto;
import ninegle.Readio.book.dto.reviewdto.ReviewListResponseDto;
import ninegle.Readio.book.dto.reviewdto.ReviewRequestDto;
import ninegle.Readio.book.dto.reviewdto.ReviewResponseDto;
import ninegle.Readio.book.dto.reviewdto.ReviewSummaryDto;
import ninegle.Readio.user.domain.User;

/**
 * Readio - ReviewMapper
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
@Component
public class ReviewMapper {
	public Review toEntity(ReviewRequestDto dto, User user, Book book){
		return Review.builder()
			.rating(dto.getRating())
			.text(dto.getText())
			.user(user)
			.book(book)
			.build();
	}

	public Review updateEntity(Review exsiting,ReviewRequestDto dto){
		return exsiting.toBuilder()
			.rating(dto.getRating())
			.text(dto.getText())
			.build();
	}

	public ReviewResponseDto toResponseDto(Review review){
		return ReviewResponseDto.builder()
			.id(review.getId())
			.email(review.getUser().getEmail())
			.rating(review.getRating())
			.text(review.getText())
			.createdAt(review.getCreatedAt())
			.updatedAt(review.getUpdatedAt())
			.build();
	}

	public List<ReviewResponseDto> toResponseDto(List<Review> reviews){
		ArrayList<ReviewResponseDto> reviewResponseDtos = new ArrayList<>();
		for (Review review : reviews) {
			reviewResponseDtos.add(toResponseDto(review));
		}
		return reviewResponseDtos;
	}

	public PaginationDto toPaginationDto(Long count,int page,int size){
		 return PaginationDto.builder()
			.totalPages((count.intValue()/size)+1)
			.size(size)
			.currentPage(page)
			.totalElements(count)
			 .build();
	}
	public ReviewSummaryDto toSummaryDto(Long count,BigDecimal avg){
		return ReviewSummaryDto.builder()
			.totalReviews(count.intValue())
			.averageRating(avg)
			.build();
	}

	public ReviewListResponseDto toReviewListResponseDto(List<ReviewResponseDto> reviewList, PaginationDto paginationDto, ReviewSummaryDto summaryDto) {

		return ReviewListResponseDto
			.builder()
			.reviews(reviewList)
			.summary(summaryDto)
			.pagination(paginationDto)
			.build();
	}
}
