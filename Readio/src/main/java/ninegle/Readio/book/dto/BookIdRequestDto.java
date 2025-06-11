package ninegle.Readio.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Readio - BookIdRequestDto
 * create date:    25. 5. 13.
 * last update:    25. 5. 13.
 * author:  gigol
 * purpose: 
 */
@Getter
@NoArgsConstructor
public class BookIdRequestDto {


	@NotNull(message = "도서 ID는 필수입니다.")
	@Min(value = 1, message = "도서 ID는 1 이상의 값이어야 합니다.")
	private Long id;
}
