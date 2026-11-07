import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { colors } from '../theme/colors';

interface StatBoxProps {
  label: string;
  value: string;
  valueColor?: string;
  compact?: boolean;
}

export function StatBox({ label, value, valueColor, compact }: StatBoxProps) {
  return (
    <View style={[styles.box, compact && styles.compact]}>
      <Text style={styles.label}>{label}</Text>
      <Text style={[styles.value, valueColor ? { color: valueColor } : null]}>
        {value}
      </Text>
    </View>
  );
}

const styles = StyleSheet.create({
  box: {
    borderWidth: 1,
    borderColor: colors.panelBorder,
    backgroundColor: colors.panel,
    borderRadius: 4,
    paddingHorizontal: 10,
    paddingVertical: 6,
    minWidth: 72,
  },
  compact: { paddingVertical: 4, minWidth: 64 },
  label: {
    color: colors.teal,
    fontSize: 9,
    fontWeight: '700',
    letterSpacing: 0.8,
    marginBottom: 2,
  },
  value: {
    color: colors.text,
    fontSize: 18,
    fontWeight: '700',
  },
});
