package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Recursive<E, O> implements Combinator<E, O> {
    private @Nullable Combinator<E, O> parser = null;

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");
        Conditions.nonNull(parser, "this.parser");

        return parser.go(input);
    }

    public void declare(final @NotNull Combinator<E, O> parser) {
        Conditions.nonNull(parser, "parser");

        this.parser = parser;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Recursive) obj;
        return Objects.equals(this.parser, that.parser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(parser);
    }

    @Override
    public String toString() {
        return "Recursive[" +
                "parser=" + parser + ']';
    }
}
