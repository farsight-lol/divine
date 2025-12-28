package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import org.jetbrains.annotations.NotNull;

public record IgnoreThen<E, OB>(
        @NotNull Combinator<E, ?> parserA,
        @NotNull Combinator<E, OB> parserB
) implements Combinator<E, OB> {
    public IgnoreThen {
        Conditions.nonNull(parserA, "parserA");
        Conditions.nonNull(parserB, "parserB");
    }

    @Override
    public @NotNull Option<OB> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var valA = parserA.go(input);
        if (valA.isNone())
            return Option.none();

        return parserB.go(input);
    }
}
