import { useCallback, useEffect, useRef, useState } from 'react';
import type {
  RelayMessage,
  RelayServerStatus,
  UniversalTelemetry,
} from '@telemetra/shared';

export type ConnectionState =
  | 'disconnected'
  | 'connecting'
  | 'connected'
  | 'error';

interface UseTelemetryOptions {
  host: string;
  port: number;
  autoReconnect?: boolean;
  reconnectDelayMs?: number;
}

interface UseTelemetryResult {
  connectionState: ConnectionState;
  telemetry: UniversalTelemetry | null;
  serverStatus: RelayServerStatus | null;
  latencyMs: number | null;
  lastError: string | null;
  connect: () => void;
  disconnect: () => void;
}

export function useTelemetry({
  host,
  port,
  autoReconnect = true,
  reconnectDelayMs = 2000,
}: UseTelemetryOptions): UseTelemetryResult {
  const [connectionState, setConnectionState] =
    useState<ConnectionState>('disconnected');
  const [telemetry, setTelemetry] = useState<UniversalTelemetry | null>(null);
  const [serverStatus, setServerStatus] = useState<RelayServerStatus | null>(
    null,
  );
  const [latencyMs, setLatencyMs] = useState<number | null>(null);
  const [lastError, setLastError] = useState<string | null>(null);

  const wsRef = useRef<WebSocket | null>(null);
  const reconnectTimer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const shouldReconnect = useRef(autoReconnect);
  const lastMessageAt = useRef<number | null>(null);

  const clearReconnectTimer = () => {
    if (reconnectTimer.current) {
      clearTimeout(reconnectTimer.current);
      reconnectTimer.current = null;
    }
  };

  const disconnect = useCallback(() => {
    shouldReconnect.current = false;
    clearReconnectTimer();
    wsRef.current?.close();
    wsRef.current = null;
    setConnectionState('disconnected');
  }, []);

  const connect = useCallback(() => {
    if (!host.trim()) {
      setLastError('Adresse IP requise');
      setConnectionState('error');
      return;
    }

    clearReconnectTimer();
    shouldReconnect.current = autoReconnect;
    wsRef.current?.close();

    const url = `ws://${host.trim()}:${port}`;
    setConnectionState('connecting');
    setLastError(null);

    try {
      const ws = new WebSocket(url);
      wsRef.current = ws;

      ws.onopen = () => {
        setConnectionState('connected');
        setLastError(null);
      };

      ws.onmessage = (event) => {
        const now = Date.now();
        if (lastMessageAt.current != null) {
          setLatencyMs(now - lastMessageAt.current);
        }
        lastMessageAt.current = now;

        try {
          const msg = JSON.parse(String(event.data)) as RelayMessage;
          if (msg.type === 'telemetry') {
            setTelemetry(msg.payload);
          } else if (msg.type === 'status') {
            setServerStatus(msg.payload);
          }
        } catch {
          // ignore malformed frames
        }
      };

      ws.onerror = () => {
        setLastError('Erreur de connexion WebSocket');
        setConnectionState('error');
      };

      ws.onclose = () => {
        wsRef.current = null;
        setConnectionState('disconnected');
        if (shouldReconnect.current) {
          reconnectTimer.current = setTimeout(() => {
            connect();
          }, reconnectDelayMs);
        }
      };
    } catch (err) {
      setLastError(err instanceof Error ? err.message : 'Connexion impossible');
      setConnectionState('error');
    }
  }, [host, port, autoReconnect, reconnectDelayMs]);

  useEffect(() => {
    return () => {
      shouldReconnect.current = false;
      clearReconnectTimer();
      wsRef.current?.close();
    };
  }, []);

  return {
    connectionState,
    telemetry,
    serverStatus,
    latencyMs,
    lastError,
    connect,
    disconnect,
  };
}
