package lol.farsight.divine.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public sealed interface Option<T> {
    @Contract("_ -> new")
    static <T> @NotNull Option<T> some(final T value) {
        return new Some<>(value);
    }

    @Contract("-> new")
    static <T> @NotNull Option<T> none() {
        return new None<>();
    }

    record Some<T>(T value) implements Option<T> {
        @Override
        public boolean isSome() {
            return true;
        }

        @Override
        public boolean isNone() {
            return false;
        }

        @Override
        public @NotNull <E> Result<T, E> okOr(final E value) {
            return Result.ok(this.value);
        }

        @Override
        public @NotNull <E> Result<T, E> ok() {
            return Result.ok(value);
        }

        @Override
        public boolean is(final @NotNull Option<T> other) {
            Conditions.nonNull(other, "other");

            if (other.isNone())
                return false;

            return Objects.equals(
                    other.unwrap(),
                    value
            );
        }

        @Override
        public @NotNull Option<T> filter(final @NotNull Predicate<T> function) {
            Conditions.nonNull(function, "function");

            if (function.test(value))
                return this;

            return Option.none();
        }

        @Override
        public void ifSome(final @NotNull Consumer<T> function) {
            Conditions.nonNull(function, "function");

            function.accept(value);
        }

        @Override
        public @NotNull <M> Option<M> cast() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <M> Option<M> map(final @NotNull Function<T, M> function) {
            Conditions.nonNull(function, "function");

            return Option.some(
                    function.apply(value)
            );
        }

        @Override
        public <M> Option<M> andThen(final @NotNull Function<T, Option<M>> function) {
            Conditions.nonNull(function, "function");

            return function.apply(value);
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public T unwrapOr(final T value) {
            return this.value;
        }
    }

    record None<T>() implements Option<T> {
        @Override
        public boolean isSome() {
            return false;
        }

        @Override
        public boolean isNone() {
            return true;
        }

        @Override
        public @NotNull <E> Result<T, E> okOr(final E value) {
            return Result.err(value);
        }

        @Override
        public @NotNull <E> Result<T, E> ok() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean is(final @NotNull Option<T> other) {
            Conditions.nonNull(other, "other");

            return other.isNone();
        }

        @Override
        public @NotNull Option<T> filter(final @NotNull Predicate<T> function) {
            Conditions.nonNull(function, "function");

            return Option.none();
        }

        @Override
        public void ifSome(final @NotNull Consumer<T> function) {
            Conditions.nonNull(function, "function");
        }

        @Override
        public @NotNull <M> Option<M> cast() {
            return Option.none();
        }

        @Override
        public <M> Option<M> map(final @NotNull Function<T, M> function) {
            Conditions.nonNull(function, "function");

            return Option.none();
        }

        @Override
        public <M> Option<M> andThen(final @NotNull Function<T, Option<M>> function) {
            Conditions.nonNull(function, "function");

            return Option.none();
        }

        @Override
        public T unwrap() {
            throw new UnsupportedOperationException();
        }

        @Override
        public T unwrapOr(final T value) {
            return value;
        }
    }

    boolean isSome();
    boolean isNone();

    <E> @NotNull Result<T, E> okOr(final E value);

    <E> @NotNull Result<T, E> ok();

    boolean is(final @NotNull Option<T> other);

    @NotNull Option<T> filter(final @NotNull Predicate<T> function);

    void ifSome(final @NotNull Consumer<T> function);

    <M> @NotNull Option<M> cast();

    <M> Option<M> map(final @NotNull Function<T, M> function);
    <M> Option<M> andThen(final @NotNull Function<T, Option<M>> function);

    T unwrap();
    T unwrapOr(final T value);
}
