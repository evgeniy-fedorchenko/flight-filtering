package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Flight;

import java.time.LocalDateTime;

public class FlightBeforeNowFilter implements Filter {

    /**
     * Метод отфильтровывает неактуальные полёты.<br> Актуальным считается полёт, который не равен {@code null}
     * и у которого вылет <b><i>еще не случился</i></b> к моменту времени, когда выполняется этот метод. Результат
     * работы этого метода можно использовать для фильтрации коллекции объектов типа {@link Flight}
     *
     * @param flight Объект, представляющий проверяемый полёт
     * @return {@code true}, если вылет уже состоялся<br>
     * {@code false}, в противном случае, если он равен {@code null} или если у полёта нет сегментов
     */
    @Override
    public boolean test(Flight flight) {
        if (flight == null || flight.getSegments().isEmpty()) {
            return false;
        }
        return flight.getSegments().getFirst().getDepartureDate().isAfter(LocalDateTime.now());
    }
}