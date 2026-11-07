import { createHttpServer } from './http';
import { getConfig } from './config';
import { TelemetrySimulator } from './simulator';
import { TelemetryBroadcaster } from './websocket';

const config = getConfig();
const broadcaster = new TelemetryBroadcaster(config.wsPort);
const app = createHttpServer(broadcaster);

let simulator: TelemetrySimulator | null = null;
let interval: ReturnType<typeof setInterval> | null = null;
let lastTick = Date.now();

function startTelemetryLoop(): void {
  const cfg = getConfig();
  const intervalMs = Math.round(1000 / cfg.broadcastHz);

  if (interval) {
    clearInterval(interval);
    interval = null;
  }

  if (cfg.simulatorMode || cfg.activeGame === 'simulator') {
    simulator = new TelemetrySimulator(cfg.activeGame === 'simulator' ? 'forza' : cfg.activeGame);
    console.log(`[sim] Simulator active (${cfg.broadcastHz} Hz)`);
  } else {
    simulator = null;
    console.log(`[adapter] ${cfg.activeGame} adapter not yet implemented — enable simulator mode`);
  }

  lastTick = Date.now();
  interval = setInterval(() => {
    const now = Date.now();
    const dt = now - lastTick;
    lastTick = now;

    const currentCfg = getConfig();
    if (!simulator) return;

    const payload = simulator.tick(dt);
    broadcaster.broadcast({ type: 'telemetry', payload });

    if (now % 2000 < intervalMs) {
      broadcaster.broadcast({
        type: 'status',
        payload: broadcaster.buildStatus(
          currentCfg.activeGame,
          currentCfg.simulatorMode,
          currentCfg.wsPort,
        ),
      });
    }
  }, intervalMs);
}

broadcaster.start();
startTelemetryLoop();

app.listen(config.httpPort, '0.0.0.0', () => {
  console.log('');
  console.log('  TELEMETRA Relay Server');
  console.log('  ─────────────────────');
  console.log(`  Config UI : http://localhost:${config.httpPort}`);
  console.log(`  WebSocket : ws://0.0.0.0:${config.wsPort}`);
  console.log(`  Mode      : ${config.simulatorMode ? 'simulateur' : config.activeGame}`);
  console.log('');
});

process.on('SIGINT', () => {
  if (interval) clearInterval(interval);
  broadcaster.stop();
  process.exit(0);
});
