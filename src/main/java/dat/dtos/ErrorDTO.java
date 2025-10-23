package dat.dtos;

import java.util.Map;

public record ErrorDTO(String error, String message, java.util.Map<String,String> details) {}