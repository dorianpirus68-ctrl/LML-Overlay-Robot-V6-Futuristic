package com.lml.overlayrobot;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class BrainCore {

    private final AgentSwarm swarm = new AgentSwarm();
    private final Mission mission = new Mission();

    public void showBrainUI(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 30, 40, 30);

        TextView title = new TextView(context);
        title.setText("LML BRAIN CORE v6");
        title.setTextSize(22);
        title.setTextColor(0xFF00E5FF);
        layout.addView(title);

        List<Agent> agents = swarm.getAgents();
        for (Agent a : agents) {
            TextView tv = new TextView(context);
            tv.setText(a.name + "  •  " + a.status);
            tv.setTextSize(18);
            tv.setTextColor(0xFFAAAAAA);
            tv.setPadding(0, 12, 0, 12);
            layout.addView(tv);
        }

        new AlertDialog.Builder(context)
            .setView(layout)
            .setPositiveButton("CLOSE", null)
            .show();
    }

    public AgentSwarm getSwarm() {
        return swarm;
    }

    public Mission getMission() {
        return mission;
    }
}