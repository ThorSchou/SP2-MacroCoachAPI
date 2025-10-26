package dat.routes;

import dat.controllers.DayController;
import dat.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DayRoutes {
    private static final DayController ctrl = new DayController();

    public static EndpointGroup getRoutes() {
        return () -> {
            path("/days", () -> {
                // static path first
                get("/summary", ctrl.summary(), Role.USER);

                // then parameterized
                get("/{date}", ctrl.get(), Role.USER);
                post("/{date}/meals", ctrl.addMeal(), Role.USER);
                patch("/meals/{mealId}", ctrl.updateMeal(), Role.USER);
                delete("/meals/{mealId}", ctrl.deleteMeal(), Role.USER);
            });
        };
    }
}
