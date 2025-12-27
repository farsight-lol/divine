package lol.farsight.divine.data;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public sealed interface Result<T, E> {
    @Contract("_ -> new")
    static <T, E> @NotNull Result<T, E> ok(final T value) {
        return new Ok<>(value);
    }

    @Contract("_ -> new")
    static <T, E> @NotNull Result<T, E> err(final E err) {
        return new Err<>(err);
    }

    record Ok<T, E>(T value) implements Result<T, E> {
        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public Option<T> val() {
            return Option.some(value);
        }

        @Override
        public Option<E> err() {
            return Option.none();
        }

        @Override
        public @NotNull <M> Result<M, E> cast() {
            throw new UnsupportedOperationException();
        }

        @Override
        public @NotNull <M> Result<T, M> castErr() {
            return Result.ok(value);
        }

        @Override
        public <M> @NotNull Result<M, E> map(final @NotNull Function<T, M> function) {
            Conditions.nonNull(function, "function");

            return Result.ok(
                    function.apply(value)
            );
        }

        @Override
        public <M> @NotNull Result<T, M> mapErr(final @NotNull Function<E, M> function) {
            Conditions.nonNull(function, "function");

            return Result.ok(value);
        }

        @Override
        public T unwrap() {
            return value;
        }

        @Override
        public E unwrapErr() {
            throw new UnsupportedOperationException();
        }
    }

    record Err<T, E>(E error) implements Result<T, E> {
        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public Option<T> val() {
            return Option.none();
        }

        @Override
        public Option<E> err() {
            return Option.some(error);
        }

        @Override
        public @NotNull <M> Result<M, E> cast() {
            return Result.err(error);
        }

        @Override
        public @NotNull <M> Result<T, M> castErr() {
            throw new UnsupportedOperationException();
        }

        @Override
        public <M> @NotNull Result<M, E> map(final @NotNull Function<T, M> function) {
            Conditions.nonNull(function, "function");

            return Result.err(error);
        }

        @Override
        public <M> @NotNull Result<T, M> mapErr(final @NotNull Function<E, M> function) {
            Conditions.nonNull(function, "function");

            return Result.err(
                    function.apply(error)
            );
        }

        @Override
        public T unwrap() {
            throw new UnsupportedOperationException();
        }

        @Override
        public E unwrapErr() {
            return error;
        }
    }

    boolean isOk();
    boolean isErr();

    Option<T> val();
    Option<E> err();

    <M> @NotNull Result<M, E> cast();
    <M> @NotNull Result<T, M> castErr();

    <M> @NotNull Result<M, E> map(final @NotNull Function<T, M> function);
    <M> @NotNull Result<T, M> mapErr(final @NotNull Function<E, M> function);

    T unwrap();
    E unwrapErr();
}
