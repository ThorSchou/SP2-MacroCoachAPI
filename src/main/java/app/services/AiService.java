package app.services;

import com.theokanning.openai.OpenAiHttpException;
import com.theokanning.openai.completion.chat.ChatCompletionChoice;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import app.dtos.DayPlanRequest;
import app.utils.PromptBuilder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class AiService {

    private final boolean enabled;
    private final OpenAiService openAi;

    public AiService() {
        this.enabled = !"false".equalsIgnoreCase(System.getenv().getOrDefault("OPENAI_ENABLED", "true"));

        String key = System.getenv("OPENAI_API_KEY");
        if (enabled) {
            if (key == null || key.isBlank()) {
                throw new IllegalStateException("OPENAI_API_KEY missing while OPENAI_ENABLED=true");
            }
            // Keep a sensible timeout so Jetty threads don’t hang
            this.openAi = new OpenAiService(key, Duration.ofSeconds(60));
        } else {
            this.openAi = null;
        }
    }

    public String generateDayPlan(DayPlanRequest req) {
        if (!enabled) {
            // Fallback so your endpoint works without OpenAI/billing.
            String prompt = PromptBuilder.buildDayPlanPrompt(req);
            return """
                   [AI DISABLED - STUB OUTPUT]
                   You asked for a day plan with these constraints:
                   %s

                   Breakfast: Greek yogurt with oats and berries (approx 350 kcal, 30g protein)
                   Lunch: Chicken, rice & broccoli bowl (approx 650 kcal, 45g protein)
                   Dinner: Omelet with spinach & whole grain toast (approx 600 kcal, 35g protein)
                   Snacks: Banana + handful of almonds (approx 400 kcal, 10g protein)

                   Adjust portions to meet targets. Replace allergens as needed.
                   """.formatted(prompt);
        }

        String system = """
            You are MacroCoach, a nutrition assistant.
            Produce a clear, practical daily meal plan.
            Constraints:
            - If target calories are provided, keep plan within ±5%%.
            - Use provided pantry items when relevant.
            - Respect diet and allergies strictly.
            - Return short sections (Breakfast/Lunch/Dinner/Snacks) with rough macros per meal.
            - Keep under ~350 tokens.
            """;

        String user = PromptBuilder.buildDayPlanPrompt(req);

        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", system));
        messages.add(new ChatMessage("user", user.isBlank() ? "Create a simple high-protein daily meal plan." : user));

        ChatCompletionRequest chatReq = ChatCompletionRequest.builder()
                .model("gpt-3.5-turbo")
                .messages(messages)
                .temperature(0.7)
                .maxTokens(500)
                .build();

        try {
            List<ChatCompletionChoice> choices = openAi.createChatCompletion(chatReq).getChoices();
            if (choices == null || choices.isEmpty() || choices.get(0).getMessage() == null) {
                return "AI returned no content.";
            }
            return choices.get(0).getMessage().getContent();
        } catch (OpenAiHttpException e) {
            // let controller map to a proper HTTP status + message
            throw new RuntimeException("OPENAI_ERROR:" + e.getMessage(), e);
        }
    }
}
