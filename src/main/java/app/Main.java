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

        app.options("/*", ctx -> {
            ctx.header("Access-Control-Allow-Origin", "http://localhost:5173");
            ctx.header("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
            ctx.status(200);
        });

        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "http://localhost:5173");
            ctx.header("Access-Control-Allow-Headers", "Content-Type,Authorization");
        });

        // Apply role-based access control
        app.beforeMatched(access::accessHandler);

        app.start("0.0.0.0", 7070);
    }
}
