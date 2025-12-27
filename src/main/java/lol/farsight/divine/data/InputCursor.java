package lol.farsight.divine.data;

import lol.farsight.divine.parser.Combinator;
import lol.farsight.divine.parser.CombinatorError;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

public final class InputCursor<E> {
    public static @NotNull InputCursor<@NotNull Character> from(final @NotNull String data) {
        Conditions.nonNull(data, "data");

        final byte[] bytes = data.getBytes();
        final Character[] boxed = IntStream.range(0, bytes.length)
                .mapToObj(i -> (char) bytes[i])
                .toArray(Character[]::new);

        return new InputCursor<>(boxed);
    }

    public record Mark(int index, int alternatesSize) {}

    private @NotNull Option<Error<E>> primary = Option.none();
    private @NotNull List<Error<E>> alternates = new ArrayList<>();

    private final @NotNull List<E> data;
    private int index = 0;

    public InputCursor(final @NotNull List<E> data) {
        Conditions.nonNull(data, "data");

        this.data = data;
    }

    @SafeVarargs
    public InputCursor(final E @NotNull ... data) {
        Conditions.nonNull(data, "data");

        this.data = new ArrayList<>(
                Arrays.asList(data)
        );
    }

    public @NotNull Option<E> peekAt(final int target) {
        if (target < 0 || target >= data.size())
            return Option.none();

        return Option.some(
                data.get(target)
        );
    }

    public @NotNull Option<E> peekAtOffset(final int offset) {
        return peekAt(index + offset);
    }

    public @NotNull Option<E> next() {
        final var element = peekAtOffset(0);
        index++;

        return element;
    }

    public void emitAlternate(final @NotNull Error<E> error) {
        Conditions.nonNull(error, "error");

        alternates.add(error);
    }

    public void emitPrimary(final @NotNull Error<E> error) {
        Conditions.nonNull(error, "error");

        if (primary.isNone()) {
            primary = Option.some(error);

            return;
        }

        final var primaryErr = primary.unwrap();

        Error<E> newPrimary;
        if (primaryErr.index() > error.index()) {
            newPrimary = primaryErr;
        } else if (error.index() > primaryErr.index()) {
            newPrimary = error;
        } else newPrimary = primaryErr.merge(error);

        primary = Option.some(newPrimary);
    }

    public void overridePrimary(final @NotNull Error<E> error) {
        Conditions.nonNull(error, "error");

        primary = Option.some(error);
    }

    public @NotNull Option<Error<E>> takePrimary() {
        if (primary.isSome()) {
            final var val = primary;
            primary = Option.none();

            return val;
        }

        return Option.none();
    }

    public @NotNull List<Error<E>> takeAlternates() {
        final var val = new ArrayList<>(alternates);
        alternates.clear();;

        return val;
    }

    public int index() {
        return index;
    }

    public void modifyAlternates(final @NotNull UnaryOperator<@NotNull Error<E>> operator) {
        Conditions.nonNull(operator, "operator");

        alternates = new ArrayList<>(
                alternates.stream()
                        .map(operator)
                        .toList()
        );
    }

    public Mark mark() {
        return new Mark(index, alternates.size());
    }

    public void rewind(final @NotNull Mark mark) {
        Conditions.nonNull(mark, "mark");

        assert alternates.size() >= mark.alternatesSize;

        index = mark.index;
        alternates.subList(
                mark.alternatesSize,
                alternates.size()
        ).clear();
    }
}
