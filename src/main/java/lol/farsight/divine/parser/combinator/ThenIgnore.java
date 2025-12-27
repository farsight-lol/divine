package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.Combinator;
import org.jetbrains.annotations.NotNull;

public record ThenIgnore<E, OA>(
        @NotNull Combinator<E, OA> parserA,
        @NotNull Combinator<E, ?> parserB
) implements Combinator<E, OA> {
    public ThenIgnore {
        Conditions.nonNull(parserA, "parserA");
        Conditions.nonNull(parserB, "parserB");
    }

    @Override
    public @NotNull Option<OA> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var valA = parserA.go(input);
        if (valA.isNone())
            return Option.none();

        if (parserB.go(input).isNone())
            return Option.none();

        return valA;
    }
}
