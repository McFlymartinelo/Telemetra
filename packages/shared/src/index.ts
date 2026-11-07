/** Supported game identifiers */
export type GameId = 'forza' | 'lmu' | 'ets2' | 'simulator';

export const GAME_LABELS: Record<GameId, string> = {
  forza: 'Forza',
  lmu: 'Le Mans Ultimate',
  ets2: 'Euro Truck Simulator 2',
  simulator: 'Simulateur',
};

/** Tire corner position */
export type TirePosition = 'FL' | 'FR' | 'RL' | 'RR';

export interface TireTelemetry {
  position: TirePosition;
  /** Surface temperature in Celsius */
  tempC: number;
  /** Wear percentage 0–100 */
  wearPct: number;
}

export type SessionType =
  | 'race'
  | 'practice'
  | 'qualifying'
  | 'time_trial'
  | 'unknown';

/**
 * Normalized telemetry payload broadcast by the relay server.
 * All game adapters must produce this shape.
 */
export interface UniversalTelemetry {
  game: GameId;
  /** Unix timestamp in milliseconds */
  timestamp: number;

  speedKmh: number;
  rpm: number;
  rpmMax: number;
  /** 0 = neutral, -1 = reverse, 1+ = forward gears */
  gear: number;

  /** Throttle input 0–1 */
  throttle: number;
  /** Brake input 0–1 */
  brake: number;
  /** Steering -1 (full left) to +1 (full right) */
  steering: number;

  fuelLiters?: number;
  /** Fuel remaining 0–100 % */
  fuelPct?: number;

  /** Lap delta in seconds; negative = faster than reference */
  lapDelta?: number;
  lapNumber?: number;
  bestLapMs?: number;
  lastLapMs?: number;

  position?: number;
  laps?: number;
  lapsLeft?: number;

  tires: TireTelemetry[];

  trackName?: string;
  sessionType?: SessionType;
}

/** WebSocket envelope from relay → clients */
export interface TelemetryMessage {
  type: 'telemetry';
  payload: UniversalTelemetry;
}

export interface ServerStatusMessage {
  type: 'status';
  payload: RelayServerStatus;
}

export type RelayMessage = TelemetryMessage | ServerStatusMessage;

export interface RelayServerStatus {
  activeGame: GameId;
  sourceConnected: boolean;
  clientCount: number;
  wsPort: number;
  uptimeMs: number;
}

/** REST /status response */
export interface RelayStatusResponse extends RelayServerStatus {
  simulatorMode: boolean;
}

export function formatLapTime(ms: number | undefined): string {
  if (ms == null || ms <= 0) return '--:--.---';
  const totalSeconds = ms / 1000;
  const minutes = Math.floor(totalSeconds / 60);
  const seconds = totalSeconds % 60;
  const secStr =
    minutes > 0
      ? seconds.toFixed(3).padStart(6, '0')
      : seconds.toFixed(3).padStart(6, '0');
  return `${String(minutes).padStart(2, '0')}:${secStr}`;
}

export function formatLapDelta(deltaSec: number | undefined): string {
  if (deltaSec == null) return '--.---';
  const sign = deltaSec >= 0 ? '+' : '';
  return `${sign}${deltaSec.toFixed(3)}s`;
}

export function rpmPercent(rpm: number, rpmMax: number): number {
  if (rpmMax <= 0) return 0;
  return Math.min(100, Math.max(0, (rpm / rpmMax) * 100));
}
