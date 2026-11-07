import type { UniversalTelemetry } from "../types/UniversalTelemetry.js";

/**
 * Game adapter interface — each game source normalizes to UniversalTelemetry.
 * Implementations: Forza UDP, LMU REST, ETS2 bridge (future).
 */
export interface GameAdapter {
  readonly name: string;
  start(): Promise<void>;
  stop(): Promise<void>;
  getLatest(): UniversalTelemetry | null;
  isConnected(): boolean;
}
