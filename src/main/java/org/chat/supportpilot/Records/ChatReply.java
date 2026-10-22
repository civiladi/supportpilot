package org.chat.supportpilot.Records;

import java.util.List;

public record ChatReply(String answer, String conversationId,
List<String> sources, boolean cached) {
}
