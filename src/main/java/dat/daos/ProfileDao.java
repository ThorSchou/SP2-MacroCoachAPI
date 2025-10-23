package dat.daos;

import dat.entities.Profile;
import jakarta.persistence.EntityManagerFactory;
import java.util.Optional;

public class ProfileDao {
    private final EntityManagerFactory emf;
    public ProfileDao(EntityManagerFactory emf) { this.emf = emf; }

    public Optional<Profile> findByUsername(String username) {
        var em = emf.createEntityManager();
        try {
            var q = em.createQuery(
                    "SELECT p FROM Profile p WHERE p.user.username = :un", Profile.class
            );
            q.setParameter("un", username);
            var list = q.getResultList();
            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally { em.close(); }
    }

    public Profile save(Profile p) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Profile managed = (p.getId()==null) ? p : em.merge(p);
            if (p.getId()==null) em.persist(managed);
            em.getTransaction().commit();
            return managed;
        } finally { em.close(); }
    }
}
