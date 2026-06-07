package com.lml.overlayrobot;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActionRecorder {

    private static final String PREFS = "lml_recorder";
    private static final String KEY = "recorded_steps";

    private final Context context;
    private final List<ActionStep> steps = new ArrayList<>();
    private boolean recording = false;

    public ActionRecorder(Context context) {
        this.context = context;
        load();
    }

    public void startRecording() {
        recording = true;
        steps.clear();
    }

    public void stopRecording() {
        recording = false;
        save();
    }

    public boolean isRecording() {
        return recording;
    }

    public void recordClick(float x, float y, String text, String desc, String className, String pkg, android.graphics.Rect bounds) {
        if (!recording) return;
        ActionStep step = new ActionStep(x, y, text, desc, className, pkg, bounds);
        steps.add(step);
        MemoryStore.log(context, "Recorder", "RECORD_CLICK", text != null ? text : "no_text");
    }

    public List<ActionStep> getSteps() {
        return new ArrayList<>(steps);
    }

    public void clear() {
        steps.clear();
        save();
    }

    private void save() {
        try {
            JSONArray array = new JSONArray();
            for (ActionStep s : steps) {
                JSONObject o = new JSONObject();
                o.put("x", s.x);
                o.put("y", s.y);
                o.put("text", s.targetText);
                o.put("desc", s.targetDesc);
                o.put("class", s.className);
                o.put("pkg", s.packageName);
                array.put(o);
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY, array.toString()).apply();
        } catch (Exception e) {
            Log.e("ActionRecorder", "Save failed", e);
        }
    }

    private void load() {
        try {
            SharedPreferences prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY, "[]");
            JSONArray array = new JSONArray(json);
            steps.clear();
            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                ActionStep s = new ActionStep();
                s.x = (float) o.optDouble("x");
                s.y = (float) o.optDouble("y");
                s.targetText = o.optString("text");
                s.targetDesc = o.optString("desc");
                s.className = o.optString("class");
                s.packageName = o.optString("pkg");
                steps.add(s);
            }
        } catch (Exception e) {
            Log.e("ActionRecorder", "Load failed", e);
        }
    }
}