package dat.utils;

import io.javalin.http.Context;

public class Pagination {
    public static int limit(Context ctx, int def) { return Integer.parseInt(ctx.queryParam("limit", String.valueOf(def))); }
    public static int offset(Context ctx) { return Integer.parseInt(ctx.queryParam("offset", "0")); }
}
