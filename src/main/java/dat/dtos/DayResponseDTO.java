package dat.dtos;

import java.time.LocalDate;
import java.util.List;

public record DayResponseDTO(
        LocalDate date, java.util.List<MealResponseDTO> meals,
        Integer totalKcal, Integer totalProtein, Integer totalCarbs, Integer totalFat,
        Integer remainingKcal, Integer remainingProtein, Integer remainingCarbs, Integer remainingFat
) {}