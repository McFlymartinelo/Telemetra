# Telemetra Relay Server

Serveur relais Node.js/TypeScript pour normaliser et diffuser la télémétrie sim racing.

## Démarrage

```bash
npm install
cp .env.example .env
npm run dev
```

## Structure

```
src/
├── index.ts                    # Point d'entrée, WebSocket + UI config
├── types/UniversalTelemetry.ts # Schéma partagé
└── adapters/
    ├── GameAdapter.ts          # Interface adaptateur
    ├── forza/                  # (à venir) Parser UDP Forza
    ├── lmu/                    # (à venir) Poller REST LMU
    └── ets2/                   # (à venir) Pont ETS2
```
