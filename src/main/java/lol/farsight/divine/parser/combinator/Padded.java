package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
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

        input.skipWhile(Character::isWhitespace);
        final var val = parser.go(input);
        input.skipWhile(Character::isWhitespace);

        return val;
    }
}
