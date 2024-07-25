package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Flight;

import java.util.List;

public interface Filter {

    default List<Flight> doFilter(List<Flight> flights) {
        return flights.stream().filter(this::test).toList();
    }

    boolean test(Flight flight);

}
