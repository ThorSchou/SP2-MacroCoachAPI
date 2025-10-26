package dat.dtos;

import java.time.LocalDate;
import java.util.List;

public record SummaryDTO(
        LocalDate from,
        LocalDate to,
        List<DayResponseDTO> days,
        Integer totalKcal,
        Integer totalProtein,
        Integer totalCarbs,
        Integer totalFat
) {}
