package org.chat.supportpilot.Repository;

import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.RedisClient;

import java.util.ArrayList;
import java.util.List;

@Component
public class RedisMemoryRepository implements ChatMemoryRepository {
    private static final String PREFIX = "chat:";
    private static final String USER = "U|";
    private static final String ASSISTANT = "A|";

    private final RedisClient client;
    private final long ttlSeconds;

    public RedisMemoryRepository(RedisClient client,
                                  @Value("${supportpilot.memory.ttl-minutes:30}") long ttlMinutes){
        this.client = client;
        this.ttlSeconds = ttlMinutes * 60;
    }

    @Override
    public List<String> findConversationIds(){
        return List.of();
    }

    @Override
    public List<Message> findByConversationId(String conversationId){
        List<String> stored = client.lrange(PREFIX + conversationId, 0, -1);
        List<Message> messages = new ArrayList<>();
        for(String line: stored){
            if(line.startsWith(USER)){
                messages.add(new UserMessage(line.substring(USER.length())));
            }
            else if(line.startsWith(ASSISTANT)){
                messages.add(new AssistantMessage(line.substring(ASSISTANT.length())));
            }
        }
        return messages;
    }

    @Override
    public void saveAll(String conversationId, List<Message> messages){
        String key = PREFIX + conversationId;
        List<String> lines = new ArrayList<>();
        for(Message m: messages){
            String text = m.getText();
            if(text == null || text.isBlank())
                continue;
            if(m.getMessageType() == MessageType.USER){
                lines.add(USER+text);
            }
            else if(m.getMessageType() == MessageType.ASSISTANT){
                lines.add(ASSISTANT+text);
            }
        }
        client.del(key);
        if(!lines.isEmpty()){
            client.rpush(key, lines.toArray(new String[0]));
            client.expire(key, ttlSeconds);
        }
    }

    @Override
    public void deleteByConversationId(String conversationId){
        client.del(PREFIX+conversationId);
    }
}
