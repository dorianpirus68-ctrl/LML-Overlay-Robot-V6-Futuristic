package com.lml.overlayrobot;

public class ActionVerifier {

    public static boolean verifyAction(ScreenNode node, RobotMode mode) {
        if (node == null) return false;
        if (mode == RobotMode.POINTAGE || mode == RobotMode.SIMULATION) return false;
        return SafetyGuard.canAutoClick(node.text, node.contentDescription, mode);
    }
}