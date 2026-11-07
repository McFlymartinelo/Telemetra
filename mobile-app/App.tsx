import React, { useEffect, useState } from 'react';
import { View, StyleSheet } from 'react-native';
import { SafeAreaProvider } from 'react-native-safe-area-context';
import * as ScreenOrientation from 'expo-screen-orientation';
import { useTelemetry } from './src/hooks/useTelemetry';
import { ConnectionScreen } from './src/screens/ConnectionScreen';
import { DashboardScreen } from './src/screens/DashboardScreen';
import { colors } from './src/theme/colors';

type Screen = 'connection' | 'dashboard';

export default function App() {
  const [screen, setScreen] = useState<Screen>('connection');
  const [host, setHost] = useState('192.168.1.42');
  const [port, setPort] = useState(8888);
  const [layout, setLayout] = useState<'cockpit' | 'grid'>('cockpit');

  const {
    connectionState,
    telemetry,
    serverStatus,
    latencyMs,
    lastError,
    connect,
    disconnect,
  } = useTelemetry({ host, port });

  useEffect(() => {
    ScreenOrientation.lockAsync(
      ScreenOrientation.OrientationLock.LANDSCAPE,
    ).catch(() => undefined);
  }, []);

  const connected = connectionState === 'connected';
  const activeGame = telemetry?.game ?? serverStatus?.activeGame;

  return (
    <SafeAreaProvider>
      <View style={styles.root}>
        {screen === 'connection' ? (
          <ConnectionScreen
            host={host}
            port={port}
            onHostChange={setHost}
            onPortChange={setPort}
            connectionState={connectionState}
            latencyMs={latencyMs}
            lastError={lastError}
            activeGame={activeGame}
            onConnect={connect}
            onOpenDashboard={() => setScreen('dashboard')}
          />
        ) : telemetry ? (
          <DashboardScreen
            telemetry={telemetry}
            latencyMs={latencyMs}
            connected={connected}
            layout={layout}
            onToggleLayout={() =>
              setLayout((l) => (l === 'cockpit' ? 'grid' : 'cockpit'))
            }
            onBack={() => {
              setScreen('connection');
              disconnect();
            }}
          />
        ) : (
          <ConnectionScreen
            host={host}
            port={port}
            onHostChange={setHost}
            onPortChange={setPort}
            connectionState={connectionState}
            latencyMs={latencyMs}
            lastError={lastError}
            activeGame={activeGame}
            onConnect={connect}
            onOpenDashboard={() => setScreen('dashboard')}
          />
        )}
      </View>
    </SafeAreaProvider>
  );
}

const styles = StyleSheet.create({
  root: { flex: 1, backgroundColor: colors.background },
});
