package dat;

import dat.routes.*;
import dat.security.routes.SecurityRoutes;
import dat.security.enums.Role;
import io.javalin.Javalin;

import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(cfg -> {
            cfg.http.defaultContentType = "application/json";
            // no cfg.accessManager(...)
        }).start("0.0.0.0", 7000);

        app.routes(() -> {
            path("/api", () -> {
                SecurityRoutes.getSecurityRoutes().addEndpoints();   // public auth routes
                SecurityRoutes.getSecuredRoutes().addEndpoints();    // its own guard inside
                get("/health", ctx -> ctx.json(Map.of("status","ok")), dat.security.enums.Role.ANYONE);

                ProfileRoutes.getRoutes().addEndpoints();
                RecipeRoutes.getRoutes().addEndpoints();
                DayRoutes.getRoutes().addEndpoints();
                AiRoutes.getRoutes().addEndpoints();
            });
        });
    }
}
