package org.chat.supportpilot.Service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReadFileService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter split = TokenTextSplitter.builder().build();

    public ReadFileService(VectorStore vec){
        this.vectorStore = vec;
    }

    public int readFile(String filename, byte[] content){
        List<Document> pages = new TikaDocumentReader(new ByteArrayResource(content)).get();
        pages.forEach(p -> p.getMetadata().put("source",filename));
        List<Document> chunks = split.apply(pages);
        vectorStore.add(chunks);
        return chunks.size();
    }
}
