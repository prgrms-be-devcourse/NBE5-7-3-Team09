package ninegle.Readio.publisher.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PublisherRequestDto {

	@NotBlank(message = "출판사를 입력해주세요.")
	private String name;
}
