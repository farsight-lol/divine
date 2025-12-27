package lol.farsight.divine.parser.error;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record Simple<E>(
        int index,
        @NotNull Set<String> messages
) implements CombinatorError<E> {
    public Simple {
        Conditions.nonNull(messages, "messages");
    }

    @Override
    public @NotNull Error<E> merge(final @NotNull Error<E> other) {
        Conditions.nonNull(other, "other");

        assert index() == other.index();

        if (other instanceof Simple<?> otherSimple) {
            return new Simple<>(
                    index,
                    Stream.concat(
                            messages.stream(),
                            otherSimple.messages.stream()
                    ).collect(Collectors.toUnmodifiableSet())
            );
        }

        return this;
    }
}