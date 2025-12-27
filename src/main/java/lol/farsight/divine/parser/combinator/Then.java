package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.Combinator;
import org.jetbrains.annotations.NotNull;

public record Then<E, OA, OB>(
        @NotNull Combinator<E, OA> parserA,
        @NotNull Combinator<E, OB> parserB
) implements Combinator<E, Pair<OA, OB>> {
    public Then {
        Conditions.nonNull(parserA, "parserA");
        Conditions.nonNull(parserB, "parserB");
    }

    @Override
    public @NotNull Option<Pair<OA, OB>> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var valA = parserA.go(input);
        if (valA.isNone())
            return valA.cast();

        final var valB = parserB.go(input);
        if (valB.isNone())
            return valB.cast();

        return Option.some(
                new Pair<>(
                        valA.unwrap(),
                        valB.unwrap()
                )
        );
    }
}
