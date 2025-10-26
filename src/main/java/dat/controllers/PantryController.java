package dat.controllers;

import dat.dtos.PantryCreateDTO;
import dat.dtos.PantryResponseDTO;
import dat.services.PantryService;
import dat.config.HibernateConfig;
import dat.daos.PantryItemDao;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import java.util.List;

public class PantryController {
    private final PantryService svc;

    public PantryController() {
        var emf = HibernateConfig.getEntityManagerFactory();
        this.svc = new PantryService(new PantryItemDao(emf));
    }

    private String requireUsername(Context ctx) {
        UserDTO dto = (UserDTO) ctx.attribute("user");
        if (dto == null || dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new UnauthorizedResponse("Not logged in");
        }
        return dto.getUsername();
    }

    public void create(Context ctx) {
        String username = requireUsername(ctx);
        PantryCreateDTO dto = ctx.bodyAsClass(PantryCreateDTO.class);
        PantryResponseDTO res = svc.create(username, dto); // expects String
        ctx.status(201).json(res);
    }

    public void list(Context ctx) {
        String username = requireUsername(ctx);
        List<PantryResponseDTO> out = svc.list(username); // expects String
        ctx.json(out);
    }
}
