package ninegle.Readio.global.exception.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
public record ErrorResponse(int status, String code, String message, String path) {

}
