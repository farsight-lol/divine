package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import org.jetbrains.annotations.NotNull;

public record Choices<E, O>(@NotNull Combinator<E, O> @NotNull ... parsers) implements Combinator<E, O> {
    @SafeVarargs // stupid ass annotation placement
    public Choices {
        Conditions.elementsNonNull(parsers, "element of parsers");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var mark = input.mark();
        for (final Combinator<E, O> parser : parsers) {
            final var val = parser.go(input);
            if (val.isSome())
                return val;

            input.rewind(mark);
        }

        return Option.none();
    }
}
