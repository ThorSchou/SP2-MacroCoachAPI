package app.dtos;

public record ErrorDTO(String error, String message, java.util.Map<String,String> details) {}