# Telemetra

Application de télémétrie sim racing universelle — monorepo contenant le serveur relais PC et l'application Android tablette/téléphone.

## Architecture

```
Telemetra/
├── relay-server/   # Serveur Node.js (TypeScript) sur le PC de jeu
└── android-app/    # Application Android native (Kotlin + Jetpack Compose)
```

Le serveur relais normalise les flux UDP/REST des jeux vers un schéma JSON unique `UniversalTelemetry`, diffusé en WebSocket sur le réseau local. L'app Android s'y connecte et affiche les données en temps réel via deux thèmes interchangeables (Gauge / Cards).

## Prérequis

- **PC de jeu** : Node.js 20+
- **Tablette/téléphone Android** : API 26+ (Android 8.0)
- **Réseau** : PC et tablette sur le même réseau Wi-Fi local

---

## Configuration des jeux

### Forza Horizon / Motorsport — Data Out (UDP)

1. Dans le jeu, ouvrir **Options → HUD and Gameplay → Data Out**
2. Activer **Data Out**
3. Renseigner l'**IP de la tablette** (ou du PC relais si le serveur tourne sur le PC)
4. Port UDP par défaut : `5300` (configurable dans le relay-server)

Le relay-server écoute ce flux UDP et parse le format natif Forza (sled/dash).

### Le Mans Ultimate — API REST locale

LMU expose une API REST sur `http://localhost:6397`. Le relay-server poll cette API à ~20 Hz.

**Prérequis : plugin rFactor2SharedMemoryMapPlugin**

1. Télécharger [rFactor2SharedMemoryMapPlugin](https://github.com/TheIronWolfModding/rF2SharedMemoryMapPlugin) (releases)
2. Copier `rFactor2SharedMemoryMapPlugin64.dll` dans le dossier `Plugins` de LMU :
   ```
   <Steam>/steamapps/common/Le Mans Ultimate/Plugins/
   ```
3. Lancer LMU — le plugin active la mémoire partagée et l'API REST locale
4. Vérifier que `http://localhost:6397/rest/watch` répond (depuis le PC de jeu)

### Euro Truck Simulator 2 (optionnel)

Prévu via un pont type [ets2-telemetry-server](https://github.com/KaterinaLup/ets2-telemetry-server). Adaptateur à brancher dans une phase ultérieure.

---

## relay-server

```bash
cd relay-server
npm install
npm run dev          # développement avec rechargement
npm run build && npm start   # production
```

Interface web de configuration : `http://localhost:3000` (port configurable).

Variables d'environnement (`.env`) :

| Variable | Défaut | Description |
|----------|--------|-------------|
| `WS_PORT` | `8888` | Port WebSocket de diffusion |
| `FORZA_UDP_PORT` | `5300` | Port d'écoute UDP Forza |
| `LMU_POLL_URL` | `http://localhost:6397` | URL API LMU |
| `HTTP_PORT` | `3000` | Port interface web config |

---

## android-app

Ouvrir le dossier `android-app/` dans Android Studio (Ladybug ou plus récent).

### Mode simulateur (développement UI)

Par défaut, l'app démarre en **mode simulateur** : des données aléatoires réalistes sont générées en boucle, sans serveur relais ni jeu lancé. Désactiver dans **Réglages → Mode simulateur**.

### Connexion au relais

1. Lancer le relay-server sur le PC de jeu
2. Noter l'IP locale du PC (ex. `192.168.1.42`)
3. Dans l'app : **Connexion** → saisir l'IP → connecter
4. Le WebSocket se connecte sur le port `8888` par défaut

### Thèmes d'affichage

- **Gauge** : jauges en arc, rapport central géant, colonnes latérales
- **Cards** : grille de cartes teal, barres throttle/brake verticales

Changement à chaud depuis **Réglages**, sans redémarrage.

---

## Schéma UniversalTelemetry

```json
{
  "game": "Forza",
  "sessionType": "Race",
  "track": "Le Mans",
  "timestamp": 1710000000000,
  "speedKmh": 286,
  "rpm": 9008,
  "rpmMax": 9500,
  "gear": 8,
  "throttlePct": 80,
  "brakePct": 10,
  "steeringAngle": 0,
  "fuelPct": 33,
  "lapNumber": 68,
  "lapsLeft": 5,
  "position": 3,
  "lapDelta": -18.279,
  "bestLapTime": "01:26.564",
  "lastLapTime": "01:23.260",
  "tires": [
    { "position": "FL", "tempC": 39, "wearPct": 30 },
    { "position": "FR", "tempC": 36, "wearPct": 90 },
    { "position": "RL", "tempC": 28, "wearPct": 90 },
    { "position": "RR", "tempC": 28, "wearPct": 90 }
  ]
}
```

---

## Roadmap

- [x] Structure monorepo + schéma partagé
- [x] Mode simulateur Android + UI Gauge/Cards
- [x] Écran connexion + réglages
- [ ] Adaptateur Forza UDP (relay-server)
- [ ] Adaptateur LMU REST (relay-server)
- [ ] WebSocket client réel (android-app)
- [ ] Adaptateur ETS2 (optionnel)
