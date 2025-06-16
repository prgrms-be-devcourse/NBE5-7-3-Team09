package ninegle.Readio.library.dto.library

import jakarta.validation.constraints.NotBlank


data class NewLibraryRequestDto(

    @NotBlank(message = "라이브러리 이름은 필수 입력값입니다.")
    val libraryName: String)
