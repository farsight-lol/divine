package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import org.jetbrains.annotations.NotNull;

public record OrNot<E, O>(
        @NotNull Combinator<E, O> parser
) implements Combinator<E, Option<O>> {
    @Override
    public @NotNull Option<Option<O>> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var mark = input.mark();

        final var val = parser.go(input);
        if (val.isNone()) {
            input.rewind(mark);

            return Option.some(Option.none());
        }

        return Option.some(val);
    }
}
