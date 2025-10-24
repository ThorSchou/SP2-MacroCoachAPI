package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.RecipeDao;
import dat.dtos.RecipeCreateDTO;
import dat.security.enums.Role;
import dat.services.RecipeService;
import io.javalin.http.Context;

// Import the principal type placed on ctx.attribute("user") by your security lib
import dk.bugelhartmann.UserDTO;

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
        // principal is a dk.bugelhartmann.UserDTO (NOT your User entity)
        UserDTO principal = (UserDTO) ctx.attribute("user");
        if (principal == null) {
            // your ApiException likely takes (int status, String message)
            throw new dat.exceptions.ApiException(401, "Missing authenticated user");
        }
        String username = principal.username();

        var dto = ctx.bodyAsClass(RecipeCreateDTO.class);
        ctx.status(201).json(svc.create(username, dto));
    }

    public void update(Context ctx) throws Exception {
        UserDTO principal = (UserDTO) ctx.attribute("user");
        if (principal == null) {
            throw new dat.exceptions.ApiException(401, "Missing authenticated user");
        }
        String username = principal.username();

        var role = (Role) ctx.attribute("role");
        boolean isAdmin = role == Role.ADMIN;

        var dto = ctx.bodyAsClass(RecipeCreateDTO.class);
        Long id = Long.parseLong(ctx.pathParam("id"));
        ctx.json(svc.update(username, isAdmin, id, dto));
    }

    public void delete(Context ctx) throws Exception {
        UserDTO principal = (UserDTO) ctx.attribute("user");
        if (principal == null) {
            throw new dat.exceptions.ApiException(401, "Missing authenticated user");
        }
        String username = principal.username();

        var role = (Role) ctx.attribute("role");
        boolean isAdmin = role == Role.ADMIN;

        Long id = Long.parseLong(ctx.pathParam("id"));
        svc.delete(username, isAdmin, id);
        ctx.status(204);
    }
}
