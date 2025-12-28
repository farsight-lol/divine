package lol.farsight.divine;

import static lol.farsight.divine.parser.combinator.Combinator.*;

import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.parser.combinator.Combinator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;

class ParsingTests {
    @Test
    void calculator() {
        final var exprParser = Combinator.<Character, BigDecimal>recursive(expr -> {
            final var number = padded(
                    digits()
                            .then(just('.').ignoreThen(digits()).orNot())
                            .map(a -> {
                                final var builder = new StringBuilder(a.left());
                                if (a.right().isSome())
                                    builder.append('.').append(a.right().unwrap());

                                return new BigDecimal(builder.toString());
                            })
            );

            final var atom = choices(
                    number,
                    token('-')
                            .ignoreThen(expr)
                            .map(BigDecimal::negate),
                    expr.delimitedBy(
                            token('('),
                            token(')')
                    )
            );

            final var multiply = atom.foldLeft(
                    token('*')
                            .or(token('/'))
                            .then(atom)
                            .repeated(),
                    (a, b) -> switch (b.left().getFirst()) {
                        case '*' -> a.multiply(b.right());
                        case '/' -> a.divide(b.right(), MathContext.DECIMAL128);

                        // unreachable
                        default -> null;
                    }
            );

            return multiply.foldLeft(
                    token('+')
                            .or(token('-'))
                            .then(multiply)
                            .repeated(),
                    (a, b) -> switch (b.left().getFirst()) {
                        case '+' -> a.add(b.right());
                        case '-' -> a.subtract(b.right());

                        // unreachable
                        default -> null;
                    }
            );
        });

        final var val = exprParser.parse(InputCursor.from("1+1+1+1+1+1"));

        assert val.left().isSome();
        assert val.left().unwrap().equals(new BigDecimal(6));
        assert val.right().isEmpty();
    }

    @Test
    void separatedBy() {
        final var atom = token('1')
                .to("1")
                .separatedBy(token(','), 0, 4, false, true)
                .collect();

        final var val = atom.parse(InputCursor.from("1, 1, 1, 1"));

        assert val.left().isSome();
        assert val.left().unwrap().equals(List.of("1", "1", "1", "1"));
        assert val.right().isEmpty();
    }
}
