package dat.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.dtos.PageDTO;
import dat.dtos.RecipeCreateDTO;
import dat.dtos.RecipeResponseDTO;
import dat.security.enums.Role;
import dat.services.RecipeService;
import dk.bugelhartmann.UserDTO;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;

import static io.javalin.apibuilder.ApiBuilder.*;

public class RecipeController {

    private final RecipeService svc = new RecipeService();
    private final ObjectMapper om = new ObjectMapper();

    public EndpointGroup routes() {
        return () -> {
            path("/recipes", () -> {
                // Public
                get(this::list, Role.ANYONE);
                get("{id}", this::get, Role.ANYONE);

                // Authenticated
                post(this::create, Role.USER);
                put("{id}", this::update, Role.USER);
                delete("{id}", this::delete, Role.USER);
            });
        };
    }

    private void list(Context ctx) {
        int limit  = Integer.parseInt(ctx.queryParam("limit",  "10"));
        int offset = Integer.parseInt(ctx.queryParam("offset", "0"));
        PageDTO<RecipeResponseDTO> page = svc.list(limit, offset);
        ctx.json(page);
    }

    private void get(Context ctx) {
        long id = Long.parseLong(ctx.pathParam("id"));
        RecipeResponseDTO dto = svc.get(id);
        ctx.json(dto);
    }

    private void create(Context ctx) throws Exception {
        UserDTO user = ctx.attribute("user"); // set by access manager
        String username = user.getUsername();

        RecipeCreateDTO body = om.readValue(ctx.body(), RecipeCreateDTO.class);
        RecipeResponseDTO created = svc.create(username, body);
        ctx.status(201).json(created);
    }

    private void update(Context ctx) throws Exception {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));

        long id = Long.parseLong(ctx.pathParam("id"));
        RecipeCreateDTO body = om.readValue(ctx.body(), RecipeCreateDTO.class);
        RecipeResponseDTO updated = svc.update(username, id, body, isAdmin);
        ctx.json(updated);
    }

    private void delete(Context ctx) {
        UserDTO user = ctx.attribute("user");
        String username = user.getUsername();
        boolean isAdmin = user.getRoles().stream().anyMatch(r -> r.equalsIgnoreCase("ADMIN"));

        long id = Long.parseLong(ctx.pathParam("id"));
        svc.delete(username, id, isAdmin);
        ctx.status(204);
    }
}
