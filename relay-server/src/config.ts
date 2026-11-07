import type { GameId } from '@telemetra/shared';

export interface RelayConfig {
  /** HTTP config UI port */
  httpPort: number;
  /** WebSocket broadcast port */
  wsPort: number;
  /** Active game adapter */
  activeGame: GameId;
  /** Use built-in simulator instead of real game adapters */
  simulatorMode: boolean;
  /** Telemetry broadcast rate in Hz */
  broadcastHz: number;
  /** Forza UDP listen port (future) */
  forzaUdpPort: number;
  /** LMU REST API base URL (future) */
  lmuApiUrl: string;
}

const DEFAULT_CONFIG: RelayConfig = {
  httpPort: Number(process.env.HTTP_PORT) || 3000,
  wsPort: Number(process.env.WS_PORT) || 8888,
  activeGame: (process.env.ACTIVE_GAME as GameId) || 'simulator',
  simulatorMode: process.env.SIMULATOR_MODE !== 'false',
  broadcastHz: Number(process.env.BROADCAST_HZ) || 30,
  forzaUdpPort: Number(process.env.FORZA_UDP_PORT) || 5300,
  lmuApiUrl: process.env.LMU_API_URL || 'http://localhost:6397',
};

let config: RelayConfig = { ...DEFAULT_CONFIG };

export function getConfig(): RelayConfig {
  return { ...config };
}

export function updateConfig(partial: Partial<RelayConfig>): RelayConfig {
  config = { ...config, ...partial };
  return getConfig();
}

export function resetConfig(): RelayConfig {
  config = { ...DEFAULT_CONFIG };
  return getConfig();
}
