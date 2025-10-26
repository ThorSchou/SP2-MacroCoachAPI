package dat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.dtos.DayResponseDTO;
import dat.dtos.MealCreateDTO;
import dat.dtos.MealResponseDTO;
import dat.security.enums.Role;
import dat.services.DayService;
import dk.bugelhartmann.UserDTO;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import java.time.LocalDate;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DayController {

    private final DayService svc = new DayService();
    private final ObjectMapper om = new ObjectMapper();

    public EndpointGroup routes() {
        return () -> {
            path("/days", () -> {

                // IMPORTANT: register /summary BEFORE /{date} so "summary" isn't parsed as a date
                get("summary", this::summary, Role.USER);

                get("{date}", this::get, Role.USER);
                post("{date}/meals", this::addMeal, Role.USER);
                patch("meals/{mealId}", this::patchMeal, Role.USER);
                delete("meals/{mealId}", this::deleteMeal, Role.USER);
            });
        };
    }

    private void get(Context ctx) {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();

        LocalDate date = LocalDate.parse(ctx.pathParam("date"));
        DayResponseDTO dto = svc.getDay(username, date);
        ctx.json(dto);
    }

    private void addMeal(Context ctx) throws Exception {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();

        LocalDate date = LocalDate.parse(ctx.pathParam("date"));
        MealCreateDTO body = new ObjectMapper().readValue(ctx.body(), MealCreateDTO.class);
        MealResponseDTO dto = svc.addMeal(username, date, body);
        ctx.status(201).json(dto);
    }

    private void patchMeal(Context ctx) throws Exception {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();

        long mealId = Long.parseLong(ctx.pathParam("mealId"));
        MealCreateDTO body = om.readValue(ctx.body(), MealCreateDTO.class);
        MealResponseDTO dto = svc.patchMeal(username, mealId, body);
        ctx.json(dto);
    }

    private void deleteMeal(Context ctx) {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();

        long mealId = Long.parseLong(ctx.pathParam("mealId"));
        svc.deleteMeal(username, mealId);
        ctx.status(204);
    }

    private void summary(Context ctx) {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();

        LocalDate from = LocalDate.parse(ctx.queryParam("from"));
        LocalDate to   = LocalDate.parse(ctx.queryParam("to"));
        ctx.json(svc.summary(username, from, to));
    }
}
