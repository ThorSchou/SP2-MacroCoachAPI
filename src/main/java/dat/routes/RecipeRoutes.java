package dat.routes;

import dat.controllers.RecipeController;
import dat.security.controllers.AccessController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RecipeRoutes {
    private static final RecipeController c = new RecipeController();
    private static final AccessController access = new AccessController();

    public static EndpointGroup getRoutes() {
        return () -> path("/recipes", () -> {
            // Public
            get(c::list);
            get("/{id}", c::get);

            // Protected (requires Authorization: Bearer <token>)
            post(c::create, Role.USER, Role.ADMIN);
            put("/{id}", c::update, Role.USER, Role.ADMIN);
            delete("/{id}", c::delete, Role.USER, Role.ADMIN);

            before(access::accessHandler);
        });
    }
}
