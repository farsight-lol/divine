package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

public record Padded<O>(
        @NotNull Combinator<Character, O> parser
) implements Combinator<Character, O> {
    public Padded {
        Conditions.nonNull(parser, "parser");
    }

    @Override
    public @NotNull Option<O> go(final @NotNull InputCursor<Character> input) {
        Conditions.nonNull(input, "input");

        skipWhitespace(input);
        final var val = parser.go(input);
        skipWhitespace(input);

        return val;
    }

    private void skipWhitespace(final @NotNull InputCursor<Character> input) {
        Option<Character> next;
        do {
            next = input.next();
        } while (next.isSome() && Character.isWhitespace(next.unwrap()));
    }
}
