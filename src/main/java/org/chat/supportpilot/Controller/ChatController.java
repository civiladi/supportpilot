package org.chat.supportpilot.Controller;

import jakarta.validation.Valid;
import org.chat.supportpilot.Auth;
import org.chat.supportpilot.Records.ChatReply;
import org.chat.supportpilot.Records.ChatRequest;
import org.chat.supportpilot.Tools.OrderTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class ChatController {

    private static final String p = "You are a customer support assistant. Answer only from provided context, conversation so far, and tool results." +
            " When the customer asks about an order, call the getOrderStatus tool. Never guess the order details. "+"If you are unsure or the context does not contain answer offer to connect with a human agent. Also keep the answer short and polite";

    private final ChatClient chatClient;
    private final OrderTool orderTool;

    public ChatController(ChatClient.Builder builder, ChatMemory chatMemory, VectorStore vec, OrderTool orderTool){
        this.chatClient = builder.defaultSystem(p)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        QuestionAnswerAdvisor.builder(vec).searchRequest(SearchRequest.builder()
                                .topK(4).similarityThreshold(0.5).build()).build())
                .build();
        this.orderTool = orderTool;
    }

    @PostMapping("/chat")
    public ChatReply chat(@Valid @RequestBody ChatRequest request,
                          @RequestHeader("X-User-Id") String userId){
//        validUser(userId);/// checking if the userId is valid or not.
        Auth.checkValid(userId);/// checking if the userId is valid or not.
        String convId = giveId(request);
        ChatClientResponse response = chatClient.prompt()
                .user(request.message())
                .tools(orderTool)
                .toolContext(Map.of("userId",userId))
                .advisors(a->a.param(ChatMemory.CONVERSATION_ID, convId))
                .call()
                .chatClientResponse();
        String answer = response.chatResponse().getResult().getOutput().getText();
        return new ChatReply(answer,convId,sourcesOf(response));
    }

    @PostMapping(value="/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<String>> stream(@Valid @RequestBody ChatRequest req){
        String convId = giveId(req);
        Flux<String> tokens = chatClient.prompt()
                .user(req.message()).advisors(a->a.param(ChatMemory.CONVERSATION_ID, convId))
                .stream().content();
        return ResponseEntity.ok().header("X-conversaton-Id", convId).body(tokens);
    }

    /// To check if the userId is valid or not
    private void validUser(String userId){
        if(!userId.matches("^[A-Za-z0-9-]{1,64}$")){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid userId");
        }
    }

    public String giveId(ChatRequest req){
        return req.conversationId()!=null ? req.conversationId() : UUID.randomUUID().toString();
    }
    private List<String> sourcesOf(ChatClientResponse res){
        Object docs = res.context().get(QuestionAnswerAdvisor.RETRIEVED_DOCUMENTS);
        if(!(docs instanceof List<?> list)){
            return List.of();
        }
        return list.stream().filter(Document.class::isInstance)
                .map(Document.class::cast)
                .map(d -> d.getMetadata().get("source"))
                .filter(java.util.Objects::nonNull)
                .map(Object::toString)
                .distinct()
                .toList();
    }
}
