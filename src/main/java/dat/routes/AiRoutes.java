package dat.routes;

import dat.controllers.AiController;
import dat.security.controllers.AccessController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AiRoutes {
    private static final AiController c = new AiController();
    private static final AccessController access = new AccessController();

    public static EndpointGroup getRoutes() {
        return () -> path("/ai", () -> {
            before(access::accessHandler);
            post("/day-plan", c::dayPlan);
        });
    }
}
