package lol.farsight.divine.parser.combinator.iter;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Result;
import lol.farsight.divine.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

public record SeparatedBy<E, O>(
        @NotNull Combinator<E, O> parser,
        @NotNull Combinator<E, ?> separator,
        int atLeast,
        int atMost,
        boolean allowLeading,
        boolean allowTrailing
) implements IterCombinator<E, O> {
    public SeparatedBy {
        Conditions.nonNull(parser, "parser");
        Conditions.nonNull(separator, "separator");
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

        final var beforeSeparator = input.mark();
        if (state.count == 0 && allowLeading) {
            if (separator.go(input).isNone())
                input.rewind(beforeSeparator);
        } else if (state.count > 0) {
            final var val = separator.go(input);
            if (val.isNone()) {
                input.rewind(beforeSeparator);

                if (state.count < atLeast)
                    return Result.err(Error.TOO_FEW_ENTRIES);
                return Result.err(Error.STOPPED);
            }
        }

        final var beforeItem = input.mark();
        final var val = parser.go(input);
        if (val.isNone()) {
            if (state.count < atLeast) {
                input.rewind(beforeSeparator);

                return Result.err(Error.TOO_FEW_ENTRIES);
            }

            input.rewind(
                    allowTrailing ? beforeItem : beforeSeparator
            );

            return Result.err(Error.STOPPED);
        }

        state.count++;

        return val.ok();
    }
}
