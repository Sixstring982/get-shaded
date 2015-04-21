package com.lunagameserve.get_shaded.json;

import android.util.JsonWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.zip.GZIPOutputStream;

/**
 * Created by sixstring982 on 4/20/15.
 */
public abstract class JSONSerializable {

    public abstract void writeJson(JsonWriter writer) throws IOException;

    public void write(File file) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        GZIPOutputStream gz = new GZIPOutputStream(out);
        OutputStreamWriter w = new OutputStreamWriter(gz);
        JsonWriter writer = new JsonWriter(w);

        writeJson(writer);

        w.close();
    }
}
