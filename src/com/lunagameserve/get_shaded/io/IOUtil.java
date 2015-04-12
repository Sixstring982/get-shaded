package com.lunagameserve.get_shaded.io;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class IOUtil {
    public static String webRequestContents(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        InputStreamReader in = new InputStreamReader(url.openStream());

        StringBuilder builder = new StringBuilder();

        int readC;
        while ((readC = in.read()) != -1) {
            builder.append((char)readC);
        }

        return builder.toString();
    }
}
