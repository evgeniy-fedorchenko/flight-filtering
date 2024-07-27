package com.gridnine.testing.flight;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Класс помещен в пакет flight, не потому что он тестирует что-то, что находится в этом пакете,
 * а чтобы иметь доступ к конструкторам классов {@link Flight} и {@link Segment}
 */
public class Constants {

    public static Flight getFlightWithTreeHourBetweenAllSegments() {
        return new Flight(List.of(
                new Segment(LocalDateTime.now(), nowPlusHours(1)),
                new Segment(nowPlusHours(2), nowPlusHours(3)),
                new Segment(nowPlusHours(4), nowPlusHours(5)),
                new Segment(nowPlusHours(6), nowPlusHours(7))
        ));
    }

    public static Flight getFlightWithOneHourBetweenAllSegments() {
        return new Flight(List.of(
                new Segment(LocalDateTime.now(), nowPlusMin(1)),
                new Segment(nowPlusMin(2), nowPlusMin(3)),
                new Segment(nowPlusMin(4), nowPlusMin(5)),
                new Segment(nowPlusMin(6), nowPlusMin(7))
        ));
    }



    public static Flight getFlightWithDepartureAfterArrivalAtSameSegments() {
        return new Flight(List.of(
                new Segment(LocalDateTime.now(), nowPlusMin(1)),
                new Segment(nowPlusMin(2), nowPlusMin(3)),
                new Segment(nowPlusMin(4), nowPlusMin(2)),
                new Segment(nowPlusMin(6), nowPlusMin(7))
        ));
    }

    public static Flight getFlightWithArrivalAfterDepartureAtAllSegments() {
        return new Flight(List.of(
                new Segment(LocalDateTime.now(), nowPlusMin(1)),
                new Segment(nowPlusMin(2), nowPlusMin(3)),
                new Segment(nowPlusMin(4), nowPlusMin(5)),
                new Segment(nowPlusMin(6), nowPlusMin(7))
        ));
    }



    public static Flight getDepartureOfFlightBeforeNow() {
        return new Flight(List.of(
                new Segment(LocalDateTime.now().minusMinutes(1), nowPlusMin(1)),
                new Segment(nowPlusMin(2), nowPlusMin(3)),
                new Segment(nowPlusMin(4), nowPlusMin(5)),
                new Segment(nowPlusMin(6), nowPlusMin(7))
        ));
    }

    public static Flight getDepartureOfFlightAfterNow() {
        return new Flight(List.of(
                new Segment(LocalDateTime.now().plusSeconds(30), nowPlusMin(1)),
                new Segment(nowPlusMin(2), nowPlusMin(3)),
                new Segment(nowPlusMin(4), nowPlusMin(5)),
                new Segment(nowPlusMin(6), nowPlusMin(7))
        ));
    }

    private static LocalDateTime nowPlusMin(int minutes) {
        return LocalDateTime.now().plusMinutes(minutes);
    }

    private static LocalDateTime nowPlusHours(int hours) {
        return LocalDateTime.now().plusHours(hours);
    }
}
