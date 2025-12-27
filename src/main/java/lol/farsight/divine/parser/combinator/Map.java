package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.parser.Combinator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record Map<E, O, OB>(
        @NotNull Combinator<E, O> parser,
        @NotNull Function<O, OB> function
) implements Combinator<E, OB> {
    public Map {
        Conditions.nonNull(parser, "parser");
        Conditions.nonNull(function, "function");
    }

    @Override
    public @NotNull Option<OB> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        return parser.go(input).map(function);
    }
}
