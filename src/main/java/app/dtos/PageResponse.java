package app.dtos;

public record PageResponse<T>(java.util.List<T> items, long total, String next) {}