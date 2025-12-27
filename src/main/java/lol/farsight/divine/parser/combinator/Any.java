package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

public record Any<E>() implements Combinator<E, E> {
    @Override
    public @NotNull Option<E> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var startMark = input.mark();

        final var next = input.next();
        if (next.isNone()) {
            input.rewind(startMark);
            input.emitPrimary(CombinatorError.simple(
                    startMark.index(),
                    "expected anything"
            ));

            return Option.none();
        }

        return next;
    }
}
