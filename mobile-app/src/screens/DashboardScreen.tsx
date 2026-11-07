import React from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  useWindowDimensions,
} from 'react-native';
import { StatusBar } from 'expo-status-bar';
import {
  formatLapDelta,
  formatLapTime,
  GAME_LABELS,
  type UniversalTelemetry,
} from '@telemetra/shared';
import { RpmBar } from '../components/RpmBar';
import { ShiftLights } from '../components/ShiftLights';
import { StatBox } from '../components/StatBox';
import { TireRow } from '../components/TireRow';
import { InputBars, SteeringGauge } from '../components/InputGauges';
import { colors } from '../theme/colors';

interface DashboardScreenProps {
  telemetry: UniversalTelemetry;
  latencyMs: number | null;
  connected: boolean;
  onBack: () => void;
  layout?: 'cockpit' | 'grid';
  onToggleLayout?: () => void;
}

export function DashboardScreen({
  telemetry: t,
  latencyMs,
  connected,
  onBack,
  layout = 'cockpit',
  onToggleLayout,
}: DashboardScreenProps) {
  const { width } = useWindowDimensions();
  const rpmBarWidth = Math.min(width * 0.45, 480);
  const deltaColor =
    t.lapDelta != null && t.lapDelta < 0 ? colors.green : colors.red;

  if (layout === 'grid') {
    return (
      <View style={styles.root}>
        <StatusBar style="light" hidden />
        <GridLayout
          t={t}
          rpmBarWidth={rpmBarWidth}
          deltaColor={deltaColor}
          connected={connected}
          onBack={onBack}
          onToggleLayout={onToggleLayout}
        />
      </View>
    );
  }

  return (
    <View style={styles.root}>
      <StatusBar style="light" hidden />

      {/* Header */}
      <View style={styles.header}>
        <TouchableOpacity onPress={onBack} style={styles.backBtn}>
          <Text style={styles.backText}>←</Text>
        </TouchableOpacity>
        <Text style={styles.logo}>TELEMETRA</Text>
        <ShiftLights rpm={t.rpm} rpmMax={t.rpmMax} />
        <View style={styles.headerRight}>
          <Text style={[styles.wifi, connected && styles.wifiOk]}>
            {connected ? 'Connected — Wi-Fi' : 'Déconnecté'}
          </Text>
          {onToggleLayout && (
            <TouchableOpacity onPress={onToggleLayout}>
              <Text style={styles.layoutBtn}>⊞</Text>
            </TouchableOpacity>
          )}
        </View>
      </View>

      {/* Main body */}
      <View style={styles.body}>
        {/* Left stats */}
        <View style={styles.sideCol}>
          <StatBox label="FUEL LEFT" value={`${t.fuelPct ?? '--'}%`} />
          <StatBox label="LAPS" value={String(t.laps ?? '--')} />
          <StatBox label="POS" value={String(t.position ?? '--')} />
          <StatBox label="LAPS LEFT" value={String(t.lapsLeft ?? '--')} />
        </View>

        {/* Center */}
        <View style={styles.centerCol}>
          <RpmBar rpm={t.rpm} rpmMax={t.rpmMax} width={rpmBarWidth} />
          <Text style={styles.rpmValue}>{t.rpm} RPM</Text>
          <Text style={styles.rpmMax}>RPM Max {t.rpmMax}</Text>

          <Text style={styles.gearLabel}>GEAR</Text>
          <Text style={styles.gear}>
            {t.gear === 0 ? 'N' : t.gear < 0 ? 'R' : t.gear}
          </Text>

          <Text style={styles.speed}>{t.speedKmh}</Text>
          <Text style={styles.speedUnit}>km/h</Text>

          <View style={styles.steerCenter}>
            <SteeringGauge steering={t.steering} width={rpmBarWidth * 0.55} />
          </View>
        </View>

        {/* Right stats + inputs */}
        <View style={styles.sideCol}>
          <StatBox label="LAP" value={String(t.lapNumber ?? '--')} />
          <StatBox
            label="LAP DELTA"
            value={formatLapDelta(t.lapDelta)}
            valueColor={deltaColor}
          />
          <StatBox label="BEST LAP" value={formatLapTime(t.bestLapMs)} />
          <StatBox label="LAST LAP" value={formatLapTime(t.lastLapMs)} />
          <View style={styles.inputSide}>
            <InputBars throttle={t.throttle} brake={t.brake} />
          </View>
        </View>
      </View>

      {/* Tires */}
      <View style={styles.tireSection}>
        <TireRow tires={t.tires} />
      </View>

      {/* Footer */}
      <View style={styles.footer}>
        <Text style={styles.footerItem}>
          Session: <Text style={styles.footerVal}>{t.sessionType ?? '—'}</Text>
        </Text>
        <Text style={styles.footerItem}>
          Track: <Text style={styles.footerVal}>{t.trackName ?? '—'}</Text>
        </Text>
        <Text style={styles.footerItem}>
          Game: <Text style={styles.footerVal}>{GAME_LABELS[t.game] ?? t.game}</Text>
        </Text>
        <Text style={styles.footerItem}>
          Latency: <Text style={styles.footerVal}>{latencyMs ?? '—'} ms</Text>
        </Text>
        <Text style={styles.brandFooter}>TELEMETRA</Text>
      </View>
    </View>
  );
}

function GridLayout({
  t,
  rpmBarWidth,
  deltaColor,
  connected,
  onBack,
  onToggleLayout,
}: {
  t: UniversalTelemetry;
  rpmBarWidth: number;
  deltaColor: string;
  connected: boolean;
  onBack: () => void;
  onToggleLayout?: () => void;
}) {
  return (
    <>
      <View style={styles.gridTop}>
        <TouchableOpacity onPress={onBack}>
          <Text style={styles.backText}>←</Text>
        </TouchableOpacity>
        <View style={styles.gridRpm}>
          <RpmBar rpm={t.rpm} rpmMax={t.rpmMax} width={rpmBarWidth * 1.1} height={48} />
        </View>
        {onToggleLayout && (
          <TouchableOpacity onPress={onToggleLayout}>
            <Text style={styles.layoutBtn}>◫</Text>
          </TouchableOpacity>
        )}
      </View>

      <View style={styles.gridBody}>
        <InputBars throttle={t.throttle} brake={t.brake} />

        <View style={styles.gridStatsLeft}>
          <StatBox label="FUEL LEFT" value={`${t.fuelPct ?? '--'}%`} compact />
          <StatBox label="POSITION" value={String(t.position ?? '--')} compact />
          <StatBox label="LAPS" value={String(t.laps ?? '--')} compact />
        </View>

        <View style={styles.gridCenter}>
          <Text style={styles.gearLabel}>GEAR</Text>
          <Text style={styles.gearGrid}>
            {t.gear === 0 ? 'N' : t.gear < 0 ? 'R' : t.gear}
          </Text>
          <Text style={styles.speedGrid}>{t.speedKmh} km/h</Text>
        </View>

        <View style={styles.gridStatsRight}>
          <StatBox
            label="LAP DELTA"
            value={formatLapDelta(t.lapDelta)}
            valueColor={deltaColor}
            compact
          />
          <StatBox label="BEST LAP" value={formatLapTime(t.bestLapMs)} compact />
          <StatBox label="LAST LAP" value={formatLapTime(t.lastLapMs)} compact />
        </View>

        <SteeringGauge steering={t.steering} width={120} />
      </View>

      <View style={styles.tireSection}>
        <TireRow tires={t.tires} />
      </View>

      <View style={styles.footer}>
        <Text style={styles.footerItem}>
          Wi-Fi: <Text style={connected ? styles.wifiOk : styles.footerVal}>
            {connected ? 'Connected' : 'Offline'}
          </Text>
        </Text>
        <Text style={styles.footerItem}>
          {GAME_LABELS[t.game]} — {t.trackName}
        </Text>
        <Text style={styles.brandFooter}>TELEMETRA</Text>
      </View>
    </>
  );
}

const styles = StyleSheet.create({
  root: {
    flex: 1,
    backgroundColor: colors.background,
    paddingHorizontal: 12,
    paddingVertical: 8,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    marginBottom: 4,
  },
  backBtn: { padding: 4, minWidth: 32 },
  backText: { color: colors.teal, fontSize: 22, fontWeight: '700' },
  logo: {
    color: colors.text,
    fontWeight: '800',
    fontSize: 14,
    letterSpacing: 2,
    position: 'absolute',
    left: 40,
  },
  headerRight: { flexDirection: 'row', alignItems: 'center', gap: 12 },
  wifi: { color: colors.textMuted, fontSize: 11 },
  wifiOk: { color: colors.green },
  layoutBtn: { color: colors.teal, fontSize: 18, paddingHorizontal: 8 },
  body: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 8,
  },
  sideCol: { gap: 6, minWidth: 90 },
  centerCol: { flex: 1, alignItems: 'center', justifyContent: 'center' },
  rpmValue: {
    color: colors.text,
    fontSize: 16,
    fontWeight: '700',
    marginTop: 2,
  },
  rpmMax: { color: colors.textMuted, fontSize: 10, marginBottom: 4 },
  gearLabel: {
    color: colors.teal,
    fontSize: 11,
    fontWeight: '700',
    letterSpacing: 2,
  },
  gear: {
    color: colors.white,
    fontSize: 72,
    fontWeight: '800',
    lineHeight: 76,
  },
  gearGrid: {
    color: colors.white,
    fontSize: 56,
    fontWeight: '800',
    lineHeight: 60,
  },
  speed: {
    color: colors.white,
    fontSize: 42,
    fontWeight: '700',
    lineHeight: 46,
  },
  speedUnit: { color: colors.textMuted, fontSize: 14, marginBottom: 4 },
  speedGrid: { color: colors.white, fontSize: 22, fontWeight: '700' },
  steerCenter: { marginTop: 4 },
  inputSide: { marginTop: 8, alignItems: 'center' },
  tireSection: { marginTop: 4, marginBottom: 4 },
  footer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 16,
    borderTopWidth: 1,
    borderTopColor: colors.panelBorder,
    paddingTop: 6,
  },
  footerItem: { color: colors.textMuted, fontSize: 10 },
  footerVal: { color: colors.text, fontWeight: '600' },
  brandFooter: {
    marginLeft: 'auto',
    color: colors.teal,
    fontSize: 10,
    fontWeight: '800',
    letterSpacing: 2,
  },
  gridTop: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
  },
  gridRpm: { flex: 1, alignItems: 'center' },
  gridBody: {
    flex: 1,
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    gap: 10,
  },
  gridStatsLeft: { gap: 6 },
  gridCenter: { flex: 1, alignItems: 'center' },
  gridStatsRight: { gap: 6 },
});
