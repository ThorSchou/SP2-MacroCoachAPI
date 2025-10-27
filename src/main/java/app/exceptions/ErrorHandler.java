package app.exceptions;

import io.javalin.Javalin;
import io.javalin.http.HttpStatus;

public class ErrorHandler {

    public static void install(Javalin app) {

        app.exception(ApiException.class, (e, ctx) -> {
            int status = e.getStatusCode();
            ctx.status(status).json(new Message(status, e.getMessage()));
        });

        app.exception(NotAuthorizedException.class, (e, ctx) -> {
            int status = e.getStatusCode(); // usually 401 or 403
            ctx.status(status).json(new Message(status, e.getMessage()));
        });

        app.exception(ValidationException.class, (e, ctx) -> {
            int status = HttpStatus.UNPROCESSABLE_CONTENT.getCode(); // 422
            ctx.status(status).json(new Message(status, e.getMessage()));
        });

        app.exception(Exception.class, (e, ctx) -> {
            // optional: log e
            int status = HttpStatus.INTERNAL_SERVER_ERROR.getCode();
            ctx.status(status).json(new Message(status, "Unexpected error"));
        });

        app.error(404, ctx -> ctx.json(new Message(404, "Route not found")));
    }
}
