export type TirePosition = "FL" | "FR" | "RL" | "RR";

export interface TireTelemetry {
  position: TirePosition;
  tempC: number;
  wearPct: number;
}

export interface UniversalTelemetry {
  game: string;
  sessionType: string;
  track: string;
  timestamp: number;
  speedKmh: number;
  rpm: number;
  rpmMax: number;
  gear: number;
  throttlePct: number;
  brakePct: number;
  steeringAngle: number;
  fuelPct: number;
  lapNumber: number;
  lapsLeft: number;
  position: number;
  lapDelta: number;
  bestLapTime: string;
  lastLapTime: string;
  tires: TireTelemetry[];
}

export const DEFAULT_TELEMETRY: UniversalTelemetry = {
  game: "Forza",
  sessionType: "Race",
  track: "Le Mans",
  timestamp: Date.now(),
  speedKmh: 0,
  rpm: 0,
  rpmMax: 9500,
  gear: 0,
  throttlePct: 0,
  brakePct: 0,
  steeringAngle: 0,
  fuelPct: 100,
  lapNumber: 0,
  lapsLeft: 0,
  position: 0,
  lapDelta: 0,
  bestLapTime: "--:--.---",
  lastLapTime: "--:--.---",
  tires: [
    { position: "FL", tempC: 25, wearPct: 0 },
    { position: "FR", tempC: 25, wearPct: 0 },
    { position: "RL", tempC: 25, wearPct: 0 },
    { position: "RR", tempC: 25, wearPct: 0 },
  ],
};

export function isUniversalTelemetry(value: unknown): value is UniversalTelemetry {
  if (typeof value !== "object" || value === null) return false;
  const t = value as Record<string, unknown>;
  return (
    typeof t.game === "string" &&
    typeof t.speedKmh === "number" &&
    typeof t.rpm === "number" &&
    Array.isArray(t.tires)
  );
}
