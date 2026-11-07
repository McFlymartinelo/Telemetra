import express from 'express';
import path from 'path';
import type { GameId, RelayStatusResponse } from '@telemetra/shared';
import { GAME_LABELS } from '@telemetra/shared';
import { getConfig, updateConfig } from './config';
import type { TelemetryBroadcaster } from './websocket';

export function createHttpServer(broadcaster: TelemetryBroadcaster) {
  const app = express();
  app.use(express.json());
  app.use(express.static(path.join(__dirname, '..', 'public')));

  app.get('/api/status', (_req, res) => {
    const cfg = getConfig();
    const status: RelayStatusResponse = {
      ...broadcaster.buildStatus(cfg.activeGame, cfg.simulatorMode, cfg.wsPort),
      simulatorMode: cfg.simulatorMode,
    };
    res.json(status);
  });

  app.get('/api/config', (_req, res) => {
    res.json(getConfig());
  });

  app.post('/api/config', (req, res) => {
    const body = req.body as Partial<{
      activeGame: GameId;
      simulatorMode: boolean;
      wsPort: number;
      forzaUdpPort: number;
    }>;

    const allowedGames: GameId[] = ['forza', 'lmu', 'ets2', 'simulator'];
    const partial: Parameters<typeof updateConfig>[0] = {};

    if (body.activeGame && allowedGames.includes(body.activeGame)) {
      partial.activeGame = body.activeGame;
      if (body.activeGame === 'simulator') {
        partial.simulatorMode = true;
      }
    }
    if (typeof body.simulatorMode === 'boolean') {
      partial.simulatorMode = body.simulatorMode;
    }
    if (typeof body.wsPort === 'number') {
      partial.wsPort = body.wsPort;
    }
    if (typeof body.forzaUdpPort === 'number') {
      partial.forzaUdpPort = body.forzaUdpPort;
    }

    const updated = updateConfig(partial);
    res.json({
      config: updated,
      games: GAME_LABELS,
      note: 'Redémarrez le serveur si vous changez le port WebSocket.',
    });
  });

  return app;
}
