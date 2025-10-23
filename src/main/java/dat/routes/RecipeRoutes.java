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
            get("/", c::list);               // public
            get("/{id}", c::get);            // public
            before(access::accessHandler);   // <— protected below
            post("/", c::create);
            put("/{id}", c::update);
            delete("/{id}", c::delete);
        });
    }
}
