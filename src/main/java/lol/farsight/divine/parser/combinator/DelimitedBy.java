package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.Combinator;
import org.jetbrains.annotations.NotNull;

public record DelimitedBy<E, O>(
        @NotNull Combinator<E, ?> parserA,
        @NotNull Combinator<E, O> parserMain,
        @NotNull Combinator<E, ?> parserB
) implements Combinator<E, O> {
    public DelimitedBy {
        Conditions.nonNull(parserA, "parserA");
        Conditions.nonNull(parserMain, "parserMain");
        Conditions.nonNull(parserB, "parserB");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var valA = parserA.go(input);
        if (valA.isNone())
            return Option.none();

        final var val = parserMain.go(input);
        if (val.isNone())
            return Option.none();

        if (parserB.go(input).isNone())
            return Option.none();

        return val;
    }
}
