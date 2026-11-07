import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import type { TireTelemetry } from '@telemetra/shared';
import { colors } from '../theme/colors';

function tireAccent(tempC: number, wearPct: number): string {
  if (wearPct >= 85) return colors.red;
  if (tempC >= 38) return colors.amber;
  if (tempC >= 32) return colors.teal;
  return colors.textMuted;
}

interface TireCardProps {
  tire: TireTelemetry;
}

export function TireCard({ tire }: TireCardProps) {
  const accent = tireAccent(tire.tempC, tire.wearPct);

  return (
    <View style={[styles.card, { borderColor: accent }]}>
      <Text style={[styles.position, { color: accent }]}>{tire.position}</Text>
      <View style={styles.icon}>
        <View style={[styles.tireOuter, { borderColor: accent }]}>
          <View style={styles.tireInner} />
        </View>
      </View>
      <Text style={styles.temp}>{tire.tempC}°C</Text>
      <Text style={styles.wear}>{Math.round(tire.wearPct)}%</Text>
    </View>
  );
}

interface TireRowProps {
  tires: TireTelemetry[];
}

export function TireRow({ tires }: TireRowProps) {
  const order: TireTelemetry['position'][] = ['FL', 'FR', 'RL', 'RR'];
  const sorted = order.map(
    (pos) => tires.find((t) => t.position === pos) ?? { position: pos, tempC: 0, wearPct: 0 },
  );

  return (
    <View style={styles.row}>
      {sorted.map((tire) => (
        <TireCard key={tire.position} tire={tire} />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', gap: 8, flex: 1 },
  card: {
    flex: 1,
    borderWidth: 1,
    borderRadius: 6,
    backgroundColor: colors.panel,
    padding: 6,
    alignItems: 'center',
  },
  position: { fontSize: 11, fontWeight: '800', letterSpacing: 1 },
  icon: { marginVertical: 4 },
  tireOuter: {
    width: 28,
    height: 40,
    borderRadius: 6,
    borderWidth: 2,
    justifyContent: 'center',
    alignItems: 'center',
  },
  tireInner: {
    width: 14,
    height: 24,
    borderRadius: 3,
    backgroundColor: '#1a2a40',
  },
  temp: { color: colors.text, fontSize: 13, fontWeight: '600' },
  wear: { color: colors.textMuted, fontSize: 11 },
});
