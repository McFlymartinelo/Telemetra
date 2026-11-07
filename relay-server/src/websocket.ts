import { WebSocketServer, WebSocket } from 'ws';
import type { RelayMessage, RelayServerStatus } from '@telemetra/shared';

export class TelemetryBroadcaster {
  private wss: WebSocketServer | null = null;
  private readonly clients = new Set<WebSocket>();
  private readonly startTime = Date.now();

  constructor(private readonly port: number) {}

  start(): void {
    this.wss = new WebSocketServer({ port: this.port, host: '0.0.0.0' });

    this.wss.on('connection', (ws) => {
      this.clients.add(ws);
      ws.on('close', () => this.clients.delete(ws));
      ws.on('error', () => this.clients.delete(ws));
    });

    this.wss.on('listening', () => {
      console.log(`[ws] Broadcasting on ws://0.0.0.0:${this.port}`);
    });
  }

  getClientCount(): number {
    return this.clients.size;
  }

  broadcast(message: RelayMessage): void {
    const data = JSON.stringify(message);
    for (const client of this.clients) {
      if (client.readyState === WebSocket.OPEN) {
        client.send(data);
      }
    }
  }

  buildStatus(
    activeGame: RelayServerStatus['activeGame'],
    sourceConnected: boolean,
    wsPort: number,
  ): RelayServerStatus {
    return {
      activeGame,
      sourceConnected,
      clientCount: this.getClientCount(),
      wsPort,
      uptimeMs: Date.now() - this.startTime,
    };
  }

  stop(): void {
    for (const client of this.clients) {
      client.close();
    }
    this.clients.clear();
    this.wss?.close();
    this.wss = null;
  }
}
