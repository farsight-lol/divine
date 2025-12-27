package lol.farsight.divine.parser;

import lol.farsight.divine.data.*;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.parser.combinator.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public interface Combinator<E, O> {
    static <E> @NotNull Any<E> any() {
        return new Any<>();
    }

    static <E> @NotNull End<E> end() {
        return new End<>();
    }

    static <E> @NotNull Just<E> just(final E value) {
        return new Just<>(value);
    }

    static <O> @NotNull Combinator<Character, O> padded(final @NotNull Combinator<Character, O> parser) {
        return new Padded<>(parser);
    }

    static @NotNull Combinator<Character, String> identifier() {
        return charMonotype(Character::isAlphabetic);
    }

    static @NotNull Combinator<Character, String> digits() {
        return charMonotype(Character::isDigit);
    }

    static @NotNull Combinator<Character, String> charMonotype(final @NotNull Predicate<Character> function) {
        Conditions.nonNull(function, "function");

        return Combinator.<Character>any()
                .filter(function)
                .repeated(1)
                .map(chars ->
                        chars.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining())
                );
    }

    @SafeVarargs
    static <E> @NotNull Combinator<E, E> noneOf(final E @NotNull ... values) {
        Conditions.nonNull(values, "values");

        return new NoneOf<>(
                Arrays.stream(values).collect(Collectors.toUnmodifiableSet())
        );
    }

    @SafeVarargs
    static <E, O> @NotNull Combinator<E, O> choices(final @NotNull Combinator<E, O> @NotNull ... parsers) {
        return new Choices<>(parsers);
    }

    static <E, O> @NotNull Combinator<E, O> recursive(final @NotNull UnaryOperator<Combinator<E, O>> function) {
        Conditions.nonNull(function, "function");

        final var recursive = new Recursive<E, O>();
        recursive.declare(function.apply(recursive));

        return recursive;
    }

    default @NotNull Choices<E, O> or(final @NotNull Combinator<E, O> parser) {
        return new Choices<>(this, parser);
    }

    default @NotNull Combinator<E, O> recoverWith(final @NotNull Strategy<E, O> strategy) {
        return new RecoverWith<>(this, strategy);
    }

    default @NotNull Combinator<E, O> thenIgnore(final @NotNull Combinator<E, ?> other) {
        return new ThenIgnore<>(this, other);
    }

    default @NotNull Combinator<E, O> andIs(final @NotNull Combinator<E, ?> other) {
        return new AndIs<>(this, other);
    }

    default <OB> @NotNull Combinator<E, OB> ignoreThen(final @NotNull Combinator<E, OB> other) {
        return new IgnoreThen<>(this, other);
    }

    default <OB> @NotNull Combinator<E, Pair<O, OB>> then(final @NotNull Combinator<E, OB> other) {
        return new Then<>(this, other);
    }

    default <OA> @NotNull Combinator<E, OA> filterMap(final @NotNull Function<O, Option<OA>> function) {
        return new FilterMap<>(this, function);
    }

    default @NotNull Combinator<E, O> filter(final @NotNull Predicate<O> function) {
        Conditions.nonNull(function, "function");

        return filterMap(value -> {
            if (function.test(value))
                return Option.some(value);

            return Option.none();
        });
    }

    default @NotNull Combinator<E, O> labelled(final @NotNull String label) {
        return new Labelled<>(this, label);
    }

    default @NotNull Combinator<E, O> delimitedBy(
            final @NotNull Combinator<E, ?> parserA,
            final @NotNull Combinator<E, ?> parserB
    ) {
        return new DelimitedBy<>(parserA, this, parserB);
    }

    default <OB> @NotNull Combinator<E, OB> to(final OB value) {
        return new To<>(this, value);
    }

    default @NotNull Combinator<E, Unit> ignored() {
        return to(Unit.INSTANCE);
    }

    default <OB> @NotNull Combinator<E, OB> map(final @NotNull Function<O, OB> function) {
        return new Map<>(this, function);
    }

    default @NotNull Combinator<E, List<O>> repeated(int atLeast, int atMost) {
        return new Repeated<>(this, atLeast, atMost);
    }

    default @NotNull Combinator<E, List<O>> repeated(int atLeast) {
        return repeated(atLeast, -1);
    }

    default @NotNull Combinator<E, List<O>> repeated() {
        return repeated(0);
    }

    @NotNull Option<O> go(final @NotNull InputCursor<E> input);

    default @NotNull Pair<@NotNull Option<O>, @NotNull List<@NotNull Error<E>>> parse(final @NotNull InputCursor<E> input) {
        Conditions.nonNull(input, "input");

        final var res = thenIgnore(end()).go(input);

        final var errors = input.takeAlternates();
        if (res.isNone())
            errors.add(
                    input.takePrimary()
                            .unwrapOr(CombinatorError.expected(input.index(), Option.none()))
            );

        return new Pair<>(
                res,
                errors
        );
    }
}
