package dat.routes;

import dat.controllers.DayController;
import dat.security.controllers.AccessController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DayRoutes {
    private static final DayController c = new DayController();
    private static final AccessController access = new AccessController();

    public static EndpointGroup getRoutes() {
        return () -> path("/days", () -> {
            before(access::accessHandler);
            get("/{date}", c::get);
            post("/{date}/meals", c::addMeal);
            patch("/meals/{mealId}", c::patchMeal);
            delete("/meals/{mealId}", c::deleteMeal);
            get("/summary", c::rangeSummary);
        });
    }
}
