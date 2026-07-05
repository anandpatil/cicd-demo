import { defineConfig } from '@playwright/test';
import dotenv from 'dotenv';

dotenv.config({ path: process.env.E2E_ENV_FILE || '.env' });

export default defineConfig({
  testDir: './tests',
  timeout: 30_000,
  expect: {
    timeout: 5_000,
  },
  fullyParallel: true,
  retries: 0,
  reporter: [['list'], ['html', { open: 'never' }]],
  use: {
    trace: 'on-first-retry',
  },
});
