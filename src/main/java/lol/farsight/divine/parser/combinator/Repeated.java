package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public record Repeated<E, O>(
        @NotNull Combinator<E, O> parser,
        int atLeast,
        int atMost
) implements Combinator<E, List<O>> {
    public Repeated {
        Conditions.nonNull(parser, "parser");
    }

    @Override
    public @NotNull Option<List<O>> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var startMark = input.mark();

        final var list = new ArrayList<O>();
        while (atMost < 0 || list.size() < atMost) {
            final var mark = input.mark();

            final var val = parser.go(input);
            if (val.isNone()) {
                input.rewind(mark);

                break;
            }

            if (input.index() == mark.index())
                throw new IllegalStateException(
                        "repeated() succeeded without consuming input (infinite loop)"
                );

            list.add(val.unwrap());
        }

        final int size = list.size();
        if (size < atLeast) {
            input.rewind(startMark);
            input.emitPrimary(CombinatorError.simple(
                    startMark.index(),
                    "expected at least " + atLeast + " repetitions"
            ));

            return Option.none();
        }

        return Option.some(list);
    }
}
