package lol.farsight.divine;

import static lol.farsight.divine.parser.combinator.Combinator.*;

import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.parser.combinator.Combinator;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;

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
                    padded(just('-'))
                            .ignoreThen(expr)
                            .map(BigDecimal::negate),
                    expr.delimitedBy(
                            padded(just('(')),
                            padded(just(')'))
                    )
            );

            final var multiply = atom.foldLeft(
                    padded(just('*'))
                            .or(padded(just('/')))
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
                    padded(just('+'))
                            .or(padded(just('-')))
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

        System.out.println(exprParser.parse(InputCursor.from("1+1+1+1+1+1")));
    }
}
