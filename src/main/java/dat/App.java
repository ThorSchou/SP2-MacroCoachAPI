package dat;

import dat.security.controllers.AccessController;
import dat.security.routes.SecurityRoutes;

import dat.routes.ProfileRoutes;
import dat.routes.RecipeRoutes;
import dat.routes.DayRoutes;
import dat.routes.AiRoutes;
import dat.routes.PantryRoutes;

import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {
    public static void main(String[] args) {
        var access = new AccessController();

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();

            // Base path
            config.router.contextPath = "/api";

            // Register all routes in the same style you showed
            config.router.apiBuilder(() -> {
                // --- Security ---
                SecurityRoutes.getSecurityRoutes().addEndpoints();
                SecurityRoutes.getSecuredRoutes().addEndpoints();

                path("/", () -> {
                    ProfileRoutes.getRoutes().addEndpoints();
                    RecipeRoutes.getRoutes().addEndpoints();
                    DayRoutes.getRoutes().addEndpoints();
                    AiRoutes.getRoutes().addEndpoints();
                    PantryRoutes.getRoutes().addEndpoints();

                    // Health check = /api/health
                    get("/health", ctx -> ctx.json(java.util.Map.of("status", "ok")));
                });
            });
        });

        // Apply role-based access control
        app.beforeMatched(access::accessHandler);

        app.start("0.0.0.0", 7000);
    }
}
