package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.combinator.iter.IterCombinator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record Collect<E, O>(
        @NotNull IterCombinator<E, O> parser
) implements Combinator<E, @NotNull List<O>> {
    @Override
    public @NotNull Option<@NotNull List<O>> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var state = new IterCombinator.State();
        final var list = new ArrayList<O>();
        while (true) {
            final var val = parser.next(input, state);
            if (val.isErr()) return switch (val.unwrapErr()) {
                case TOO_FEW_ENTRIES -> Option.none();
                case REACHED_MAX, STOPPED -> Option.some(list);
            };

            list.add(val.unwrap());
        }
    }
}
