package app.controllers;

import app.dtos.PantryRequestDTO;
import app.dtos.PantryResponseDTO;
import app.entities.PantryItem;
import app.security.entities.User;
import app.config.HibernateConfig;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class PantryController {

    public void list(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }
        String username = user.getUsername(); // <-- use getter

        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            List<PantryItem> items = em.createQuery(
                            "SELECT p FROM PantryItem p WHERE p.user.username=:un ORDER BY p.id", PantryItem.class)
                    .setParameter("un", username)
                    .getResultList();
            List<PantryResponseDTO> out = items.stream().map(PantryResponseDTO::from).toList();
            ctx.json(out);
        } finally {
            em.close();
        }
    }

    public void create(Context ctx) {
        UserDTO user = ctx.attribute("user");
        if (user == null) {
            ctx.status(HttpStatus.UNAUTHORIZED);
            return;
        }
        String username = user.getUsername(); // <-- use getter

        PantryRequestDTO in = ctx.bodyAsClass(PantryRequestDTO.class);

        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            User u = em.find(User.class, username);
            if (u == null) throw new IllegalArgumentException("User not found: " + username);

            PantryItem p = new PantryItem();
            p.setUser(u);
            p.setName(in.name());
            p.setGrams(in.grams());
            LocalDate expiry = (in.expiry() == null || in.expiry().isBlank())
                    ? null
                    : LocalDate.parse(in.expiry());
            p.setExpiry(expiry);

            em.persist(p);
            em.getTransaction().commit();

            ctx.status(HttpStatus.CREATED).json(PantryResponseDTO.from(p));
        } finally {
            em.close();
        }
    }
}
