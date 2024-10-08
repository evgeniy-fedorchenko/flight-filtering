package com.gridnine.testing;

import com.gridnine.testing.filter.Filter;
import com.gridnine.testing.filter.FilterFactory;
import com.gridnine.testing.flight.Flight;
import com.gridnine.testing.flight.FlightBuilder;

import java.util.Collection;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<Flight> flights = FlightBuilder.createFlights();
        Collection<Filter> filters = new FilterFactory().getFilters();
        String viewPattern = "Фильтр: %s\nРезультат: %s\nОтфильтровано полётов: %s\n\n";

        filters.forEach(f -> {
            Collection<Flight> result = f.doFilter(flights);
            System.out.printf(viewPattern, f.getName(), result, flights.size() - result.size());
        });
    }
}
