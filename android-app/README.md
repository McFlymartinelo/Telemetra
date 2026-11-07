# Telemetra — Android App

Application Android native (Kotlin + Jetpack Compose, Material 3) pour afficher la télémétrie sim racing en temps réel.

## Prérequis

- Android Studio Ladybug (2024.2) ou plus récent
- JDK 17
- SDK Android API 35

## Lancement

1. Ouvrir `android-app/` dans Android Studio
2. Laisser Gradle synchroniser
3. Lancer sur émulateur tablette paysage ou appareil physique

L'app démarre en **mode simulateur** par défaut — des données mockées tournent à ~20 Hz sans serveur relais.

## Architecture

```
app/src/main/java/com/telemetra/app/
├── data/
│   ├── model/          # UniversalTelemetry, AppSettings
│   ├── preferences/    # DataStore (thème, unités, IP relais)
│   └── repository/     # TelemetryRepository (simulateur + WebSocket futur)
├── ui/
│   ├── components/     # Jauges Canvas, cartes données, pneus
│   ├── screens/
│   │   ├── gauge/      # Thème 1 — Gauge
│   │   ├── cards/      # Thème 2 — Cards
│   │   ├── connection/ # Écran connexion WebSocket
│   │   └── settings/   # Réglages (thème, unités, simulateur)
│   ├── navigation/     # NavHost Compose
│   ├── theme/          # Couleurs Telemetra
│   └── viewmodel/      # TelemetraViewModel (StateFlow partagé)
└── MainActivity.kt
```

## Navigation

- **Dashboard** : thème Gauge ou Cards selon réglages
- **Réglages** (icône engrenage) : switch thème, km/h ↔ mph, simulateur
- **Connexion** : IP/port du relay-server

## Prochaines étapes

- [ ] Client WebSocket OkHttp branché sur le relay-server
- [ ] Reconnexion automatique
- [ ] Mode portrait responsive téléphone
