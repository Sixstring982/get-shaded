package com.lunagameserve.get_shaded.io;

import android.util.Log;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * @author Sixstring982
 * @since 4/12/2015
 */
public class IOUtil {
    public static String webRequestContents(String urlPath) throws IOException {
        URL url = new URL(urlPath);
        HttpsURLConnection urlConnection =
                (HttpsURLConnection)url.openConnection();
        //urlConnection.setHostnameVerifier(replacementVerifier);
        InputStream inStream = urlConnection.getInputStream();
        InputStreamReader in = new InputStreamReader(inStream);

        StringBuilder builder = new StringBuilder();

        int readC;
        while ((readC = in.read()) != -1) {
                builder.append((char) readC);
        }
        return builder.toString();
    }

    private static HostnameVerifier replacementVerifier = new HostnameVerifier() {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true; /* Only gonna make one request */
        }
    };
}
