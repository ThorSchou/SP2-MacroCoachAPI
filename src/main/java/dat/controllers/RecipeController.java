package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.RecipeDao;
import dat.dtos.RecipeCreateDTO;
import dat.security.entities.User;
import dat.security.enums.Role;
import dat.services.RecipeService;
import io.javalin.http.Context;

public class RecipeController {
    private final RecipeService svc;

    public RecipeController() {
        var emf = HibernateConfig.getEntityManagerFactory();
        this.svc = new RecipeService(new RecipeDao(emf));
    }

    public void list(Context ctx) throws Exception {
        int limit  = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(20);
        int offset = ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);
        ctx.json(svc.list(limit, offset));
    }

    public void get(Context ctx) throws Exception {
        Long id = Long.parseLong(ctx.pathParam("id"));
        ctx.json(svc.get(id));
    }

    public void create(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        var dto = ctx.bodyAsClass(RecipeCreateDTO.class);
        ctx.status(201).json(svc.create(user, dto));
    }

    public void update(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        var role = (Role) ctx.attribute("role");
        var dto = ctx.bodyAsClass(RecipeCreateDTO.class);
        Long id = Long.parseLong(ctx.pathParam("id"));
        boolean isAdmin = role == Role.ADMIN;
        ctx.json(svc.update(user, isAdmin, id, dto));
    }

    public void delete(Context ctx) throws Exception {
        User user = (User) ctx.attribute("user");
        var role = (Role) ctx.attribute("role");
        boolean isAdmin = role == Role.ADMIN;
        Long id = Long.parseLong(ctx.pathParam("id"));
        svc.delete(user, isAdmin, id);
        ctx.status(204);
    }
}
