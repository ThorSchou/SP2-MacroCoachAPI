package dat.dtos;

public record PantryRequestDTO(
        String name,
        Integer grams,
        String expiry
) {}
