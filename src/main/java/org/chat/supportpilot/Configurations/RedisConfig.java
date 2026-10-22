package org.chat.supportpilot.Configurations;

import org.chat.supportpilot.Repository.RedisMemoryRepository;
import org.chat.supportpilot.Repository.RedisMemoryRespository;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.RedisClient;

@Configuration
public class RedisConfig {

    @Bean
    public RedisClient redisClient() {
        return RedisClient.builder().hostAndPort("localhost", 6379).build();
    }
    @Bean
    public VectorStore vectorStore(RedisClient redisClient, EmbeddingModel embeddingModel) {
        return RedisVectorStore.builder(redisClient, embeddingModel)
                .indexName("supportpilot-docs")
                .prefix("doc:")
                .metadataFields(RedisVectorStore.MetadataField.tag("source"))
                .initializeSchema(true)
                .build();
    }

    @Bean
    public ChatMemory chatMemory(RedisMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();
    }
}
