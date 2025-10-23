package dat.routes;

import dat.controllers.ProfileController;
import dat.security.controllers.AccessController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ProfileRoutes {
    private static final ProfileController c = new ProfileController();
    private static final AccessController access = new AccessController();

    public static EndpointGroup getRoutes() {
        return () -> path("/profiles", () -> {
            before(access::accessHandler);
            get("/me", c::me);
            put("/me", c::replace);
            patch("/me", c::patch);
        });
    }
}
