package dat.utils;

import io.javalin.http.Context;

public class Pagination {

    public static int limit(Context ctx, int def) {
        return ctx.queryParamAsClass("limit", Integer.class).getOrDefault(def);
    }

    public static int offset(Context ctx) {
        return ctx.queryParamAsClass("offset", Integer.class).getOrDefault(0);
    }


    public static int nonNegative(int value, int fallback) {
        return value < 0 ? fallback : value;
    }
}
