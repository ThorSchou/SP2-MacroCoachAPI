package app.services;

import app.config.HibernateConfig;
import app.daos.PantryItemDao;
import app.dtos.PantryCreateDTO;
import app.dtos.PantryResponseDTO;
import app.entities.PantryItem;
import app.exceptions.ApiException;
import app.security.entities.User;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class PantryService {
    private final PantryItemDao pantry;

    public PantryService(PantryItemDao pantry) {
        this.pantry = pantry;
    }

    private User requireUserEntity(String username) {
        EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager();
        try {
            User u = em.find(User.class, username);
            if (u == null) throw new ApiException(404, "User not found: " + username);
            return u;
        } finally {
            em.close();
        }
    }

    public PantryResponseDTO create(String username, PantryCreateDTO dto) {
        if (username == null || username.isBlank()) throw new ApiException(401, "Missing authenticated user");
        if (dto == null) throw new ApiException(400, "Body is required");
        if (dto.name() == null || dto.name().isBlank()) throw new ApiException(422, "name must not be blank");
        if (dto.grams() == null || dto.grams() < 0) throw new ApiException(422, "grams must be >= 0");

        User owner = requireUserEntity(username);

        PantryItem e = new PantryItem();
        e.setUser(owner);
        e.setName(dto.name().trim());
        e.setGrams(dto.grams());

        if (dto.expiry() != null && !dto.expiry().isBlank()) {
            try {
                e.setExpiry(LocalDate.parse(dto.expiry())); // ISO-8601 (YYYY-MM-DD)
            } catch (Exception ex) {
                throw new ApiException(422, "expiry must be YYYY-MM-DD");
            }
        }

        e = pantry.save(e);
        return toDTO(e);
    }

    public List<PantryResponseDTO> list(String username) {
        if (username == null || username.isBlank()) throw new ApiException(401, "Missing authenticated user");
        // Your existing DAO already scopes by username; if it expects a User entity, adjust it there.
        return pantry.listByUsername(username).stream().map(this::toDTO).toList();
    }

    private PantryResponseDTO toDTO(PantryItem e) {
        String expiry = e.getExpiry() == null ? null : e.getExpiry().toString();
        return new PantryResponseDTO(e.getId(), e.getName(), e.getGrams(), expiry);
        // Keep the DTO small for SP2.
    }
}
