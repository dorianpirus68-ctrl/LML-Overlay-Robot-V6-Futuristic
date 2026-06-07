package com.lml.overlayrobot;

import android.content.Context;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MemoryStore {

    private static final String FILE_NAME = "lml_robot_memory.jsonl";

    public static void log(Context context, String agent, String action, String detail) {
        try {
            File file = new File(context.getFilesDir(), FILE_NAME);
            JSONObject obj = new JSONObject();
            obj.put("ts", System.currentTimeMillis());
            obj.put("agent", agent);
            obj.put("action", action);
            obj.put("detail", detail);

            BufferedWriter writer = new BufferedWriter(new FileWriter(file, true));
            writer.write(obj.toString());
            writer.newLine();
            writer.close();
        } catch (Exception e) {
            Log.e("MemoryStore", "Log failed", e);
        }
    }
}