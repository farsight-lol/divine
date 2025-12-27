package lol.farsight.divine.parser;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.error.Expected;
import lol.farsight.divine.parser.error.Labelled;
import lol.farsight.divine.parser.error.Simple;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface CombinatorError<E> extends Error<E> {
    static <E> Error<E> simple(
            int index,
            @NotNull String @NotNull ... messages
    ) {
        Conditions.nonNull(messages, "messages");

        return new Simple<>(
                index,
                Arrays.stream(messages).collect(Collectors.toUnmodifiableSet())
        );
    }

    @SafeVarargs
    static <E> Error<E> expected(
            int index,
            @NotNull Option<E> found,
            @NotNull E @NotNull ... expected
    ) {
        Conditions.nonNull(expected, "expected");

        return expected(
                index,
                found,
                Arrays.stream(expected).collect(Collectors.toUnmodifiableSet())
        );
    }

    static <E> Error<E> expected(
            int index,
            @NotNull Option<E> found,
            @NotNull Set<E> expected
    ) {
        Conditions.nonNull(expected, "expected");

        return new Expected<>(
                index,
                found,
                expected
        );
    }

    default Error<E> label(final @NotNull String label) {
        return new Labelled<>(
                this,
                label
        );
    }

    default void report() {
        System.err.println(this);
    }
}
