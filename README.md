# TELEMETRA

Application de télémétrie sim racing en temps réel — monorepo avec serveur relais PC et dashboard mobile/tablette.

## Architecture

```
telemetra/
├── packages/shared/     # Schéma UniversalTelemetry (TypeScript)
├── relay-server/        # Serveur Node.js (PC de jeu)
└── mobile-app/          # App Expo React Native (téléphone/tablette)
```

Le **relay-server** normalise les données de chaque jeu vers un JSON unique `UniversalTelemetry`, puis les diffuse en WebSocket sur le réseau local. L'**app mobile** se connecte au WebSocket et affiche le dashboard TELEMETRA.

## Prérequis

- Node.js 18+
- npm 9+
- Pour le mobile : [Expo Go](https://expo.dev/go) sur téléphone/tablette **ou** émulateur Android/iOS

## Installation

```bash
cd telemetra
npm install
npm run build:shared
```

## Démarrage rapide (mode simulateur)

### 1. Lancer le serveur relais sur le PC

```bash
npm run relay
```

- Interface de config : http://localhost:3000
- WebSocket : `ws://0.0.0.0:8888`
- Par défaut, le **simulateur** génère des données réalistes à 30 Hz (aucun jeu requis)

### 2. Lancer l'app mobile

```bash
npm run mobile
```

Scannez le QR code avec Expo Go. Sur l'écran de connexion :

1. Entrez l'**IP locale du PC** (ex. `192.168.1.42`)
2. Port WebSocket : `8888`
3. Connectez-vous puis ouvrez le dashboard

> **Astuce** : PC et mobile doivent être sur le même réseau Wi-Fi. Désactivez les pare-feu bloquants sur le port 8888 si besoin.

## Schéma UniversalTelemetry

```typescript
{
  game: 'forza' | 'lmu' | 'ets2' | 'simulator',
  timestamp: number,
  speedKmh: number,
  rpm: number,
  rpmMax: number,
  gear: number,
  throttle: number,    // 0–1
  brake: number,       // 0–1
  steering: number,    // -1 à +1
  fuelLiters?: number,
  fuelPct?: number,
  lapDelta?: number,
  lapNumber?: number,
  bestLapMs?: number,
  lastLapMs?: number,
  position?: number,
  laps?: number,
  lapsLeft?: number,
  tires: [{ position: 'FL'|'FR'|'RL'|'RR', tempC: number, wearPct: number }],
  trackName?: string,
  sessionType?: 'race' | 'practice' | 'qualifying' | 'time_trial' | 'unknown'
}
```

## Configuration Forza (Data Out)

À brancher dans une prochaine étape — voici la procédure :

1. Dans Forza Horizon 5/6 ou Motorsport : **Paramètres → HUD et Gameplay → Data Out**
2. Activer **Data Out**
3. Renseigner l'**IP du PC** qui exécute le relay-server (pas celle du téléphone)
4. Port UDP par défaut : `5300` (format `dash` ou `sled`)
5. Sur le relay-server, désactiver le mode simulateur et sélectionner `forza`

## Configuration Le Mans Ultimate

LMU expose une API REST locale. Prérequis :

### 1. Installer rFactor2SharedMemoryMapPlugin

1. Télécharger [rFactor2SharedMemoryMapPlugin](https://github.com/TheIronWolfModding/rF2SharedMemoryMapPlugin/releases)
2. Copier les fichiers dans le dossier `Plugins` de LMU :
   ```
   <LMU>/Plugins/rFactor2SharedMemoryMapPlugin64.dll
   ```
3. Activer le plugin dans les paramètres du jeu si demandé
4. Redémarrer LMU

### 2. Vérifier l'API REST

Une fois en piste, l'API doit répondre sur :

```
http://localhost:6397/rest/watch/standings
```

Le relay-server poll cette API à ~20 Hz (adaptateur à implémenter).

## Euro Truck Simulator 2 (optionnel)

Utiliser un pont existant type [ets2-telemetry-server](https://github.com/Funbit/ets2-telemetry-server) qui expose des données en HTTP/WebSocket. L'adaptateur ETS2 du relay-server transformera ce flux vers `UniversalTelemetry`.

## Variables d'environnement (relay-server)

| Variable | Défaut | Description |
|----------|--------|-------------|
| `HTTP_PORT` | 3000 | Port interface web |
| `WS_PORT` | 8888 | Port WebSocket |
| `SIMULATOR_MODE` | `true` | Mode simulateur |
| `ACTIVE_GAME` | `simulator` | Jeu actif |
| `BROADCAST_HZ` | 30 | Fréquence de diffusion |
| `FORZA_UDP_PORT` | 5300 | Port UDP Forza |
| `LMU_API_URL` | `http://localhost:6397` | API LMU |

## Dashboard mobile

Deux layouts interchangeables (bouton ⊞ en haut à droite) :

- **Cockpit** : vue tablette avec shift lights, RPM, rapport, vitesse, inputs
- **Grid** : vue compacte type grille (proche de votre 1er screenshot)

Thème sombre navy/teal, optimisé paysage.

## Prochaines étapes

- [ ] Adaptateur Forza UDP (`dash` / `sled`)
- [ ] Adaptateur LMU REST polling
- [ ] Adaptateur ETS2 (pont optionnel)
- [ ] Découverte mDNS (`telemetra.local`)

## Licence

MIT
