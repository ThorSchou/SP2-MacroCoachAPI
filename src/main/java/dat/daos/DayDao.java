package dat.daos;

import dat.entities.Day;
import jakarta.persistence.EntityManagerFactory;
import java.time.LocalDate;
import java.util.*;

public class DayDao {
    private final EntityManagerFactory emf;
    public DayDao(EntityManagerFactory emf) { this.emf = emf; }

    public Optional<Day> findByUsernameAndDate(String username, LocalDate date) {
        var em = emf.createEntityManager();
        try {
            var q = em.createQuery(
                    "SELECT d FROM Day d LEFT JOIN FETCH d.meals m " +
                            "WHERE d.user.username = :un AND d.date = :dt", Day.class
            );
            q.setParameter("un", username);
            q.setParameter("dt", date);
            var list = q.getResultList();
            return list.isEmpty()? Optional.empty(): Optional.of(list.get(0));
        } finally { em.close(); }
    }

    public Day save(Day d) {
        var em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (d.getId()==null) em.persist(d); else d = em.merge(d);
            em.getTransaction().commit();
            return d;
        } finally { em.close(); }
    }
}
