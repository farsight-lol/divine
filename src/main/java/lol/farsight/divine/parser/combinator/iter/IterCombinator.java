package lol.farsight.divine.parser.combinator.iter;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.combinator.Collect;
import lol.farsight.divine.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

public interface IterCombinator<E, O> extends Combinator<E, Unit> {
    enum Error {
        TOO_FEW_ENTRIES,
        REACHED_MAX,
        REACHED_END
    }

    class State {
        public int count = 0;
    }

    default @NotNull Collect<E, O> collect() {
        return new Collect<>(this);
    }

    @Override
    default @NotNull Option<Unit> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var state = new State();
        while (true) {
            final var val = next(input, state);
            if (val.isErr()) return switch (val.unwrapErr()) {
                case TOO_FEW_ENTRIES -> Option.none();
                case REACHED_MAX, REACHED_END -> Option.some(Unit.INSTANCE);
            };
        }
    }

    @NotNull Result<O, @NotNull Error> next(
            final @NotNull InputCursor<E> input,
            final @NotNull State state
    );
}