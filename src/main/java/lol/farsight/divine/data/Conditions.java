package lol.farsight.divine.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

public final class Conditions {
    private Conditions() {
        throw new UnsupportedOperationException();
    }

    @Contract("null, _ -> fail; _, null -> fail")
    public static void nonNull(final @Nullable Object object, final @Nullable String message) {
        if (message == null)
            throw new NullPointerException("messages");
        if (object == null)
            throw new NullPointerException(message);
    }

    @Contract("null, _ -> fail; _, null -> fail")
    public static <T> void elementsNonNull(final @Nullable T @Nullable [] object, final @Nullable String message) {
        nonNull(message, "messages");
        nonNull(object, "object");

        if (Arrays.stream(object).anyMatch(Objects::isNull))
            throw new NullPointerException(message);
    }
}
