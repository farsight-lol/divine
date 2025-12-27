package lol.farsight.divine.parser.error;

import lol.farsight.divine.data.Conditions;
import lol.farsight.divine.data.Error;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Labelled<E>(
        @NotNull Error<E> error,
        @NotNull String label
) implements CombinatorError<E> {
    @Deprecated
    public Labelled {
        Conditions.nonNull(error, "error");
        Conditions.nonNull(label, "label");
    }

    @Override
    public @NotNull Error<E> merge(final @NotNull Error<E> other) {
        Conditions.nonNull(other, "other");

        assert index() == other.index();

        if (other instanceof Labelled(CombinatorError<?> otherError, String otherLabel)) {
            assert Objects.equals(
                    label,
                    otherLabel
            );

            // java is stupid
            // noinspection unchecked
            return new Labelled<>(
                    error.merge((Error<E>) otherError),
                    label
            );
        }

        return new Labelled<>(
                error.merge(other),
                label
        );
    }

    @Override
    public int index() {
        return error.index();
    }
}