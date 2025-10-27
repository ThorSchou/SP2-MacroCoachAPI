package app.routes;

import app.controllers.AiController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AiRoutes {

    private static final AiController c = new AiController();

    public static EndpointGroup getRoutes() {
        return () -> path("/ai", () -> {
            post("/day-plan", c::generateDayPlan, Role.USER, Role.ADMIN);
        });
    }
}
