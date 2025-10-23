package dat.utils;

public class Validation {
    public static void require(boolean cond, String code) {
        if (!cond) throw new RuntimeException(code);
    }
}
