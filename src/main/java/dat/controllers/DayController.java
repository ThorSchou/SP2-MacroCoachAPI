package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.*;
import dat.dtos.*;
import dat.entities.Profile;
import dat.security.entities.User;
import dat.services.*;
import io.javalin.http.Context;

import java.time.LocalDate;

public class DayController {
    private final DayService svc;
    private final ProfileService profiles;

    public DayController() {
        var emf = HibernateConfig.getEntityManagerFactory();
        this.svc = new DayService(new DayDao(emf), new MealDao(emf), new RecipeDao(emf));
        this.profiles = new ProfileService(new ProfileDao(emf));
    }

    public void get(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        LocalDate date = LocalDate.parse(ctx.pathParam("date"));
        var p = new ProfileDao(HibernateConfig.getEntityManagerFactory()).findByUsername(user.getUsername()).orElse(null);
        ctx.json(svc.get(user, date, p));
    }

    public void addMeal(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        LocalDate date = LocalDate.parse(ctx.pathParam("date"));
        var dto = ctx.bodyAsClass(MealCreateDTO.class);
        var p = new ProfileDao(HibernateConfig.getEntityManagerFactory()).findByUsername(user.getUsername()).orElse(null);
        ctx.status(201).json(svc.addMeal(user, date, dto, p));
    }

    public void patchMeal(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        Long mealId = Long.parseLong(ctx.pathParam("mealId"));
        var dto = ctx.bodyAsClass(MealCreateDTO.class);
        ctx.json(svc.patchMeal(user, mealId, dto));
    }

    public void deleteMeal(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        Long mealId = Long.parseLong(ctx.pathParam("mealId"));
        svc.deleteMeal(user, mealId);
        ctx.status(204);
    }

    public void rangeSummary(Context ctx) {
        ctx.json(java.util.Map.of("todo","summary"));
    }
}
