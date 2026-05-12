package com.aiwalletplatform.investigation.service;

import com.aiwalletplatform.investigation.entity.InvestigationCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * Investigation Service using RAG (Retrieval-Augmented Generation)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InvestigationService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    /**
     * Query evidence using RAG pattern
     */
    public String queryInvestigation(String query, int topK) {
        log.info("Querying investigation with RAG: {}", query);

        // Retrieve relevant documents from vector store
        var searchResults = vectorStore.similaritySearch(query, topK);
        
        String contextFromVectorDb = searchResults.stream()
                .map(doc -> doc.getContent())
                .collect(Collectors.joining("\n\n"));

        // Use RAG with retrieved context
        String ragPrompt = String.format("""
                Based on the following evidence and transaction history:
                
                %s
                
                Please analyze and answer this investigation query:
                %s
                
                Provide detailed findings with specific references to the evidence.
                """, contextFromVectorDb, query);

        String analysis = chatClient.prompt()
                .user(ragPrompt)
                .call()
                .content();

        log.info("Investigation analysis completed");
        return analysis;
    }

    /**
     * Generate investigation report using AI
     */
    public String generateInvestigationReport(InvestigationCase investigationCase) {
        String prompt = String.format("""
                Generate a compliance investigation report for:
                - Case Number: %s
                - Wallet ID: %s
                - User ID: %s
                - Status: %s
                - Description: %s
                
                Include findings, recommendations, and compliance notes.
                """, investigationCase.getCaseNumber(), 
                    investigationCase.getWalletId(),
                    investigationCase.getUserId(),
                    investigationCase.getStatus(),
                    investigationCase.getDescription());

        String report = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        log.info("Investigation report generated for case: {}", investigationCase.getCaseNumber());
        return report;
    }
}
