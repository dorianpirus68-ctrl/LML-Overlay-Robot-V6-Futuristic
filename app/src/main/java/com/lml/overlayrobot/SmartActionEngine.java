package com.lml.overlayrobot;

import android.content.Context;
import android.util.Log;

import java.util.List;

public class SmartActionEngine {

    private final Context context;
    private final ActionRecorder recorder;
    private final BrainCore brain;
    private RobotMode currentMode = RobotMode.POINTAGE;

    public SmartActionEngine(Context context, ActionRecorder recorder, BrainCore brain) {
        this.context = context;
        this.recorder = recorder;
        this.brain = brain;
    }

    public void setMode(RobotMode mode) {
        this.currentMode = mode;
        MemoryStore.log(context, "Engine", "MODE_CHANGE", mode.name());
    }

    public RobotMode getMode() {
        return currentMode;
    }

    public void executePointage(List<ActionStep> steps, RobotCursorView robot) {
        brain.getSwarm().pulseAgent("Action", "POINTAGE");
        for (ActionStep step : steps) {
            if (robot != null) robot.moveTo(step.x, step.y, "POINT " + (step.targetText != null ? step.targetText : ""));
            try { Thread.sleep(280); } catch (Exception ignored) {}
        }
    }

    public void executeSimulation(List<ActionStep> steps, RobotCursorView robot, ScreenMapOverlayView map) {
        brain.getSwarm().pulseAgent("Vision", "SIMULATION");
        for (ActionStep step : steps) {
            if (robot != null) robot.moveTo(step.x, step.y, "SIM " + (step.targetText != null ? step.targetText : ""));
            if (map != null) map.invalidate();
            try { Thread.sleep(320); } catch (Exception ignored) {}
        }
    }

    public void executeAutoSafe(List<ActionStep> steps, LmlAccessibilityService accessibility, RobotCursorView robot) {
        brain.getSwarm().pulseAgent("Safety", "AUTO_SAFE");
        for (ActionStep step : steps) {
            if (robot != null) robot.moveTo(step.x, step.y, "SAFE " + (step.targetText != null ? step.targetText : ""));
            if (SafetyGuard.canAutoClick(step.targetText, step.targetDesc, currentMode)) {
                if (accessibility != null) {
                    accessibility.performClick(step.x, step.y);
                }
            } else {
                MemoryStore.log(context, "Safety", "BLOCKED", step.targetText != null ? step.targetText : "sensitive");
            }
            try { Thread.sleep(450); } catch (Exception ignored) {}
        }
    }

    public void executeAssisted(List<ActionStep> steps, RobotCursorView robot) {
        brain.getSwarm().pulseAgent("Action", "ASSISTED");
        for (ActionStep step : steps) {
            if (robot != null) robot.moveTo(step.x, step.y, "WAIT VALIDATE");
            try { Thread.sleep(600); } catch (Exception ignored) {}
        }
    }
}