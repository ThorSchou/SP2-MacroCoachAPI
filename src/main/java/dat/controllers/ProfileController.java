package dat.controllers;

import dat.config.HibernateConfig;
import dat.daos.ProfileDao;
import dat.dtos.ProfileCreateDTO;
import dat.security.entities.User;
import dat.services.ProfileService;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.Optional;


public class ProfileController {
    private final ProfileService svc;

    public ProfileController() {
        var emf = HibernateConfig.getEntityManagerFactory();
        this.svc = new ProfileService(new ProfileDao(emf));
    }

    private User requireUserEntity(Context ctx) {

        UserDTO auth = ctx.attribute("user");
        if (auth == null) throw new UnauthorizedResponse("Missing authenticated user");

        var em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            User user = em.find(User.class, auth.getUsername());
            return Optional.ofNullable(user)
                    .orElseThrow(() -> new NotFoundResponse("User not found: " + auth.getUsername()));
        } finally {
            em.close();
        }
    }

    public void me(Context ctx) {
        User user = requireUserEntity(ctx);
        ctx.json(svc.getMine(user));
    }

    public void replace(Context ctx) {
        User user = requireUserEntity(ctx);
        var dto = ctx.bodyAsClass(ProfileCreateDTO.class);
        ctx.json(svc.replace(user, dto));
    }

    public void patch(Context ctx) {
        User user = requireUserEntity(ctx);
        var dto = ctx.bodyAsClass(ProfileCreateDTO.class);
        ctx.json(svc.patch(user, dto));
    }
}
