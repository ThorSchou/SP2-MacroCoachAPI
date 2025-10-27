package app;

import app.security.controllers.AccessController;
import app.security.routes.SecurityRoutes;

import app.routes.ProfileRoutes;
import app.routes.RecipeRoutes;
import app.routes.DayRoutes;
import app.routes.AiRoutes;
import app.routes.PantryRoutes;

import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Main {
    public static void main(String[] args) {
        var access = new AccessController();

        Javalin app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();

            // Base path
            config.router.contextPath = "/api";

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
