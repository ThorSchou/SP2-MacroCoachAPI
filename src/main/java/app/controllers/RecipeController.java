package app.controllers;

import app.dtos.PageDTO;
import app.dtos.RecipeCreateDTO;
import app.dtos.RecipeResponseDTO;
import app.services.RecipeService;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

public class RecipeController {
    private final RecipeService svc = new RecipeService();

    public void list(Context ctx) {
        int limit  = ctx.queryParamAsClass("limit", Integer.class).getOrDefault(10);
        int offset = ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);

        PageDTO<RecipeResponseDTO> page = svc.list(limit, offset);
        ctx.json(page);
    }

    public void get(Context ctx) {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        RecipeResponseDTO dto = svc.get(id);
        ctx.json(dto);
    }

    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }
        RecipeCreateDTO in = ctx.bodyAsClass(RecipeCreateDTO.class);
        RecipeResponseDTO created = svc.create(user.getUsername(), in);
        ctx.status(HttpStatus.CREATED).json(created);
    }

    public void update(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }
        long id = ctx.pathParamAsClass("id", Long.class).get();
        RecipeCreateDTO in = ctx.bodyAsClass(RecipeCreateDTO.class);
        RecipeResponseDTO updated = svc.update(user.getUsername(), id, in);
        ctx.json(updated);
    }

    public void delete(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }
        long id = ctx.pathParamAsClass("id", Long.class).get();
        svc.delete(user.getUsername(), id);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
