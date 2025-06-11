package ninegle.Readio.book.dto.reviewdto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Readio - ReviewRequestDto
 * create date:    25. 5. 12.
 * last update:    25. 5. 12.
 * author:  gigol
 * purpose: 
 */
@Getter
@NoArgsConstructor
public class ReviewRequestDto {


	@DecimalMin(value = "1.0", inclusive = true,message = "별점은 1.0이상 이어야 합니다.")
	@DecimalMax(value = "5.0", inclusive = true,message = "별점은 1.0이하 이어야 합니다.")
	private BigDecimal rating;

	@NotBlank(message = "리뷰 텍스트는 필수입니다.")
	private String text;

}
