package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.Strategy;
import org.jetbrains.annotations.NotNull;

public record RecoverWith<E, O>(
        @NotNull Combinator<E, O> parser,
        @NotNull Strategy<E, O> strategy
) implements Combinator<E, O> {
    public RecoverWith {
        Conditions.nonNull(parser, "parser");
        Conditions.nonNull(strategy, "strategy");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var mark = input.mark();

        final var val = parser.go(input);
        if (val.isSome())
            return val;

        input.rewind(mark);

        final var recoverVal = strategy.recover(input, parser);
        if (recoverVal.isSome())
            return recoverVal;

        input.rewind(mark);

        return Option.none();
    }
}
