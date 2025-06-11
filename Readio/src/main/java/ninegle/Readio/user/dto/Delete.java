package ninegle.Readio.user.dto;

import lombok.Builder;

@Builder

public record Delete(String refreshToken, String email, String password) {
}
