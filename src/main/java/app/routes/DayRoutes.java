package app.routes;

import app.controllers.DayController;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DayRoutes {
    private static final DayController c = new DayController();

    public static EndpointGroup getRoutes() {
        return () -> path("/days", () -> {
            get("/summary", c::summary, Role.USER, Role.ADMIN);

            get("/{date}", c::get, Role.USER, Role.ADMIN);
            post("/{date}/meals", c::addMeal, Role.USER, Role.ADMIN);
            patch("/meals/{mealId}", c::updateMeal, Role.USER, Role.ADMIN);
            delete("/meals/{mealId}", c::deleteMeal, Role.USER, Role.ADMIN);
        });
    }
}
