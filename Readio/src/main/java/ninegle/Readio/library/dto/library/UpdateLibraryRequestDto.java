package ninegle.Readio.library.dto.library;

import jakarta.validation.constraints.NotBlank;

public record UpdateLibraryRequestDto(
	@NotBlank(message = "라이브러리 이름은 필수 입력값입니다.") String libraryName) {
}
