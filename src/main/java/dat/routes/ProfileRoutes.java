package dat.routes;

import dat.controllers.ProfileController;
import dat.security.enums.Role; // your enum with USER, ADMIN
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ProfileRoutes {
    private static final ProfileController c = new ProfileController();

    public static EndpointGroup getRoutes() {
        return () -> path("/profiles", () -> {

            get("/me",   c::me,      Role.USER, Role.ADMIN);
            put("/me",   c::replace, Role.USER, Role.ADMIN);
            patch("/me", c::patch,   Role.USER, Role.ADMIN);
        });
    }
}
