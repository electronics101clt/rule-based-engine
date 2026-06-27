package com.eliza;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import com.google.gson.*;

/**
 * LLM Configuration Manager - Similar to openclaw's config system
 */
public class LLMConfig {

    private static final String CONFIG_DIR = System.getProperty("user.home") + "/.ubuntu-assistant";
    private static final String CONFIG_FILE = CONFIG_DIR + "/config.json";

    private JsonObject config;
    private Gson gson;

    public LLMConfig() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        loadConfig();
    }

    private void loadConfig() {
        try {
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                String json = new String(Files.readAllBytes(configFile.toPath()));
                config = gson.fromJson(json, JsonObject.class);
            } else {
                createDefaultConfig();
            }
        } catch (IOException e) {
            createDefaultConfig();
        }
    }

    private void createDefaultConfig() {
        config = new JsonObject();

        // Meta information
        JsonObject meta = new JsonObject();
        meta.addProperty("version", "1.0.0");
        meta.addProperty("lastModified", new Date().toString());
        config.add("meta", meta);

        // LLM providers
        JsonObject llm = new JsonObject();
        llm.addProperty("enabled", false);
        llm.addProperty("provider", "none");
        llm.addProperty("model", "");
        config.add("llm", llm);

        // Available providers
        JsonObject providers = new JsonObject();

        JsonObject claude = new JsonObject();
        claude.addProperty("apiKey", "");
        claude.addProperty("defaultModel", "claude-sonnet-4-5-20250929");
        providers.add("claude", claude);

        JsonObject openai = new JsonObject();
        openai.addProperty("apiKey", "");
        openai.addProperty("defaultModel", "gpt-4");
        providers.add("openai", openai);

        JsonObject gemini = new JsonObject();
        gemini.addProperty("apiKey", "");
        gemini.addProperty("defaultModel", "gemini-2.5-flash");
        providers.add("gemini", gemini);

        JsonObject ollama = new JsonObject();
        ollama.addProperty("endpoint", "http://localhost:11434");
        ollama.addProperty("defaultModel", "llama2");
        providers.add("ollama", ollama);

        config.add("providers", providers);

        saveConfig();
    }

    public void saveConfig() {
        try {
            // Create directory if it doesn't exist
            File dir = new File(CONFIG_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Update last modified
            if (config.has("meta")) {
                config.getAsJsonObject("meta").addProperty("lastModified", new Date().toString());
            }

            // Write config
            String json = gson.toJson(config);
            Files.write(Paths.get(CONFIG_FILE), json.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getters and setters
    public boolean isLLMEnabled() {
        return config.has("llm") && config.getAsJsonObject("llm").get("enabled").getAsBoolean();
    }

    public void setLLMEnabled(boolean enabled) {
        if (!config.has("llm")) {
            config.add("llm", new JsonObject());
        }
        config.getAsJsonObject("llm").addProperty("enabled", enabled);
        saveConfig();
    }

    public String getProvider() {
        if (config.has("llm")) {
            return config.getAsJsonObject("llm").get("provider").getAsString();
        }
        return "none";
    }

    public void setProvider(String provider) {
        if (!config.has("llm")) {
            config.add("llm", new JsonObject());
        }
        config.getAsJsonObject("llm").addProperty("provider", provider);
        saveConfig();
    }

    public String getModel() {
        if (config.has("llm")) {
            return config.getAsJsonObject("llm").get("model").getAsString();
        }
        return "";
    }

    public void setModel(String model) {
        if (!config.has("llm")) {
            config.add("llm", new JsonObject());
        }
        config.getAsJsonObject("llm").addProperty("model", model);
        saveConfig();
    }

    public String getApiKey(String provider) {
        if (config.has("providers")) {
            JsonObject providers = config.getAsJsonObject("providers");
            if (providers.has(provider)) {
                JsonObject providerObj = providers.getAsJsonObject(provider);
                if (providerObj.has("apiKey")) {
                    return providerObj.get("apiKey").getAsString();
                }
            }
        }
        return "";
    }

    public void setApiKey(String provider, String apiKey) {
        if (!config.has("providers")) {
            config.add("providers", new JsonObject());
        }
        JsonObject providers = config.getAsJsonObject("providers");
        if (!providers.has(provider)) {
            providers.add(provider, new JsonObject());
        }
        providers.getAsJsonObject(provider).addProperty("apiKey", apiKey);
        saveConfig();
    }

    public String getOllamaEndpoint() {
        if (config.has("providers")) {
            JsonObject providers = config.getAsJsonObject("providers");
            if (providers.has("ollama")) {
                return providers.getAsJsonObject("ollama").get("endpoint").getAsString();
            }
        }
        return "http://localhost:11434";
    }

    public void setOllamaEndpoint(String endpoint) {
        if (!config.has("providers")) {
            config.add("providers", new JsonObject());
        }
        JsonObject providers = config.getAsJsonObject("providers");
        if (!providers.has("ollama")) {
            providers.add("ollama", new JsonObject());
        }
        providers.getAsJsonObject("ollama").addProperty("endpoint", endpoint);
        saveConfig();
    }

    public String getDefaultModel(String provider) {
        if (config.has("providers")) {
            JsonObject providers = config.getAsJsonObject("providers");
            if (providers.has(provider)) {
                JsonObject providerObj = providers.getAsJsonObject(provider);
                if (providerObj.has("defaultModel")) {
                    return providerObj.get("defaultModel").getAsString();
                }
            }
        }
        return "";
    }

    public String[] getAvailableProviders() {
        return new String[]{"claude", "openai", "gemini", "ollama"};
    }

    public String getConfigPath() {
        return CONFIG_FILE;
    }
}
