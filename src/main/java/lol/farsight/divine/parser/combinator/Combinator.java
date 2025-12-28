package lol.farsight.divine.parser.combinator;

import lol.farsight.divine.data.*;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.parser.combinator.iter.IterCombinator;
import lol.farsight.divine.parser.combinator.iter.Repeated;
import lol.farsight.divine.parser.error.CombinatorError;
import lol.farsight.divine.parser.strategy.Strategy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Combinator<E, O> {
    static <E> @NotNull Any<E> any() {
        return new Any<>();
    }

    static <E> @NotNull End<E> end() {
        return new End<>();
    }

    @SafeVarargs
    static <E> @NotNull Just<E> just(final E @NotNull ... value) {
        Conditions.nonNull(value, "value");

        return new Just<>(Arrays.asList(value));
    }

    static @NotNull Just<Character> just(final @NotNull String value) {
        Conditions.nonNull(value, "value");

        final char[] unboxed = value.toCharArray();

        final List<@NotNull Character> boxed = new ArrayList<>(unboxed.length);
        for (char c : unboxed) boxed.add(c);

        return new Just<>(boxed);
    }

    static <O> @NotNull Padded<O> padded(final @NotNull Combinator<Character, O> parser) {
        return new Padded<>(parser);
    }

    static @NotNull Combinator<Character, String> identifier() {
        return Combinator.<Character>any()
                .filter(Character::isJavaIdentifierStart)
                .then(
                        Combinator.<Character>any()
                                .filter(Character::isJavaIdentifierPart)
                                .repeated()
                                .collect()
                )
                .map(chars ->
                        Stream.concat(
                                Stream.of(chars.a()),
                                chars.b().stream()
                        ).map(String::valueOf).collect(Collectors.joining())
                );
    }

    static @NotNull Combinator<Character, String> letters() {
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
                .collect()
                .map(chars ->
                        chars.stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining())
                );
    }

    @SafeVarargs
    static <E> @NotNull NoneOf<E> noneOf(final E @NotNull ... values) {
        Conditions.nonNull(values, "values");

        return new NoneOf<>(
                Arrays.stream(values).collect(Collectors.toUnmodifiableSet())
        );
    }

    @SafeVarargs
    static <E, O> @NotNull Choices<E, O> choices(final @NotNull Combinator<E, O> @NotNull ... parsers) {
        return new Choices<>(parsers);
    }

    static <E, O> @NotNull Recursive<E, O> recursive(final @NotNull UnaryOperator<Combinator<E, O>> function) {
        Conditions.nonNull(function, "function");

        final var recursive = new Recursive<E, O>();
        recursive.declare(function.apply(recursive));

        return recursive;
    }

    default @NotNull Choices<E, O> or(final @NotNull Combinator<E, O> parser) {
        return new Choices<>(this, parser);
    }

    default <OB> @NotNull FoldLeft<E, O, OB> foldLeft(
            final @NotNull IterCombinator<E, OB> parser,
            final @NotNull BiFunction<O, OB, O> function
    ) {
        return new FoldLeft<>(this, parser, function);
    }

    default @NotNull RecoverWith<E, O> recoverWith(final @NotNull Strategy<E, O> strategy) {
        return new RecoverWith<>(this, strategy);
    }

    default @NotNull ThenIgnore<E, O> thenIgnore(final @NotNull Combinator<E, ?> other) {
        return new ThenIgnore<>(this, other);
    }

    default @NotNull AndIs<E, O> andIs(final @NotNull Combinator<E, ?> other) {
        return new AndIs<>(this, other);
    }

    default <OB> @NotNull IgnoreThen<E, OB> ignoreThen(final @NotNull Combinator<E, OB> other) {
        return new IgnoreThen<>(this, other);
    }

    default <OB> @NotNull Then<E, O, OB> then(final @NotNull Combinator<E, OB> other) {
        return new Then<>(this, other);
    }

    default <OA> @NotNull FilterMap<E, OA, O> filterMap(final @NotNull Function<O, Option<OA>> function) {
        return new FilterMap<>(this, function);
    }

    default @NotNull FilterMap<E, O, O> filter(final @NotNull Predicate<O> function) {
        Conditions.nonNull(function, "function");

        return filterMap(value -> {
            if (function.test(value))
                return Option.some(value);

            return Option.none();
        });
    }

    default @NotNull Labelled<E, O> labelled(final @NotNull String label) {
        return new Labelled<>(this, label);
    }

    default @NotNull DelimitedBy<E, O> delimitedBy(
            final @NotNull Combinator<E, ?> parserA,
            final @NotNull Combinator<E, ?> parserB
    ) {
        return new DelimitedBy<>(parserA, this, parserB);
    }

    default <OB> @NotNull To<E, O, OB> to(final OB value) {
        return new To<>(this, value);
    }

    default @NotNull To<E, O, Unit> ignored() {
        return to(Unit.INSTANCE);
    }

    default @NotNull OrNot<E, O> orNot() {
        return new OrNot<>(this);
    }

    default <OB> @NotNull Map<E, O, OB> map(final @NotNull Function<O, OB> function) {
        return new Map<>(this, function);
    }

    default @NotNull Repeated<E, O> repeated(int atLeast, int atMost) {
        return new Repeated<>(this, atLeast, atMost);
    }

    default @NotNull Repeated<E, O> repeated(int atLeast) {
        return repeated(atLeast, -1);
    }

    default @NotNull Repeated<E, O> repeated() {
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
