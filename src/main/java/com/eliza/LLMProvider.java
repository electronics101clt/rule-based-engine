package com.eliza;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.*;

/**
 * LLM Provider - Handles requests to various LLM APIs
 */
public class LLMProvider {

    private LLMConfig config;
    private Gson gson;

    public LLMProvider(LLMConfig config) {
        this.config = config;
        this.gson = new Gson();
    }

    public String query(String userMessage) {
        if (!config.isLLMEnabled()) {
            return null;
        }

        String provider = config.getProvider();

        try {
            switch (provider) {
                case "claude":
                    return queryClaude(userMessage);
                case "openai":
                    return queryOpenAI(userMessage);
                case "gemini":
                    return queryGemini(userMessage);
                case "ollama":
                    return queryOllama(userMessage);
                default:
                    return null;
            }
        } catch (Exception e) {
            System.err.println("LLM Error: " + e.getMessage());
            return null;
        }
    }

    private String queryClaude(String userMessage) throws IOException {
        String apiKey = config.getApiKey("claude");
        if (apiKey.isEmpty()) {
            return null;
        }

        String model = config.getModel();
        if (model.isEmpty()) {
            model = config.getDefaultModel("claude");
        }

        URL url = new URL("https://api.anthropic.com/v1/messages");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-api-key", apiKey);
        conn.setRequestProperty("anthropic-version", "2023-06-01");
        conn.setDoOutput(true);

        JsonObject request = new JsonObject();
        request.addProperty("model", model);
        request.addProperty("max_tokens", 1024);

        JsonArray messages = new JsonArray();
        JsonObject message = new JsonObject();
        message.addProperty("role", "user");
        message.addProperty("content", "You are a helpful Ubuntu/Linux assistant. Answer this question concisely: " + userMessage);
        messages.add(message);
        request.add("messages", messages);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(request).getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonObject response = gson.fromJson(br, JsonObject.class);
                JsonArray content = response.getAsJsonArray("content");
                if (content.size() > 0) {
                    return content.get(0).getAsJsonObject().get("text").getAsString();
                }
            }
        }

        return null;
    }

    private String queryOpenAI(String userMessage) throws IOException {
        String apiKey = config.getApiKey("openai");
        if (apiKey.isEmpty()) {
            return null;
        }

        String model = config.getModel();
        if (model.isEmpty()) {
            model = config.getDefaultModel("openai");
        }

        URL url = new URL("https://api.openai.com/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setDoOutput(true);

        JsonObject request = new JsonObject();
        request.addProperty("model", model);
        request.addProperty("max_tokens", 1024);

        JsonArray messages = new JsonArray();
        JsonObject systemMsg = new JsonObject();
        systemMsg.addProperty("role", "system");
        systemMsg.addProperty("content", "You are a helpful Ubuntu/Linux assistant.");
        messages.add(systemMsg);

        JsonObject userMsg = new JsonObject();
        userMsg.addProperty("role", "user");
        userMsg.addProperty("content", userMessage);
        messages.add(userMsg);

        request.add("messages", messages);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(request).getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonObject response = gson.fromJson(br, JsonObject.class);
                JsonArray choices = response.getAsJsonArray("choices");
                if (choices.size() > 0) {
                    return choices.get(0).getAsJsonObject()
                        .getAsJsonObject("message")
                        .get("content").getAsString();
                }
            }
        }

        return null;
    }

    private String queryGemini(String userMessage) throws IOException {
        String apiKey = config.getApiKey("gemini");
        if (apiKey.isEmpty()) {
            return null;
        }

        String model = config.getModel();
        if (model.isEmpty()) {
            model = config.getDefaultModel("gemini");
        }

        URL url = new URL("https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JsonObject request = new JsonObject();
        JsonArray contents = new JsonArray();
        JsonObject content = new JsonObject();
        JsonArray parts = new JsonArray();
        JsonObject part = new JsonObject();
        part.addProperty("text", "You are a helpful Ubuntu/Linux assistant. Answer this question concisely: " + userMessage);
        parts.add(part);
        content.add("parts", parts);
        contents.add(content);
        request.add("contents", contents);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(request).getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonObject response = gson.fromJson(br, JsonObject.class);
                JsonArray candidates = response.getAsJsonArray("candidates");
                if (candidates.size() > 0) {
                    JsonObject candidate = candidates.get(0).getAsJsonObject();
                    JsonArray responseParts = candidate.getAsJsonObject("content").getAsJsonArray("parts");
                    if (responseParts.size() > 0) {
                        return responseParts.get(0).getAsJsonObject().get("text").getAsString();
                    }
                }
            }
        }

        return null;
    }

    private String queryOllama(String userMessage) throws IOException {
        String endpoint = config.getOllamaEndpoint();
        String model = config.getModel();
        if (model.isEmpty()) {
            model = config.getDefaultModel("ollama");
        }

        URL url = new URL(endpoint + "/api/generate");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        JsonObject request = new JsonObject();
        request.addProperty("model", model);
        request.addProperty("prompt", "You are a helpful Ubuntu/Linux assistant. Answer this question concisely: " + userMessage);
        request.addProperty("stream", false);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(gson.toJson(request).getBytes());
        }

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                JsonObject response = gson.fromJson(br, JsonObject.class);
                return response.get("response").getAsString();
            }
        }

        return null;
    }
}
