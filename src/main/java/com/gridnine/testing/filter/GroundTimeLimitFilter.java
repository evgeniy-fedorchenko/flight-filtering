package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Flight;
import com.gridnine.testing.flight.Segment;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.IntStream;

public class GroundTimeLimitFilter implements Filter {

    /**
     * Настройки лимитов с которыми работает фильтр. Определение единицы времени
     * вынесено в отдельную переменную для более гибкой настройки фильтрации
     */
    private static final ChronoUnit UNIT = ChronoUnit.HOURS;
    private static final long AMOUNT = 2L;

    /**
     * Метод отфильтровывает полёты с <b><i>длительными</i></b> простоями в пути, а так же равные {@code null}<br>
     * Считается, что полёт имеет длительные простои, если общая длительность интервалов между прилётом
     * одного сегмента и вылетом следующего за ним больше {@value #AMOUNT} {@value #UNIT}
     *
     * @param flight Объект, представляющий проверяемый полёт
     * @return {@code true} если переданный аргумент {@code flight} не имеет длительных простоев.<br>
     *         {@code false} в противном случае или если переданный аргумент равен {@code null}
     */
    @Override
    public boolean test(Flight flight) {
        if (flight == null) {
            return false;
        }
        List<Segment> segments = flight.getSegments();

        return IntStream.range(0, segments.size() - 1)
                .mapToObj(idx ->
                        Duration.between(segments.get(idx).getArrivalDate(), segments.get(idx + 1).getDepartureDate()))
                .reduce(Duration.ZERO, Duration::plus)
                .compareTo(Duration.of(AMOUNT, UNIT)) < 0;
    }
}
