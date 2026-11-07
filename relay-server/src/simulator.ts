import type { GameId, TireTelemetry, UniversalTelemetry } from '@telemetra/shared';

const TIRE_POSITIONS: TireTelemetry['position'][] = ['FL', 'FR', 'RL', 'RR'];

function clamp(value: number, min: number, max: number): number {
  return Math.min(max, Math.max(min, value));
}

/**
 * Generates realistic oscillating telemetry for UI development
 * without a game running.
 */
export class TelemetrySimulator {
  private t = 0;
  private lapPhase = 0;
  private readonly rpmMax = 9500;

  constructor(private readonly game: GameId = 'simulator') {}

  tick(dtMs: number): UniversalTelemetry {
    this.t += dtMs / 1000;
    this.lapPhase += dtMs / 85000; // ~85s lap

    const lapSin = Math.sin(this.lapPhase * Math.PI * 2);
    const corner = Math.sin(this.t * 0.7);
    const straight = Math.cos(this.t * 0.35);

    const throttle = clamp(0.55 + straight * 0.35 + Math.random() * 0.05, 0, 1);
    const brake = clamp(Math.max(0, -corner * 0.9 - 0.1) + Math.random() * 0.03, 0, 1);
    const steering = clamp(corner * 0.85, -1, 1);

    const rpmBase = 5500 + straight * 3200 + throttle * 800;
    const rpm = clamp(rpmBase + Math.random() * 120, 1200, this.rpmMax);

    const speedKmh = clamp(80 + straight * 180 + throttle * 40 - brake * 60, 0, 320);
    const gear = speedKmh < 5 ? 0 : clamp(Math.round(speedKmh / 38), 1, 8);

    const lapDelta = -18.279 + lapSin * 0.4 + Math.random() * 0.05;
    const fuelPct = clamp(33 - this.t * 0.02, 5, 100);

    const tires: TireTelemetry[] = TIRE_POSITIONS.map((position, i) => {
      const baseTemp = [39, 36, 28, 28][i];
      const baseWear = [30, 90, 90, 90][i];
      return {
        position,
        tempC: Math.round(baseTemp + Math.sin(this.t + i) * 3 + throttle * 4),
        wearPct: clamp(baseWear + this.t * 0.01, 0, 100),
      };
    });

    return {
      game: this.game,
      timestamp: Date.now(),
      speedKmh: Math.round(speedKmh),
      rpm: Math.round(rpm),
      rpmMax: this.rpmMax,
      gear,
      throttle,
      brake,
      steering,
      fuelPct: Math.round(fuelPct),
      fuelLiters: Math.round(fuelPct * 0.45),
      lapDelta,
      lapNumber: 1 + Math.floor(this.lapPhase),
      bestLapMs: 86564,
      lastLapMs: 83260,
      position: 3,
      laps: 67,
      lapsLeft: 5,
      tires,
      trackName: 'Le Mans',
      sessionType: 'race',
    };
  }
}
