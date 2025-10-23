package dat.routes;

import dat.controllers.RecipeController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RecipeRoutes {
    private static final RecipeController c = new RecipeController();
    public static EndpointGroup getRoutes() {
        return () -> path("/recipes", () -> {
            get("/", c::list, Role.ANYONE);
            get("/{id}", c::get, Role.ANYONE);
            post("/", c::create, Role.USER);
            put("/{id}", c::update, Role.USER);
            delete("/{id}", c::delete, Role.ADMIN);
        });
    }
}
