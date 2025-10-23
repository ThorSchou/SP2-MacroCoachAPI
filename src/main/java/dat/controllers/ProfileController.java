package dat.controllers;

import dat.daos.ProfileDao;
import dat.dtos.ProfileCreateDTO;
import dat.services.ProfileService;
import dat.security.entities.User;
import dat.config.HibernateConfig;
import io.javalin.http.Context;

public class ProfileController {
    private final ProfileService svc;

    public ProfileController() {
        var emf = HibernateConfig.getEntityManagerFactory();
        this.svc = new ProfileService(new ProfileDao(emf));
    }

    public void me(Context ctx) {
        User user = (User) ctx.attribute("user");
        ctx.json(svc.getMine(user));
    }

    public void replace(Context ctx) {
        User user = (User) ctx.attribute("user");
        var dto = ctx.bodyAsClass(ProfileCreateDTO.class);
        ctx.json(svc.replace(user, dto));
    }

    public void patch(Context ctx) {
        User user = (User) ctx.attribute("user");
        var dto = ctx.bodyAsClass(ProfileCreateDTO.class);
        ctx.json(svc.patch(user, dto));
    }
}
