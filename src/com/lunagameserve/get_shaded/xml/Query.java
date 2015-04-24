package com.lunagameserve.get_shaded.xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class Query {

    private final String QUERY_BASE = "http://www.pollicompsus.com/get/xml/";

    private final String queryString;

    public Query() {
        queryString = QUERY_BASE;
    }

    public Query(List<String> filters) {
        StringBuilder builder = new StringBuilder("?");
        for (int i = 0; i < filters.size(); i++) {
            builder.append(filters.get(i));
            if (i < filters.size() - 1) {
                builder.append("&");
            }
        }

        queryString = QUERY_BASE + builder.toString();
    }

    public List<Record> execute()
            throws IOException, XmlPullParserException {

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput(new InputStreamReader(new URL(queryString).openStream()));
        int eventType = xpp.getEventType();
        List<Record> records = new ArrayList<Record>();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG &&
                    xpp.getName().equals("datapoint")) {
                Map<String, String> attributes =
                        new HashMap<String, String>();
                for (int i = 0; i < xpp.getAttributeCount(); i++) {
                    attributes.put(xpp.getAttributeName(i),
                            xpp.getAttributeValue(i));
                }

                Record r = new Record(
                        attributes.get("owner"),
                        parseLongOrZero(attributes.get("time")),
                        parseDoubleOrZero(attributes.get("lon")),
                        parseDoubleOrZero(attributes.get("lat")),
                        parseDoubleOrZero(attributes.get("alt")),
                        parseDoubleOrZero(attributes.get("accelx")),
                        parseDoubleOrZero(attributes.get("accely")),
                        parseDoubleOrZero(attributes.get("accelz")),
                        parseDoubleOrZero(attributes.get("rotx")),
                        parseDoubleOrZero(attributes.get("roty")),
                        parseDoubleOrZero(attributes.get("rotz")),
                        parseDoubleOrZero(attributes.get("light")),
                        parseDoubleOrZero(attributes.get("press")));

                records.add(r);
            }
            eventType = xpp.next();
        }
        return records;
    }

    private double parseDoubleOrZero(String in) {
        try {
            return Double.parseDouble(in);
        } catch (Exception e) {
            return 0.0;
        }
    }

    private long parseLongOrZero(String in) {
        try {
            return Long.parseLong(in);
        } catch (Exception e) {
            return 0L;
        }
    }
}
