package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

public record Just<E>(E value) implements Combinator<E, E> {
    @Override
    public @NotNull Option<E> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var mark = input.mark();

        final var val = input.next();
        if (val.isNone()) {
            input.rewind(mark);
            input.emitPrimary(CombinatorError.expected(
                    mark.index(),
                    Option.none(),
                    value
            ));

            return Option.none();
        }

        final var other = val.unwrap();
        if (Objects.equals(other, value))
            return Option.some(other);

        input.rewind(mark);
        input.emitPrimary(CombinatorError.expected(
                mark.index(),
                Option.some(other),
                value
        ));

        return Option.none();
    }
}
