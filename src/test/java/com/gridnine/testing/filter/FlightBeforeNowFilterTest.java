package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Constants;
import com.gridnine.testing.flight.Flight;
import org.junit.jupiter.api.Test;

import java.util.Collection;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class FlightBeforeNowFilterTest implements FilterTest {

    @Test
    @Override
    public void filterTest() {
        Collection<Flight> testFlights = Collections.singletonList(Constants.getDepartureOfFlightBeforeNow());
        Collection<Flight> resultFlights = new FlightBeforeNowFilter().doFilter(testFlights);

        assertNotEquals(0, testFlights.size());
        assertEquals(0, resultFlights.size());
    }

    @Test
    @Override
    public void unfilteredTest() {
        Collection<Flight> testFlights = Collections.singletonList(Constants.getDepartureOfFlightAfterNow());
        Collection<Flight> resultFlights = new FlightBeforeNowFilter().doFilter(testFlights);

        assertNotEquals(0, testFlights.size());
        assertEquals(testFlights.size(), resultFlights.size());
    }

    @Test
    @Override
    public void nullSafeTest() {
        assertDoesNotThrow(() -> new FlightBeforeNowFilter().doFilter(Collections.emptyList()));
    }

}
