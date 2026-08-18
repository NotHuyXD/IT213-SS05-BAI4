package com.example.ai.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EtlPipelineService {

    private final VectorStore vectorStore;

    public EtlPipelineService(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    public void runEtlPipeline(Resource jsonResource) {
        // 1. EXTRACT: Đọc dữ liệu từ file JSON
        JsonReader jsonReader = new JsonReader(jsonResource, "content", "category");
        List<Document> documents = jsonReader.get();

        // 2. TRANSFORM: Phân tách tài liệu thành các khối nhỏ hơn (Chunks) để lưu trữ hiệu quả
        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
        List<Document> splitDocuments = splitter.apply(documents);

        // 3. LOAD: Lưu các khối dữ liệu (Embeddings) vào Vector Database
        vectorStore.accept(splitDocuments);
    }
}