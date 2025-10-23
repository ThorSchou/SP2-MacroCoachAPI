package dat.controllers;

import dat.dtos.DayPlanRequest;
import dat.services.AiService;
import io.javalin.http.Context;

public class AiController {
    private final AiService svc = new AiService();
    public void dayPlan(Context ctx) throws Exception {
        var req = ctx.bodyAsClass(DayPlanRequest.class);
        ctx.json(svc.dayPlan(req));
    }
}
