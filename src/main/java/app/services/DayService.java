package app.services;

import app.config.HibernateConfig;
import app.daos.DayDao;
import app.daos.MealDao;
import app.dtos.DayResponseDTO;
import app.dtos.MealCreateDTO;
import app.dtos.SummaryDTO;
import app.entities.Day;
import app.entities.Meal;
import app.entities.Recipe;
import app.security.entities.User;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;

public class DayService {
    private final DayDao dayDao = new DayDao(HibernateConfig.getEntityManagerFactory());
    private final MealDao mealDao = new MealDao(HibernateConfig.getEntityManagerFactory());

    public DayResponseDTO get(String username, LocalDate date) {
        Day d = dayDao.getOrCreate(username, date);
        return DayResponseDTO.from(d);
    }

    public DayResponseDTO addMeal(String username, LocalDate date, MealCreateDTO in) {
        Day d = dayDao.getOrCreate(username, date);
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();

            Recipe r = em.find(Recipe.class, in.recipeId());
            if (r == null) throw new IllegalArgumentException("Recipe not found: " + in.recipeId());

            Meal m = new Meal();
            m.setDay(d);
            m.setRecipe(r);
            m.setGrams(in.grams());
            m.setType(in.type()); 
            m.setNote(in.note());
            em.persist(m);

            em.getTransaction().commit();
        }
        Day fresh = dayDao.getOrCreate(username, date);
        return DayResponseDTO.from(fresh);
    }

    public DayResponseDTO updateMeal(String username, long mealId, MealCreateDTO in) {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();

            Meal m = em.find(Meal.class, mealId);
            if (m == null) throw new IllegalArgumentException("Meal not found: " + mealId);

            // verify ownership
            User u = m.getDay().getUser();
            if (u == null || !u.getUsername().equals(username)) {
                throw new IllegalArgumentException("Not your meal");
            }

            if (in.recipeId() != null) {
                Recipe r = em.find(Recipe.class, in.recipeId());
                if (r == null) throw new IllegalArgumentException("Recipe not found: " + in.recipeId());
                m.setRecipe(r);
            }
            if (in.grams() != null) m.setGrams(in.grams());
            if (in.type()  != null) m.setType(in.type());
            m.setNote(in.note());

            LocalDate date = m.getDay().getDate();
            em.getTransaction().commit();
            return DayResponseDTO.from(dayDao.getOrCreate(username, date));
        }
    }

    public DayResponseDTO deleteMeal(String username, long mealId) {
        LocalDate date;
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            em.getTransaction().begin();

            Meal m = em.find(Meal.class, mealId);
            if (m == null) throw new IllegalArgumentException("Meal not found: " + mealId);
            if (m.getDay() == null || m.getDay().getUser() == null
                    || !m.getDay().getUser().getUsername().equals(username)) {
                throw new IllegalArgumentException("Not your meal");
            }

            date = m.getDay().getDate();
            em.remove(m);

            em.getTransaction().commit();
        }
        return DayResponseDTO.from(dayDao.getOrCreate(username, date));
    }

    public SummaryDTO summary(String username, LocalDate from, LocalDate to) {
        var days = listDaysBetween(username, from, to);

        int totalKcal = 0, totalProtein = 0, totalCarbs = 0, totalFat = 0;
        for (var d : days) {
            totalKcal    += d.totalKcal()    != null ? d.totalKcal()    : 0;
            totalProtein += d.totalProtein() != null ? d.totalProtein() : 0;
            totalCarbs   += d.totalCarbs()   != null ? d.totalCarbs()   : 0;
            totalFat     += d.totalFat()     != null ? d.totalFat()     : 0;
        }

        return new SummaryDTO(from, to, days, totalKcal, totalProtein, totalCarbs, totalFat);
    }

    private List<DayResponseDTO> listDaysBetween(String username, LocalDate from, LocalDate to) {
        try (EntityManager em = HibernateConfig.getEntityManagerFactory().createEntityManager()) {
            var entities = em.createQuery(
                            "SELECT DISTINCT d FROM Day d " +
                                    "LEFT JOIN FETCH d.meals m " +
                                    "LEFT JOIN FETCH m.recipe r " +
                                    "WHERE d.user.username = :un AND d.date BETWEEN :from AND :to " +
                                    "ORDER BY d.date", Day.class)
                    .setParameter("un", username)
                    .setParameter("from", from)
                    .setParameter("to", to)
                    .getResultList();

            return entities.stream().map(DayResponseDTO::from).toList();
        }
    }
}
