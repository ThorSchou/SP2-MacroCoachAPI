package dat.controllers;

import dat.dtos.DayResponseDTO;
import dat.dtos.MealCreateDTO;
import dat.dtos.SummaryDTO;
import dat.services.DayService;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.time.LocalDate;

public class DayController {
    private final DayService svc = new DayService();

    private String usernameOr401(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return null;
        }
        return user.getUsername(); // IMPORTANT: use getter, not .username
    }

    public void get(Context ctx) {
        String username = usernameOr401(ctx);
        if (username == null) return;

        LocalDate date = ctx.pathParamAsClass("date", LocalDate.class).get();
        DayResponseDTO dto = svc.get(username, date);
        ctx.json(dto);
    }

    public void addMeal(Context ctx) {
        String username = usernameOr401(ctx);
        if (username == null) return;

        LocalDate date = ctx.pathParamAsClass("date", LocalDate.class).get();
        MealCreateDTO in = ctx.bodyAsClass(MealCreateDTO.class);
        DayResponseDTO dto = svc.addMeal(username, date, in);
        ctx.status(HttpStatus.CREATED).json(dto);
    }

    public void updateMeal(Context ctx) {
        String username = usernameOr401(ctx);
        if (username == null) return;

        long mealId = ctx.pathParamAsClass("mealId", Long.class).get();
        MealCreateDTO in = ctx.bodyAsClass(MealCreateDTO.class);
        DayResponseDTO dto = svc.updateMeal(username, mealId, in);
        ctx.json(dto);
    }

    public void deleteMeal(Context ctx) {
        String username = usernameOr401(ctx);
        if (username == null) return;

        long mealId = ctx.pathParamAsClass("mealId", Long.class).get();
        svc.deleteMeal(username, mealId);
        ctx.status(HttpStatus.NO_CONTENT);
    }

    public void summary(Context ctx) {
        String username = usernameOr401(ctx);
        if (username == null) return;

        LocalDate from = ctx.queryParamAsClass("from", LocalDate.class).get();
        LocalDate to   = ctx.queryParamAsClass("to", LocalDate.class).get();
        SummaryDTO dto = svc.summary(username, from, to);
        ctx.json(dto);
    }
}
