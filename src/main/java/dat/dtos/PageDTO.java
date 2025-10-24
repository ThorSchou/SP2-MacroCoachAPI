package dat.dtos;

import java.util.List;

public record PageDTO<T>(List<T> items, long total, int limit, int offset) {}
