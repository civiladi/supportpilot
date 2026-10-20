package org.chat.supportpilot.Records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChatRequest(
        @NotBlank @Size(max=2000) String message,
        @Pattern(regexp = "^[A-Za-z0-9-]{1,64}$") String conversationId
) {
}
