package com.gridnine.testing.filter;

import com.gridnine.testing.flight.Flight;

public class GroundTimeLimitFilter implements Filter {

    @Override
    public boolean test(Flight flight) {
        return false;
    }

}
