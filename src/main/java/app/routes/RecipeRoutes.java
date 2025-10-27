package app.routes;

import app.controllers.RecipeController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RecipeRoutes {
    private static final RecipeController c = new RecipeController();

    public static EndpointGroup getRoutes() {
        return () -> path("/recipes", () -> {
            // public
            get(c::list);
            get("/{id}", c::get);

            // authenticated
            post(c::create, Role.USER, Role.ADMIN);
            put("/{id}", c::update, Role.USER, Role.ADMIN);
            delete("/{id}", c::delete, Role.USER, Role.ADMIN);
        });
    }
}
