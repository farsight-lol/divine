package lol.farsight.divine.parser.strategy;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.data.Unit;
import lol.farsight.divine.parser.combinator.Combinator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

import static lol.farsight.divine.parser.combinator.Combinator.*;

public record ViaParser<E, O>(
        @NotNull Combinator<E, O> parser
) implements Strategy<E, O> {
    public static <E, O> Combinator<E, O> nestedDelimiters(
            final @NotNull E start,
            final @NotNull E end,
            final @NotNull Supplier<O> function
    ) {
        Conditions.nonNull(start, "start");
        Conditions.nonNull(end, "end");
        Conditions.nonNull(function, "function");

        return Combinator.<E, Unit>recursive(block ->
            choices(
                    block.delimitedBy(
                            just(start),
                            just(end)
                    ),
                    noneOf(start, end).ignored()
            ).repeated().ignored()
        ).delimitedBy(
                just(start),
                just(end)
        ).map(_ -> function.get());
    }

    public ViaParser {
        Conditions.nonNull(parser, "parser");
    }

    @Override
    public @NotNull Option<O> recover(
            final @NotNull InputCursor<E> input,
            final @NotNull Combinator<E, O> parser
    ) {
        final var primary = input.takePrimary().unwrap();

        final var val = this.parser.go(input);
        if (val.isNone()) {
            input.overridePrimary(primary);

            return Option.none();
        }

        input.emitAlternate(primary);

        return val;
    }
}
