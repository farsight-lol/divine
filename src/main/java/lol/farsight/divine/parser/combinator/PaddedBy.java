package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import org.jetbrains.annotations.NotNull;

public record PaddedBy<E, O>(
        @NotNull Combinator<E, O> parser,
        @NotNull Combinator<E, ?> padding
) implements Combinator<E, O> {
    public PaddedBy {
        Conditions.nonNull(parser, "parser");
        Conditions.nonNull(padding, "padding");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var valA = padding.go(input);
        if (valA.isNone())
            return Option.none();

        final var val = parser.go(input);
        if (val.isNone())
            return Option.none();

        final var valB = padding.go(input);
        if (valB.isNone())
            return Option.none();

        return val;
    }
}
