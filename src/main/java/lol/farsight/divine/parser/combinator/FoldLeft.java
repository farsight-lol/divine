package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.combinator.iter.IterCombinator;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;

public record FoldLeft<E, O, OB>(
        @NotNull Combinator<E, O> parserA,
        @NotNull IterCombinator<E, OB> parserB,
        @NotNull BiFunction<O, OB, O> function
) implements Combinator<E, O> {
    public FoldLeft {
        Conditions.nonNull(parserA, "parserA");
        Conditions.nonNull(parserB, "parserB");
        Conditions.nonNull(function, "function");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var state = new IterCombinator.State();

        final var outOption = parserA.go(input);
        if (outOption.isNone())
            return Option.none();

        var out = outOption.unwrap();
        while (true) {
            final var val = parserB.next(input, state);
            if (val.isErr()) return switch (val.unwrapErr()) {
                case TOO_FEW_ENTRIES -> Option.none();
                case STOPPED, REACHED_MAX -> Option.some(out);
            };

            out = function.apply(out, val.unwrap());
        }
    }
}
