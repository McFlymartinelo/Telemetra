import React, { useEffect, useState } from 'react';
import {
  View,
  Text,
  TextInput,
  TouchableOpacity,
  StyleSheet,
  ActivityIndicator,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { GAME_LABELS } from '@telemetra/shared';
import type { ConnectionState } from '../hooks/useTelemetry';
import { colors } from '../theme/colors';

const STORAGE_HOST = '@telemetra/host';
const STORAGE_PORT = '@telemetra/port';

interface ConnectionScreenProps {
  host: string;
  port: number;
  onHostChange: (v: string) => void;
  onPortChange: (v: number) => void;
  connectionState: ConnectionState;
  latencyMs: number | null;
  lastError: string | null;
  activeGame?: string;
  onConnect: () => void;
  onOpenDashboard: () => void;
}

export function ConnectionScreen({
  host,
  port,
  onHostChange,
  onPortChange,
  connectionState,
  latencyMs,
  lastError,
  activeGame,
  onConnect,
  onOpenDashboard,
}: ConnectionScreenProps) {
  const [portText, setPortText] = useState(String(port));

  useEffect(() => {
    (async () => {
      const savedHost = await AsyncStorage.getItem(STORAGE_HOST);
      const savedPort = await AsyncStorage.getItem(STORAGE_PORT);
      if (savedHost) onHostChange(savedHost);
      if (savedPort) {
        const p = Number(savedPort);
        if (!Number.isNaN(p)) {
          onPortChange(p);
          setPortText(savedPort);
        }
      }
    })();
  }, [onHostChange, onPortChange]);

  const saveAndConnect = async () => {
    await AsyncStorage.setItem(STORAGE_HOST, host);
    await AsyncStorage.setItem(STORAGE_PORT, String(port));
    onConnect();
  };

  const statusColor =
    connectionState === 'connected'
      ? colors.green
      : connectionState === 'connecting'
        ? colors.amber
        : connectionState === 'error'
          ? colors.red
          : colors.textMuted;

  const statusLabel: Record<ConnectionState, string> = {
    disconnected: 'Déconnecté',
    connecting: 'Connexion…',
    connected: 'Connecté',
    error: 'Erreur',
  };

  return (
    <View style={styles.container}>
      <Text style={styles.brand}>TELEMETRA</Text>
      <Text style={styles.subtitle}>Télémétrie sim racing</Text>

      <View style={styles.card}>
        <Text style={styles.label}>IP du PC relais</Text>
        <TextInput
          style={styles.input}
          value={host}
          onChangeText={onHostChange}
          placeholder="192.168.1.42"
          placeholderTextColor={colors.textMuted}
          autoCapitalize="none"
          autoCorrect={false}
          keyboardType="decimal-pad"
        />

        <Text style={styles.label}>Port WebSocket</Text>
        <TextInput
          style={styles.input}
          value={portText}
          onChangeText={(t) => {
            setPortText(t);
            const n = Number(t);
            if (!Number.isNaN(n)) onPortChange(n);
          }}
          placeholder="8888"
          placeholderTextColor={colors.textMuted}
          keyboardType="number-pad"
        />

        <View style={styles.statusRow}>
          <View style={[styles.dot, { backgroundColor: statusColor }]} />
          <Text style={styles.statusText}>{statusLabel[connectionState]}</Text>
          {connectionState === 'connecting' && (
            <ActivityIndicator size="small" color={colors.teal} style={{ marginLeft: 8 }} />
          )}
        </View>

        {activeGame && (
          <Text style={styles.meta}>
            Jeu : {GAME_LABELS[activeGame as keyof typeof GAME_LABELS] ?? activeGame}
          </Text>
        )}
        {latencyMs != null && connectionState === 'connected' && (
          <Text style={styles.meta}>Latence : ~{latencyMs} ms</Text>
        )}
        {lastError && <Text style={styles.error}>{lastError}</Text>}

        <TouchableOpacity style={styles.btn} onPress={saveAndConnect}>
          <Text style={styles.btnText}>
            {connectionState === 'connected' ? 'Reconnecter' : 'Se connecter'}
          </Text>
        </TouchableOpacity>

        {connectionState === 'connected' && (
          <TouchableOpacity style={styles.btnSecondary} onPress={onOpenDashboard}>
            <Text style={styles.btnSecondaryText}>Ouvrir le dashboard</Text>
          </TouchableOpacity>
        )}
      </View>

      <Text style={styles.hint}>
        Lancez le relay-server sur votre PC : npm run relay
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
    justifyContent: 'center',
    alignItems: 'center',
    padding: 24,
  },
  brand: {
    color: colors.teal,
    fontSize: 32,
    fontWeight: '800',
    letterSpacing: 4,
  },
  subtitle: { color: colors.textMuted, marginBottom: 24 },
  card: {
    width: '100%',
    maxWidth: 400,
    backgroundColor: colors.panel,
    borderWidth: 1,
    borderColor: colors.panelBorder,
    borderRadius: 8,
    padding: 20,
  },
  label: { color: colors.teal, fontSize: 12, fontWeight: '700', marginBottom: 6, marginTop: 8 },
  input: {
    backgroundColor: '#0d1525',
    borderWidth: 1,
    borderColor: colors.panelBorder,
    borderRadius: 6,
    color: colors.text,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
  },
  statusRow: { flexDirection: 'row', alignItems: 'center', marginTop: 16 },
  dot: { width: 10, height: 10, borderRadius: 5, marginRight: 8 },
  statusText: { color: colors.text, fontWeight: '600' },
  meta: { color: colors.textMuted, fontSize: 13, marginTop: 6 },
  error: { color: colors.red, marginTop: 8, fontSize: 13 },
  btn: {
    backgroundColor: colors.teal,
    borderRadius: 6,
    paddingVertical: 12,
    marginTop: 20,
    alignItems: 'center',
  },
  btnText: { color: '#041018', fontWeight: '800', fontSize: 15 },
  btnSecondary: {
    borderWidth: 1,
    borderColor: colors.teal,
    borderRadius: 6,
    paddingVertical: 12,
    marginTop: 10,
    alignItems: 'center',
  },
  btnSecondaryText: { color: colors.teal, fontWeight: '700' },
  hint: { color: colors.textMuted, fontSize: 12, marginTop: 16, textAlign: 'center' },
});
