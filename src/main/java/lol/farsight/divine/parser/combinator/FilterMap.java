package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record FilterMap<E, O, OA>(
        @NotNull Combinator<E, OA> parser,
        @NotNull Function<OA, Option<O>> function
) implements Combinator<E, O> {
    public FilterMap {
        Conditions.nonNull(parser, "parser");
        Conditions.nonNull(function, "function");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final int index = input.index();
        final var found = input.peekAt(index);

        final var oldPrimary = input.takePrimary();
        final var res = parser.go(input);
        final var newPrimary = input.takePrimary();

        oldPrimary.ifSome(input::emitPrimary);
        if (res.isNone()) {
            newPrimary.ifSome(input::emitPrimary);

            return Option.none();
        }

        final var mapped = function.apply(res.unwrap());
        if (mapped.isSome()) {
            newPrimary.ifSome(input::emitPrimary);

            return mapped;
        }

        input.emitPrimary(CombinatorError.expected(
                index,
                found
        ));

        return Option.none();
    }
}
