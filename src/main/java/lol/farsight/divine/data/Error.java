package lol.farsight.divine.data;

import lol.farsight.divine.parser.CombinatorError;
import lol.farsight.divine.parser.error.Labelled;
import org.jetbrains.annotations.NotNull;

public interface Error<E> {
    int index();

    @NotNull Error<E> merge(final @NotNull Error<E> other);

    Error<E> label(final @NotNull String label);

    void report();
}
