package dat.services;

import dat.daos.DayDao;
import dat.daos.MealDao;
import dat.daos.RecipeDao;
import dat.dtos.*;
import dat.entities.*;
import dat.exceptions.ApiException;
import dat.exceptions.NotAuthorizedException;
import dat.exceptions.ValidationException;
import dat.security.entities.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class DayService {
    private final DayDao days;
    private final MealDao meals;
    private final RecipeDao recipes;

    public DayService(DayDao days, MealDao meals, RecipeDao recipes) {
        this.days = days; this.meals = meals; this.recipes = recipes;
    }

    public DayResponseDTO get(User user, LocalDate date, Profile profile) {
        var d = days.findByUsernameAndDate(user.getUsername(), date).orElseGet(() -> {
            var nd = new Day(); nd.setUser(user); nd.setDate(date); return days.save(nd);
        });
        return toDayDTO(d, profile);
    }

    public MealResponseDTO addMeal(User user, LocalDate date, MealCreateDTO dto, Profile profile)
            throws ValidationException, ApiException {
        validateMealCreate(dto);

        var d = days.findByUsernameAndDate(user.getUsername(), date).orElseGet(() -> {
            var nd = new Day(); nd.setUser(user); nd.setDate(date); return days.save(nd);
        });

        var r = recipes.findById(dto.recipeId()).orElseThrow(() -> new ApiException(404, "Recipe not found"));

        var m = new Meal();
        m.setDay(d); m.setRecipe(r);
        m.setGrams(dto.grams()); m.setNote(dto.note()); m.setType(dto.type());
        meals.save(m);

        d.getMeals().add(m);
        return toMealDTO(m);
    }

    public MealResponseDTO patchMeal(User user, Long mealId, MealCreateDTO dto)
            throws ApiException, NotAuthorizedException, ValidationException {
        var m = meals.findById(mealId).orElseThrow(() -> new ApiException(404, "Meal not found"));
        if (!m.getDay().getUser().getUsername().equals(user.getUsername()))
            throw new NotAuthorizedException(403, "You don't have access to this meal");

        if (dto.grams()!=null && dto.grams() < 1) throw new ValidationException("grams must be >= 1");

        if (dto.grams()!=null) m.setGrams(dto.grams());
        if (dto.note()!=null) m.setNote(dto.note());
        if (dto.type()!=null) m.setType(dto.type());
        return toMealDTO(meals.save(m));
    }

    public void deleteMeal(User user, Long mealId) throws ApiException, NotAuthorizedException {
        var m = meals.findById(mealId).orElseThrow(() -> new ApiException(404, "Meal not found"));
        if (!m.getDay().getUser().getUsername().equals(user.getUsername()))
            throw new NotAuthorizedException(403, "You don't have access to this meal");
        meals.delete(m);
    }

    private void validateMealCreate(MealCreateDTO dto) throws ValidationException {
        if (dto.recipeId()==null) throw new ValidationException("recipeId is required");
        if (dto.grams()==null || dto.grams() < 1) throw new ValidationException("grams must be >= 1");
    }

    private DayResponseDTO toDayDTO(Day d, Profile profile) {
        var mealDTOs = d.getMeals().stream().map(this::toMealDTO).collect(Collectors.toList());
        int kcal=0,p=0,c=0,f=0;
        for (var m : mealDTOs) { kcal+=m.kcal(); p+=m.protein(); c+=m.carbs(); f+=m.fat(); }
        Integer rk = (profile==null||profile.getTargetKcal()==null) ? null : profile.getTargetKcal()-kcal;
        Integer rp = (profile==null||profile.getTargetProtein()==null) ? null : profile.getTargetProtein()-p;
        Integer rc = (profile==null||profile.getTargetCarbs()==null) ? null : profile.getTargetCarbs()-c;
        Integer rf = (profile==null||profile.getTargetFat()==null) ? null : profile.getTargetFat()-f;
        return new DayResponseDTO(d.getDate(), mealDTOs, kcal,p,c,f, rk,rp,rc,rf);
    }

    private MealResponseDTO toMealDTO(Meal m) {
        var r = m.getRecipe();
        return new MealResponseDTO(m.getId(), r.getId(), r.getName(), m.getGrams(), m.getType(),
                r.getKcal(), r.getProtein(), r.getCarbs(), r.getFat(), m.getNote());
    }
}
