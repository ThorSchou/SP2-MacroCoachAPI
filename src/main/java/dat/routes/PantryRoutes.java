package dat.routes;

import dat.controllers.PantryController;
import dat.security.controllers.AccessController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class PantryRoutes {
    private static final PantryController c = new PantryController();
    private static final AccessController access = new AccessController();

    public static EndpointGroup getRoutes() {
        return () -> path("/pantry", () -> {
            before(access::accessHandler);

            get("", c::list, Role.USER, Role.ADMIN);   // GET /api/pantry
            post("", c::create, Role.USER, Role.ADMIN); // POST /api/pantry
        });
    }
}
