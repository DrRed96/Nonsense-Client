package wtf.bhopper.nonsense.util.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.*;
import java.util.stream.Collector;

public class CustomCollectors {

    public static <T> Collector<T, List<String>, List<String>> stringList(Function<T, String> toStringFunction) {
        return new Collector<>() {
            @Override
            public Supplier<List<String>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<String>, T> accumulator() {
                return (strings, t) -> strings.add(toStringFunction.apply(t));
            }

            @Override
            public BinaryOperator<List<String>> combiner() {
                return (strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                };
            }

            @Override
            public Function<List<String>, List<String>> finisher() {
                return strings -> strings;
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Set.of(Characteristics.UNORDERED);
            }
        };
    }

}
