package com.lunagameserve.get_shaded.xml;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public enum Filter {
    Owner,
    MinLat,
    MaxLat,
    MinLon,
    MaxLon,
    MinTime,
    MaxTime;

    public String create(String filterValue) {
        return name().toLowerCase() + "=" + filterValue;
    }

    public String create(double filterValue) {
        return create(Double.toString(filterValue));
    }

    public String create(long filterValue) {
        return create(Long.toString(filterValue));
    }
}
