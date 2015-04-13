package com.lunagameserve.get_shaded.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class IOUtil {
    public static InputStream webRequestToStream(String requestUrl)
            throws IOException {
        return new URL(requestUrl).openStream();
    }
}
