package com.landkid.said.util;

import com.squareup.otto.Bus;

/**
 * Created by landkid on 2017. 6. 21..
 */

public final class BusProvider {
    private static final Bus BUS = new Bus();

    public static Bus getInstance() {
        return BUS;
    }

    private BusProvider() {

    }
}
