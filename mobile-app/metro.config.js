const { getDefaultConfig } = require('expo/metro-config');
const path = require('path');

// Keep Metro project root on mobile-app (not monorepo root)
process.env.EXPO_NO_METRO_WORKSPACE_ROOT = '1';

const projectRoot = __dirname;
const monorepoRoot = path.resolve(projectRoot, '..');

/** @type {import('expo/metro-config').MetroConfig} */
const config = getDefaultConfig(projectRoot);

config.watchFolders = [monorepoRoot];
config.resolver.nodeModulesPaths = [
  path.resolve(projectRoot, 'node_modules'),
  path.resolve(monorepoRoot, 'node_modules'),
];
// Allow nested deps (e.g. whatwg-url → webidl-conversions, react-native → virtualized-lists)
config.resolver.disableHierarchicalLookup = false;

// Single React instance — avoids "Invalid hook call" in monorepo
config.resolver.extraNodeModules = {
  react: path.resolve(monorepoRoot, 'node_modules/react'),
  'react-native': path.resolve(projectRoot, 'node_modules/react-native'),
};

module.exports = config;
