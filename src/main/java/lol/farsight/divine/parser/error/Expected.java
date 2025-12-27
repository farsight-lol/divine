package lol.farsight.divine.parser.error;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Expected<E>(
        int index,
        Option<E> found,
        @NotNull Set<E> expected
) implements CombinatorError<E> {
    public Expected {
        Conditions.nonNull(expected, "expected");
        Conditions.nonNull(found, "found");
    }

    @Override
    public @NotNull Error<E> merge(final @NotNull Error<E> other) {
        Conditions.nonNull(other, "other");

        assert index() == other.index();

        if (other instanceof Expected<E> otherExpected) {
            assert Objects.equals(
                    found,
                    otherExpected.found
            );

            return new Expected<>(
                    index,
                    found,
                    Stream.concat(
                            expected.stream(),
                            otherExpected.expected.stream()
                    ).collect(Collectors.toUnmodifiableSet())
            );
        }

        return this;
    }
}