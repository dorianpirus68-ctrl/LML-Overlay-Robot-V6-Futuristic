package com.lml.overlayrobot;

import java.util.ArrayList;
import java.util.List;

public class AgentSwarm {

    public final List<Agent> agents = new ArrayList<>();

    public AgentSwarm() {
        agents.add(new Agent("Planner"));
        agents.add(new Agent("Vision"));
        agents.add(new Agent("Memory"));
        agents.add(new Agent("Action"));
        agents.add(new Agent("Safety"));
        agents.add(new Agent("Critic"));
        agents.add(new Agent("Replay"));
        agents.add(new Agent("Recalibrator"));
        agents.add(new Agent("MissionBrain"));
        agents.add(new Agent("Theme"));
    }

    public void pulseAgent(String name, String status) {
        for (Agent a : agents) {
            if (a.name.equals(name)) {
                a.setStatus(status);
                return;
            }
        }
    }

    public List<Agent> getAgents() {
        return agents;
    }
}