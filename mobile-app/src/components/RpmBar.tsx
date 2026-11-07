import React from 'react';
import { View, StyleSheet } from 'react-native';
import Svg, { Path, Line, Circle } from 'react-native-svg';
import { rpmPercent } from '@telemetra/shared';
import { colors } from '../theme/colors';

interface RpmBarProps {
  rpm: number;
  rpmMax: number;
  width?: number;
  height?: number;
}

const SEGMENTS = 24;

function segmentColor(index: number, total: number): string {
  const ratio = index / (total - 1);
  if (ratio < 0.55) return colors.green;
  if (ratio < 0.78) return colors.amber;
  return colors.red;
}

export function RpmBar({ rpm, rpmMax, width = 420, height = 56 }: RpmBarProps) {
  const pct = rpmPercent(rpm, rpmMax);
  const litSegments = Math.round((pct / 100) * SEGMENTS);
  const segW = (width - (SEGMENTS - 1) * 3) / SEGMENTS;

  return (
    <View style={styles.wrap}>
      <Svg width={width} height={height} viewBox={`0 0 ${width} ${height}`}>
        {Array.from({ length: SEGMENTS }, (_, i) => {
          const x = i * (segW + 3);
          const active = i < litSegments;
          const fill = active ? segmentColor(i, SEGMENTS) : '#1a2a40';
          const path = `M ${x} ${height * 0.7} Q ${x + segW / 2} ${height * 0.15} ${x + segW} ${height * 0.7} L ${x + segW} ${height} L ${x} ${height} Z`;
          return (
            <Path
              key={i}
              d={path}
              fill={fill}
              opacity={active ? 1 : 0.45}
            />
          );
        })}
        {litSegments > 0 && litSegments < SEGMENTS && (
          <Line
            x1={litSegments * (segW + 3) - 1.5}
            y1={4}
            x2={litSegments * (segW + 3) - 1.5}
            y2={height - 4}
            stroke={colors.amber}
            strokeWidth={2}
          />
        )}
      </Svg>
      <View style={styles.markerRow}>
        <Circle cx={0} cy={0} r={0} />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  wrap: { alignItems: 'center' },
  markerRow: { height: 0 },
});
