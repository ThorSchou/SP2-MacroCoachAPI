package dat.ai;

import com.theokanning.openai.service.OpenAiService;


import java.time.Duration;

public final class OpenAIClientFactory {
    private static volatile OpenAiService instance;

    private OpenAIClientFactory() {}

    public static OpenAiService get() {
        if (instance == null) {
            synchronized (OpenAIClientFactory.class) {
                if (instance == null) {
                    String apiKey = System.getenv("OPENAI_API_KEY");
                    if (apiKey == null || apiKey.isBlank()) {
                        throw new IllegalStateException("OPENAI_API_KEY env var is not set");
                    }

                    instance = new OpenAiService(apiKey, Duration.ofSeconds(60));
                }
            }
        }
        return instance;
    }
}
