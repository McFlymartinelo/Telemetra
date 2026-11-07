import express from "express";
import { WebSocketServer, WebSocket } from "ws";
import type { UniversalTelemetry } from "./types/UniversalTelemetry.js";
import { DEFAULT_TELEMETRY } from "./types/UniversalTelemetry.js";

const WS_PORT = Number(process.env.WS_PORT ?? 8888);
const HTTP_PORT = Number(process.env.HTTP_PORT ?? 3000);

let activeGame = "none";
let latestTelemetry: UniversalTelemetry = { ...DEFAULT_TELEMETRY };
const clients = new Set<WebSocket>();

function broadcast(telemetry: UniversalTelemetry): void {
  const payload = JSON.stringify(telemetry);
  for (const client of clients) {
    if (client.readyState === WebSocket.OPEN) {
      client.send(payload);
    }
  }
}

export function updateTelemetry(telemetry: UniversalTelemetry): void {
  latestTelemetry = telemetry;
  broadcast(telemetry);
}

// WebSocket server
const wss = new WebSocketServer({ port: WS_PORT });
wss.on("connection", (ws) => {
  clients.add(ws);
  ws.send(JSON.stringify(latestTelemetry));
  ws.on("close", () => clients.delete(ws));
  ws.on("error", () => clients.delete(ws));
});

// Minimal config web UI
const app = express();
app.use(express.json());

app.get("/api/status", (_req, res) => {
  res.json({
    activeGame,
    wsPort: WS_PORT,
    connectedClients: clients.size,
    latestTimestamp: latestTelemetry.timestamp,
  });
});

app.get("/api/telemetry", (_req, res) => {
  res.json(latestTelemetry);
});

app.post("/api/game", (req, res) => {
  const { game } = req.body as { game?: string };
  if (game) activeGame = game;
  res.json({ activeGame });
});

app.get("/", (_req, res) => {
  res.send(`<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8">
  <title>Telemetra Relay</title>
  <style>
    body { font-family: system-ui; background: #0d1b2e; color: #fff; padding: 2rem; }
    h1 { color: #2dd4bf; }
    .status { background: #1a2a40; padding: 1rem; border-radius: 8px; margin: 1rem 0; }
    select, button { padding: 0.5rem 1rem; border-radius: 4px; border: none; margin: 0.25rem; }
    button { background: #2dd4bf; color: #0d1b2e; cursor: pointer; font-weight: bold; }
  </style>
</head>
<body>
  <h1>Telemetra Relay Server</h1>
  <div class="status" id="status">Chargement...</div>
  <label>Jeu actif :
    <select id="game">
      <option value="none">Aucun</option>
      <option value="forza">Forza</option>
      <option value="lmu">Le Mans Ultimate</option>
      <option value="ets2">ETS2</option>
    </select>
  </label>
  <button onclick="setGame()">Appliquer</button>
  <script>
    async function refresh() {
      const r = await fetch('/api/status');
      const s = await r.json();
      document.getElementById('status').innerHTML =
        'Jeu: <b>' + s.activeGame + '</b> | WS port: <b>' + s.wsPort +
        '</b> | Clients: <b>' + s.connectedClients + '</b>';
      document.getElementById('game').value = s.activeGame;
    }
    async function setGame() {
      const game = document.getElementById('game').value;
      await fetch('/api/game', { method: 'POST', headers: {'Content-Type':'application/json'}, body: JSON.stringify({game}) });
      refresh();
    }
    refresh();
    setInterval(refresh, 2000);
  </script>
</body>
</html>`);
});

app.listen(HTTP_PORT, () => {
  console.log(`[Telemetra] HTTP config UI  → http://localhost:${HTTP_PORT}`);
  console.log(`[Telemetra] WebSocket relay → ws://0.0.0.0:${WS_PORT}`);
  console.log(`[Telemetra] Game adapters not yet connected — scaffold only`);
});
