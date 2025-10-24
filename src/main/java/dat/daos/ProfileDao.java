package dat.daos;

import dat.entities.Profile;
import dat.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.Optional;

public class ProfileDao {
    private final EntityManagerFactory emf;
    public ProfileDao(EntityManagerFactory emf) { this.emf = emf; }


    public Optional<Profile> findByUsername(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            var q = em.createQuery(
                    "SELECT p FROM Profile p " +
                            "LEFT JOIN FETCH p.allergies " +
                            "WHERE p.user.username = :un",
                    Profile.class
            );
            q.setParameter("un", username);
            var list = q.getResultList();

            return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
        } finally {
            em.close();
        }
    }


    public Profile saveForUsername(String username, Profile p) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            User managedUser = em.find(User.class, username);
            if (managedUser == null) {
                throw new IllegalStateException("Cannot save profile: user '" + username + "' not found");
            }

            Profile managed;
            if (p.getId() == null) {
                p.setUser(managedUser);     // attach managed user on create
                em.persist(p);
                managed = p;
            } else {

                p.setUser(managedUser);
                managed = em.merge(p);
            }

            em.getTransaction().commit();
            return managed;
        } finally {
            em.close();
        }
    }

    public Profile save(Profile p) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Profile managed = (p.getId() == null) ? p : em.merge(p);
            if (p.getId() == null) em.persist(managed);
            em.getTransaction().commit();
            return managed;
        } finally {
            em.close();
        }
    }
}
