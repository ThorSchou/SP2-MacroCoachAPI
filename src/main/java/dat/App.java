package dat;

import dat.security.controllers.AccessController;
import dat.security.routes.SecurityRoutes;
import dat.routes.ProfileRoutes;
import dat.routes.RecipeRoutes;
import dat.routes.DayRoutes;
import dat.routes.AiRoutes;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {
    public static void main(String[] args) {
        var access = new AccessController();

        Javalin app = Javalin.create(cfg -> {
            cfg.http.defaultContentType = "application/json";
            cfg.bundledPlugins.enableCors(cors -> cors.addRule(r -> r.anyHost()));

            cfg.router.apiBuilder(() -> {
                path("/api", () -> {
                    // Security groups (they return EndpointGroup and we mount them inline)
                    SecurityRoutes.getSecurityRoutes().addEndpoints();
                    SecurityRoutes.getSecuredRoutes().addEndpoints();

                    // Domain routes
                    ProfileRoutes.getRoutes().addEndpoints();
                    RecipeRoutes.getRoutes().addEndpoints();
                    DayRoutes.getRoutes().addEndpoints();
                    AiRoutes.getRoutes().addEndpoints();

                    // Health
                    get("/health", ctx -> ctx.json(java.util.Map.of("status", "ok")));
                });
            });
        }).start("0.0.0.0", 7000);

        // Must be beforeMatched in Javalin 6 so ctx.routeRoles() is available to AccessController
        app.beforeMatched(access::accessHandler);
    }
}
