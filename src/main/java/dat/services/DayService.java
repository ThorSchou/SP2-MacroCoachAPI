package dat.services;

import dat.daos.DayDao;
import dat.daos.MealDao;
import dat.daos.RecipeDao;
import dat.dtos.DayResponseDTO;
import dat.dtos.MealCreateDTO;
import dat.dtos.MealResponseDTO;
import dat.entities.Day;
import dat.entities.Meal;
import dat.entities.Recipe;
import dat.security.exceptions.ApiException;

import java.time.LocalDate;
import java.util.Map;

public class DayService {

    private final DayDao dayDao = new DayDao();
    private final MealDao mealDao = new MealDao();
    private final RecipeDao recipeDao = new RecipeDao();

    public DayResponseDTO getDay(String username, LocalDate date) {
        Day day = dayDao.findByUsernameAndDate(username, date).orElseGet(() -> {
            Day d = new Day();
            d.setDate(date);
            // user link is handled inside DAO (via username)
            return dayDao.saveForUser(username, d);
        });
        return DayResponseDTO.from(day);
    }

    public MealResponseDTO addMeal(String username, LocalDate date, MealCreateDTO dto) {
        Day day = dayDao.findByUsernameAndDate(username, date).orElseGet(() -> {
            Day d = new Day();
            d.setDate(date);
            return dayDao.saveForUser(username, d);
        });

        Recipe recipe = recipeDao.get(dto.getRecipeId());
        if (recipe == null) throw new ApiException(404, "Recipe not found");

        Meal m = new Meal();
        m.setRecipe(recipe);
        m.setGrams(dto.getGrams());
        m.setType(dto.getType());
        m.setNote(dto.getNote());
        m.setDay(day);

        Meal saved = mealDao.save(m);
        return MealResponseDTO.from(saved);
    }

    public MealResponseDTO patchMeal(String username, long mealId, MealCreateDTO dto) {
        Meal existing = mealDao.get(mealId);
        if (existing == null) throw new ApiException(404, "Meal not found");

        // ownership check
        String owner = existing.getDay().getUser().getUsername();
        if (!owner.equals(username)) throw new ApiException(403, "You cannot modify this meal");

        if (dto.getRecipeId() != null) {
            Recipe r = recipeDao.get(dto.getRecipeId());
            if (r == null) throw new ApiException(404, "Recipe not found");
            existing.setRecipe(r);
        }
        if (dto.getGrams() != null) existing.setGrams(dto.getGrams());
        if (dto.getType() != null) existing.setType(dto.getType());
        if (dto.getNote() != null) existing.setNote(dto.getNote());

        Meal saved = mealDao.save(existing);
        return MealResponseDTO.from(saved);
    }

    public void deleteMeal(String username, long mealId) {
        Meal existing = mealDao.get(mealId);
        if (existing == null) return;
        String owner = existing.getDay().getUser().getUsername();
        if (!owner.equals(username)) throw new ApiException(403, "You cannot delete this meal");
        mealDao.delete(mealId);
    }

    /** Simple summary: totals for kcal/protein/carbs/fat in the range (inclusive). */
    public Map<String, Object> summary(String username, LocalDate from, LocalDate to) {
        return dayDao.summary(username, from, to);
    }
}
