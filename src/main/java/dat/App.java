package dat;

import dat.routes.*;
import dat.security.routes.SecurityRoutes;
import dat.security.enums.Role;
import io.javalin.Javalin;

import static io.javalin.apibuilder.ApiBuilder.*;

public class App {
    public static void main(String[] args) {
        Javalin app = Javalin.create(cfg -> {
            cfg.http.defaultContentType = "application/json";
            // optional CORS in v5 (uncomment if you want it now):
            // cfg.plugins.enableCors(cors -> cors.add(it -> it.anyHost()));
            cfg.accessManager(dat.security.config.AccessManager.getInstance());
        }).start("0.0.0.0", 7000);

        dat.exceptions.ErrorHandler.install(app);

        app.routes(() -> {
            path("/api", () -> {
                // security
                addEndpoints(SecurityRoutes.getSecurityRoutes());
                addEndpoints(SecurityRoutes.getSecuredRoutes());

                // health
                get("/health", ctx -> ctx.json(java.util.Map.of("status","ok")), Role.ANYONE);

                // domain
                addEndpoints(ProfileRoutes.getRoutes());
                addEndpoints(RecipeRoutes.getRoutes());
                addEndpoints(DayRoutes.getRoutes());
                addEndpoints(AiRoutes.getRoutes());
            });
        });
    }
}
