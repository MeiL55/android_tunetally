package com.example.tunetally;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;


public class openAI_API {
    //private final static String APIKEY = "";
    private OpenAiService aiService;
    private final String systemMessage;
    public openAI_API (String systemMessage) {
        this.aiService = new OpenAiService(APIKEY);
        this.systemMessage = systemMessage;
    }
    public interface LLMResponse{
        void receive(String response);
    }
    public void getCompletionRequest(String message, LLMResponse response){
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), message));
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .maxTokens(256)
                .build();
        new Thread(() -> {
            try {
                ChatCompletionResult result = aiService.createChatCompletion(chatCompletionRequest);
                if (result.getChoices().isEmpty()) {
                    Log.e("LLM", "Chat completion was empty and there is no response.");
                } else {
                    String aiResponse = result.getChoices().get(0).getMessage().getContent();
                    response.receive(aiResponse);
                }
            } catch (Exception e) {
                Log.e("LLM", "Error occured during char completion: " + e.getMessage());
            }
        }).start();
    }
}
