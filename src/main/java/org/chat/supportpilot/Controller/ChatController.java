package org.chat.supportpilot.Controller;

import jakarta.validation.Valid;
import org.chat.supportpilot.Records.ChatReply;
import org.chat.supportpilot.Records.ChatRequest;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.UUID;

@RestController
public class ChatController {

    private static final String p = "You are a customer support assistant. Answer only from provided contex " +
            "if you are unsure or the context does not contain answer offer to connect with a human agent. Also keep the answer short and polite";

    private final ChatClient chatClient;
    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory){
        this.chatClient = builder.defaultSystem(p)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }

    @PostMapping("/chat")
    public ChatReply chat(@Valid @RequestBody ChatRequest request){
        String convId = giveId(request);
        String answer = chatClient.prompt()
                .user(request.message())
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID, convId))
                .call().content();
        return new ChatReply(answer,convId);
    }

    @PostMapping(value="/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> stream(@Valid @RequestBody ChatRequest req){
        String convId = giveId(req);
        Flux<String> tokens = chatClient.prompt()
                .user(req.message()).advisors(a->a.param(ChatMemory.CONVERSATION_ID, convId))
                .stream().content();
        return ResponseEntity.ok().header("X-conversaton-Id", convId).body(tokens);
    }

    public String giveId(ChatRequest req){
        return req.conversationId()!=null ? req.conversationId() : UUID.randomUUID().toString();
    }
}
