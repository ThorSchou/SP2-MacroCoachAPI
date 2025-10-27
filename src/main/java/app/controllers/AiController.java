package app.controllers;

import app.dtos.DayPlanRequest;
import app.services.AiService;
import io.javalin.http.Context;

public class AiController {
    private final AiService service = new AiService();

    public void generateDayPlan(Context ctx) {
        try {
            DayPlanRequest req = ctx.bodyAsClass(DayPlanRequest.class);
            String text = service.generateDayPlan(req);
            ctx.json(text); // plain string response; front-end shows it as text
        } catch (IllegalStateException e) {
            // OPENAI_ENABLED=true but key missing, etc.
            ctx.status(503).json(new ErrorDto("AI_NOT_CONFIGURED", e.getMessage()));
        } catch (RuntimeException e) {
            String msg = e.getMessage() == null ? "" : e.getMessage();
            if (msg.startsWith("OPENAI_ERROR:")) {
                // Most common: 429 quota exceeded / rate limited
                ctx.status(429).json(new ErrorDto("OPENAI_QUOTA", msg.replace("OPENAI_ERROR:", "").trim()));
            } else {
                ctx.status(500).json(new ErrorDto("AI_FAILED", msg));
            }
        } catch (Exception e) {
            ctx.status(500).json(new ErrorDto("AI_FAILED", e.getMessage()));
        }
    }

    // DTO to keep error responses consistent
    public record ErrorDto(String code, String message) {}
}
