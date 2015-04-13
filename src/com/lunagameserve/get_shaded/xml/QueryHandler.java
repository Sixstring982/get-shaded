package com.lunagameserve.get_shaded.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class QueryHandler extends DefaultHandler {

    public List<Record> records = new ArrayList<Record>();

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        if(qName.equals("datapoint")) {
            for (int i = 0; i < attributes.getLength(); i++) {
                records.add(new Record(
                        attributes.getValue("owner"),
                        Long.parseLong(attributes.getValue("time")),
                        definedOrZero(attributes.getValue("lon")),
                        definedOrZero(attributes.getValue("lat")),
                        definedOrZero(attributes.getValue("alt")),
                        definedOrZero(attributes.getValue("accelx")),
                        definedOrZero(attributes.getValue("accely")),
                        definedOrZero(attributes.getValue("accelz")),
                        definedOrZero(attributes.getValue("rotx")),
                        definedOrZero(attributes.getValue("roty")),
                        definedOrZero(attributes.getValue("rotz")),
                        definedOrZero(attributes.getValue("light")),
                        definedOrZero(attributes.getValue("press"))
                ));
            }
        }
    }

    private double definedOrZero(String value) {
        if (value.equals("undefined")) {
            return 0.0;
        } else {
            return Double.parseDouble(value);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        super.characters(ch, start, length);
    }
}
