package lol.farsight.divine.parser;

import lol.farsight.divine.data.InputCursor;
import lol.farsight.divine.data.Option;
import lol.farsight.divine.parser.strategy.ViaParser;
import org.jetbrains.annotations.NotNull;

public interface Strategy<E, O> {
    static <E, O> Strategy<E, O> viaParser(final @NotNull Combinator<E, O> parser) {
        return new ViaParser<>(parser);
    }

    @NotNull Option<O> recover(
            final @NotNull InputCursor<E> input,
            final @NotNull Combinator<E, O> parser
    );
}
