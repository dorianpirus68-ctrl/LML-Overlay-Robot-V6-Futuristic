package com.lml.overlayrobot;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class ActionRecorder {
    private Context context;
    private SharedPreferences prefs;
    private List<ActionStep> recordedActions;
    private boolean recording;

    public ActionRecorder(Context context) {
        this.context = context;
        this.prefs = context.getSharedPreferences("lml_actions", Context.MODE_PRIVATE);
        this.recordedActions = new ArrayList<>();
        this.recording = false;
        loadActions();
    }

    public void startRecording() {
        recording = true;
        recordedActions.clear();
        MemoryStore.getInstance().log("rec_start", "Enregistrement démarré");
    }

    public void stopRecording() {
        recording = false;
        saveActions();
        MemoryStore.getInstance().log("rec_stop", "Enregistrement arrêté (" + recordedActions.size() + " actions)");
    }

    public void addAction(ActionStep step) {
        if (recording && step != null) {
            recordedActions.add(step);
        }
    }

    public void saveActions() {
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < recordedActions.size(); i++) {
            editor.putString("action_" + i, recordedActions.get(i).toLine());
        }
        editor.putInt("action_count", recordedActions.size());
        editor.apply();
    }

    public void loadActions() {
        recordedActions.clear();
        int count = prefs.getInt("action_count", 0);
        for (int i = 0; i < count; i++) {
            String line = prefs.getString("action_" + i, null);
            if (line != null) {
                ActionStep step = ActionStep.fromLine(line);
                if (step != null) {
                    recordedActions.add(step);
                }
            }
        }
    }

    public void clear() {
        recordedActions.clear();
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
        MemoryStore.getInstance().log("clear", "Actions effacées");
    }

    public List<ActionStep> getActions() {
        return new ArrayList<>(recordedActions);
    }

    public int getCount() {
        return recordedActions.size();
    }

    public boolean isRecording() {
        return recording;
    }
}
