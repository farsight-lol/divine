package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import org.jetbrains.annotations.NotNull;

public record Labelled<E, O>(
        @NotNull Combinator<E, O> parser,
        @NotNull String label
) implements Combinator<E, O> {
    public Labelled {
        Conditions.nonNull(parser, "parser");
        Conditions.nonNull(label, "label");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var prevPrimary = input.takePrimary();
        final var startMark = input.mark();

        final var val = parser.go(input);

        final var nextPrimary = input.takePrimary();
        prevPrimary.ifSome(input::emitPrimary);

        if (nextPrimary.isSome()) {
            var err = nextPrimary.unwrap();

            final int startIndex = startMark.index();
            final int nextIndex = err.index();
            if (startIndex >= nextIndex)
                err = err.label(label);

            input.emitPrimary(err);
        }

        input.modifyAlternates(err -> err.label(label));

        return val;
    }
}
