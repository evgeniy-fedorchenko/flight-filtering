package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Flight;

public class EarlyArrivalSegmentFilter implements Filter {

    /**
     * Метод отфильтровывает некорректные полеты.
     * Корректным считается полёт, который не равняется {@code null} и если у
     * <b>каждого</b> его сегмента дата вылета предшествует (или равна) дате прилета
     *
     * @param flight Объект, представляющий проверяемый полёт
     * @return {@code true}, если полёт считается корректным<br>
     *         {@code false} в противном случае, или если переданный аргумент равен {@code null}
     */
    @Override
    public boolean test(Flight flight) {

        return flight != null && flight.getSegments().stream()
                .allMatch(segment -> segment.getDepartureDate().isBefore(segment.getArrivalDate()));
    }

}
