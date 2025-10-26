package dat.daos;

import dat.entities.PantryItem;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class PantryItemDao {
    private final EntityManagerFactory emf;
    public PantryItemDao(EntityManagerFactory emf) { this.emf = emf; }

    public PantryItem save(PantryItem p) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (p.getId()==null) em.persist(p); else p = em.merge(p);
            em.getTransaction().commit();
            return p;
        } finally { em.close(); }
    }

    public List<PantryItem> listByUser(Long userId) {
        var em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PantryItem p WHERE p.user.id=:uid ORDER BY p.id",
                    PantryItem.class
            ).setParameter("uid", userId).getResultList();
        } finally { em.close(); }
    }

    // NEW: filter by username (matches your security principal)
    public List<PantryItem> listByUsername(String username) {
        var em = emf.createEntityManager();
        try {
            return em.createQuery(
                    "SELECT p FROM PantryItem p WHERE p.user.username=:uname ORDER BY p.id",
                    PantryItem.class
            ).setParameter("uname", username).getResultList();
        } finally { em.close(); }
    }
}
