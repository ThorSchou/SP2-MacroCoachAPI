package dat.routes;

import dat.controllers.AiController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AiRoutes {
    private static final AiController c = new AiController();
    public static EndpointGroup getRoutes() {
        return () -> path("/ai", () -> {
            post("/day-plan", c::dayPlan, Role.USER);
        });
    }
}
