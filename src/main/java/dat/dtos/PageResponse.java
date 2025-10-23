package dat.dtos;

import java.util.List;

public record PageResponse<T>(java.util.List<T> items, long total, String next) {}