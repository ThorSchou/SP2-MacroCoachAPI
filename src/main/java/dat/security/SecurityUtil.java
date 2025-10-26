package dat.security;

import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

public final class SecurityUtil {
    private SecurityUtil() {}

    public static String requireUsername(Context ctx) {
        String pre = ctx.attribute("username");
        if (pre != null && !pre.isBlank()) return pre;

        UserDTO dto = ctx.attribute("user");
        if (dto == null) throw new UnauthorizedResponse("Not logged in");
        String u = dto.getUsername();
        if (u == null || u.isBlank()) throw new UnauthorizedResponse("Invalid user");
        return u;
    }
}
