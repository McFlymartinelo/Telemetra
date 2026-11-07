import React from 'react';
import { View, StyleSheet } from 'react-native';
import { colors } from '../theme/colors';

interface ShiftLightsProps {
  rpm: number;
  rpmMax: number;
}

const LIGHT_COUNT = 8;

export function ShiftLights({ rpm, rpmMax }: ShiftLightsProps) {
  const pct = rpmMax > 0 ? rpm / rpmMax : 0;
  const litCount = Math.min(LIGHT_COUNT, Math.floor(pct * LIGHT_COUNT * 1.05));

  return (
    <View style={styles.row}>
      {Array.from({ length: LIGHT_COUNT }, (_, i) => {
        let bg = '#1e2d42';
        if (i < litCount) {
          if (i < 2) bg = colors.green;
          else if (i < 5) bg = colors.red;
          else bg = colors.purple;
        }
        return <View key={i} style={[styles.light, { backgroundColor: bg }]} />;
      })}
    </View>
  );
}

const styles = StyleSheet.create({
  row: { flexDirection: 'row', gap: 6, alignItems: 'center' },
  light: {
    width: 14,
    height: 14,
    borderRadius: 7,
    borderWidth: 1,
    borderColor: '#2a4060',
  },
});
