package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.*;
import org.jetbrains.annotations.NotNull;

public record To<E, O, OB>(
        @NotNull Combinator<E, O> parser,
        OB value
) implements Combinator<E, OB> {
    public To {
        Conditions.nonNull(parser, "parser");
    }

    @Override
    public @NotNull Option<OB> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var val = parser.go(input);
        if (val.isNone())
            return val.cast();

        return Option.some(value);
    }
}
