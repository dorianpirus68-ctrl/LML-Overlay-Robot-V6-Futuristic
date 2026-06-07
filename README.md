# LML-Overlay-Robot-V6-Futuristic

Futuristic Android overlay robot with accessibility scanning, smart action engine, safety guard, brain swarm simulation and holographic robot cursor.

## Features
- Fully native Java Android (no Kotlin, no Compose, no Firebase)
- Floating holographic robot with Canvas drawing (neon eyes, brain, halo, particles, color pulse)
- Full screen accessibility scanner + neon map overlay
- Action recording / replay with SmartRecalibrator
- Multiple modes: POINTAGE, SIMULATION, AUTO SAFE, ASSISTED
- Strong SafetyGuard blocking any financial / destructive action in AUTO SAFE
- Internal AgentSwarm (Planner, Vision, Memory, Action, Safety, Critic, Replay, Recalibrator, MissionBrain, Theme)
- MemoryStore JSONL logging
- GitHub Actions that builds APK

## Build
```bash
./gradlew assembleDebug --no-daemon
```

## Usage
1. Install APK
2. Open app → grant **Overlay** permission
3. Open **Accessibility** settings → enable **LML Overlay Robot**
4. Tap **LAUNCH ROBOT**
5. Use the floating panel to control the robot

## Safety
AUTO SAFE mode **never** clicks on sensitive words (acheter, buy, password, bank, etc.).  
POINTAGE and SIMULATION modes never perform real clicks.

## Author
LML Systems - V6 Futuristic