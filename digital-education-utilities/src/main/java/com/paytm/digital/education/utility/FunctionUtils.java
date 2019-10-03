package com.paytm.digital.education.utility;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Function;

public class FunctionUtils {
    public static <S, T> T fetchIfPresentFromNullable(
            @Nullable S source,
            Function<S, T> sourceToTarget) {
        return Optional.ofNullable(source).map(sourceToTarget).orElse(null);
    }

    public static <S, T1, T2> T2 fetchIfPresentFromNullable(
            @Nullable S source,
            Function<S, T1> sourceToTarget1,
            Function<T1, T2> sourceToTarget2) {
        return Optional.ofNullable(source).map(sourceToTarget1).map(sourceToTarget2).orElse(null);
    }

    public static <S, T1, T2, T3> T3 fetchIfPresentFromNullable(
            @Nullable S source,
            Function<S, T1> sourceToTarget1,
            Function<T1, T2> sourceToTarget2,
            Function<T2, T3> sourceToTarget3) {
        return Optional
                .ofNullable(source)
                .map(sourceToTarget1)
                .map(sourceToTarget2)
                .map(sourceToTarget3)
                .orElse(null);
    }
}
