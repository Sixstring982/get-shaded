package com.lunagameserve.get_shaded.settings;

import android.util.JsonReader;
import android.util.JsonWriter;
import org.json.JSONException;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Sixstring982
 * @since 4/20/2015
 */
public class Settings {

    private boolean renderGrid = false;
    private boolean renderPoints = false;

    private final File baseDir;

    public Settings(File baseDir) {

        this.baseDir = baseDir;

        try {
            loadSettings(baseDir);
        } catch (IOException e) {
            createNewSettings();
        } catch (JSONException e) {
            createNewSettings();
        }

    }

    private void loadSettings(File baseDir) throws IOException, JSONException {
        File settings = new File(baseDir, "settings.json");
        if (settings.exists()) {
            InputStream in = new FileInputStream(settings);
            GZIPInputStream gz = new GZIPInputStream(in);
            JsonReader reader = new JsonReader(new InputStreamReader(gz));

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("renderGrid")) {
                    renderGrid = reader.nextBoolean();
                } else if (name.equals("renderPoints")) {
                    renderPoints = reader.nextBoolean();
                }
            }

            reader.close();
        } else {
            createNewSettings();
        }
    }

    public void save() throws IOException, JSONException {
        File settingsFile = new File(baseDir, "settings.json");
        OutputStream out = new FileOutputStream(settingsFile);
        GZIPOutputStream gz = new GZIPOutputStream(out);
        OutputStreamWriter writer = new OutputStreamWriter(gz);
        JsonWriter jwriter = new JsonWriter(writer);

        jwriter.beginObject();

        jwriter.name("renderGrid").value(renderGrid);
        jwriter.name("renderPoints").value(renderPoints);

        jwriter.endObject();

        writer.close();
    }

    private void createNewSettings() {
        /* The defaults work fine! */
    }

    public boolean isRenderGrid() {
        return renderGrid;
    }

    public void setRenderGrid(boolean renderGrid) {
        this.renderGrid = renderGrid;
    }

    public boolean isRenderPoints() {
        return renderPoints;
    }

    public void setRenderPoints(boolean renderPoints) {
        this.renderPoints = renderPoints;
    }
}
