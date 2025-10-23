package dat.routes;

import dat.controllers.DayController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DayRoutes {
    private static final DayController c = new DayController();
    public static EndpointGroup getRoutes() {
        return () -> path("/days", () -> {
            get("/{date}", c::get, Role.USER);
            post("/{date}/meals", c::addMeal, Role.USER);
            patch("/meals/{mealId}", c::patchMeal, Role.USER);
            delete("/meals/{mealId}", c::deleteMeal, Role.USER);
            get("/summary", c::rangeSummary, Role.USER);
        });
    }
}
