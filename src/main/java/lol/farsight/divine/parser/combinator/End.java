package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public record End<E>() implements Combinator<E, Unit> {
    @Override
    public @NotNull Option<Unit> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var mark = input.mark();

        final var next = input.next();
        if (next.isNone())
            return Option.some(Unit.INSTANCE);

        input.rewind(mark);
        input.emitPrimary(CombinatorError.simple(
                mark.index(),
                "expected end of input"
        ));

        return Option.none();
    }
}
