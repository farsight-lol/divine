package lol.farsight.divine;

import static lol.farsight.divine.parser.combinator.Combinator.*;

import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.parser.combinator.Combinator;
import org.junit.jupiter.api.Test;

class ParsingTests {
    @Test
    void calculator() {
        final var exprParser = Combinator.<Character, Float>recursive(expr -> {
            final var number = padded(
                    digits()
                            .then(just('.').ignoreThen(digits()).orNot())
                            .map(a -> {
                                final var builder = new StringBuilder(a.a());
                                if (a.b().isSome())
                                    builder.append('.').append(a.b().unwrap());

                                return Float.parseFloat(builder.toString());
                            })
            );

            final var atom = choices(
                    number,
                    padded(just('-'))
                            .ignoreThen(expr)
                            .map(a -> -a),
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
                    (a, b) -> switch (b.a().getFirst()) {
                        case '*' -> a * b.b();
                        case '/' -> a / b.b();

                        // unreachable
                        default -> null;
                    }
            );

            return multiply.foldLeft(
                    padded(just('+'))
                            .or(padded(just('-')))
                            .then(multiply)
                            .repeated(),
                    (a, b) -> switch (b.a().getFirst()) {
                        case '+' -> a + b.b();
                        case '-' -> a - b.b();

                        // unreachable
                        default -> null;
                    }
            );
        });

        System.out.println(exprParser.parse(InputCursor.from("1 / 2 + 1.1 * 1.3454 / (0.4 - 2)")));
    }
}
