package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.error.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public record Just<E>(@NotNull List<E> values) implements Combinator<E, @NotNull List<E>> {
    public Just {
        Conditions.nonNull(values, "values");
    }

    @Override
    public @NotNull Option<@NotNull List<E>> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var startMark = input.mark();
        for (final E value : values) {
            final var val = input.next();
            if (val.isNone()) {
                input.rewind(startMark);
                input.emitPrimary(CombinatorError.expectedExactly(
                        startMark.index(),
                        Option.none(),
                        Set.of(values)
                ));

                return Option.none();
            }

            final var other = val.unwrap();
            if (!Objects.equals(other, value)) {
                input.rewind(startMark);
                input.emitPrimary(CombinatorError.expected(
                        startMark.index(),
                        Option.some(other),
                        value
                ));

                return Option.none();
            }
        }

        return Option.some(
                new ArrayList<>(values)
        );
    }
}
