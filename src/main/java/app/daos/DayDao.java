package app.daos;

import app.entities.Day;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.util.List;

public class DayDao {
    private final EntityManagerFactory emf;
    public DayDao(EntityManagerFactory emf) { this.emf = emf; }

    public Day getOrCreate(String username, LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            Day d = em.createQuery(
                            "select d from Day d " +
                                    "left join fetch d.meals m " +
                                    "left join fetch m.recipe " +
                                    "where d.user.username = :u and d.date = :d", Day.class)
                    .setParameter("u", username)
                    .setParameter("d", date)
                    .getResultStream().findFirst().orElse(null);
            if (d != null) return d;

            em.getTransaction().begin();
            User u = em.find(User.class, username);
            if (u == null) {
                u = new User(); // fallback if needed
                u.setUsername(username);
                em.persist(u);
            }
            Day created = new Day();
            created.setUser(u);
            created.setDate(date);
            em.persist(created);
            em.getTransaction().commit();
            return created;
        } finally {
            em.close();
        }
    }

    public List<Day> list(String username, LocalDate from, LocalDate to) {
        EntityManager em = emf.createEntityManager();
        try {
            return em.createQuery(
                            "select distinct d from Day d " +
                                    "left join fetch d.meals m " +
                                    "left join fetch m.recipe " +
                                    "where d.user.username = :u and d.date between :f and :t " +
                                    "order by d.date", Day.class)
                    .setParameter("u", username)
                    .setParameter("f", from)
                    .setParameter("t", to)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
