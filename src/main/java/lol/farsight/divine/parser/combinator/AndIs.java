package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import org.jetbrains.annotations.NotNull;

public record AndIs<E, O>(
        @NotNull Combinator<E, O> parserA,
        @NotNull Combinator<E, ?> parserB
) implements Combinator<E, O> {
    public AndIs {
        Conditions.nonNull(parserA, "parserA");
        Conditions.nonNull(parserB, "parserB");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var startMark = input.mark();
        final var valA = parserA.go(input);

        final var afterMark = input.mark();
        input.rewind(startMark);

        if (valA.isNone())
            return Option.none();
        if (parserB.go(input).isNone())
            return Option.none();

        input.rewind(afterMark);

        return valA;
    }
}
