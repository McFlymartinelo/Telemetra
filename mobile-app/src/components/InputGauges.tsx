import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import Svg, { Line, Path } from 'react-native-svg';
import { colors } from '../theme/colors';

interface InputBarsProps {
  throttle: number;
  brake: number;
  label?: boolean;
}

export function InputBars({ throttle, brake, label = true }: InputBarsProps) {
  const barH = 100;

  return (
    <View style={styles.wrap}>
      {label && <Text style={styles.caption}>INPUTS</Text>}
      <View style={styles.row}>
        <View style={styles.barCol}>
          <Text style={styles.pct}>{Math.round(throttle * 100)}%</Text>
          <View style={[styles.track, { height: barH }]}>
            <View
              style={[
                styles.fill,
                {
                  height: barH * throttle,
                  backgroundColor: colors.green,
                },
              ]}
            />
          </View>
          <Text style={styles.name}>THR</Text>
        </View>
        <View style={styles.barCol}>
          <Text style={styles.pct}>{Math.round(brake * 100)}%</Text>
          <View style={[styles.track, { height: barH }]}>
            <View
              style={[
                styles.fill,
                {
                  height: barH * brake,
                  backgroundColor: colors.red,
                },
              ]}
            />
          </View>
          <Text style={styles.name}>BRK</Text>
        </View>
      </View>
    </View>
  );
}

interface SteeringGaugeProps {
  steering: number;
  width?: number;
}

export function SteeringGauge({ steering, width = 160 }: SteeringGaugeProps) {
  const height = 48;
  const cx = width / 2;
  const angle = steering * 55;
  const rad = ((angle - 90) * Math.PI) / 180;
  const r = width * 0.38;
  const nx = cx + Math.cos(rad) * r;
  const ny = height * 0.75 + Math.sin(rad) * r;

  return (
    <View style={styles.steerWrap}>
      <Text style={styles.steerLabel}>Steering angle</Text>
      <Svg width={width} height={height}>
        <Path
          d={`M ${cx - r} ${height * 0.75} A ${r} ${r} 0 0 1 ${cx + r} ${height * 0.75}`}
          stroke={colors.tealDim}
          strokeWidth={2}
          fill="none"
        />
        <Line
          x1={cx}
          y1={height * 0.75}
          x2={nx}
          y2={ny}
          stroke={colors.teal}
          strokeWidth={2.5}
        />
        <Line
          x1={cx}
          y1={height * 0.3}
          x2={cx}
          y2={height * 0.75}
          stroke={colors.textMuted}
          strokeWidth={1}
          strokeDasharray="3 3"
        />
      </Svg>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { alignItems: 'center' },
  caption: {
    color: colors.teal,
    fontSize: 9,
    fontWeight: '700',
    letterSpacing: 1,
    marginBottom: 4,
  },
  row: { flexDirection: 'row', gap: 8 },
  barCol: { alignItems: 'center' },
  track: {
    width: 18,
    backgroundColor: '#152238',
    borderRadius: 3,
    justifyContent: 'flex-end',
    overflow: 'hidden',
    borderWidth: 1,
    borderColor: colors.panelBorder,
  },
  fill: { width: '100%', borderRadius: 2 },
  pct: { color: colors.textMuted, fontSize: 10, marginBottom: 2 },
  name: { color: colors.textMuted, fontSize: 9, marginTop: 4, fontWeight: '700' },
  steerWrap: { alignItems: 'center' },
  steerLabel: {
    color: colors.textMuted,
    fontSize: 9,
    marginBottom: 2,
    letterSpacing: 0.5,
  },
});
