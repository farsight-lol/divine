package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.error.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record NoneOf<E>(@NotNull Set<E> values) implements Combinator<E, E> {
    public NoneOf {
        Conditions.nonNull(values, "values");
    }

    @Override
    public @NotNull Option<E> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var mark = input.mark();

        final var next = input.next();
        if (next.isNone()) {
            input.emitPrimary(CombinatorError.expected(
                    input.index(),
                    Option.none(),
                    values
            ));

            return Option.none();
        }

        final var other = next.unwrap();
        if (values.contains(other))
            return Option.some(other);

        input.rewind(mark);
        input.emitPrimary(CombinatorError.expected(
                input.index(),
                Option.some(other),
                values
        ));

        return Option.none();
    }
}
