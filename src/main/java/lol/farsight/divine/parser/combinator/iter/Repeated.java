package lol.farsight.divine.parser.combinator.iter;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

public record Repeated<E, O>(
        @NotNull Combinator<E, O> parser,
        int atLeast,
        int atMost
) implements IterCombinator<E, O> {
    public Repeated {
        Conditions.nonNull(parser, "parser");
    }

    @Override
    public @NotNull Result<O, @NotNull Error> next(
            final @NotNull InputCursor<E> input,
            final @NotNull State state
    ) {
        Conditions.nonNull(input, "input");
        Conditions.nonNull(state, "state");

        if (atMost >= 0 && state.count >= atMost)
            return Result.err(Error.REACHED_MAX);

        final var mark = input.mark();

        final var val = parser.go(input);
        if (val.isNone()) {
            input.rewind(mark);

            if (state.count < atLeast)
                return Result.err(Error.TOO_FEW_ENTRIES);
            return Result.err(Error.STOPPED);
        }

        state.count++;

        return val.ok();
    }
}
