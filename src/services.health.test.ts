import test from 'node:test';
import assert from 'node:assert/strict';

// Minimal localStorage polyfill for Node unit tests
const store = new Map<string, string>();
(globalThis as unknown as { localStorage: Storage }).localStorage = {
  get length() {
    return store.size;
  },
  clear() {
    store.clear();
  },
  getItem(key: string) {
    return store.get(key) ?? null;
  },
  key(index: number) {
    return Array.from(store.keys())[index] ?? null;
  },
  removeItem(key: string) {
    store.delete(key);
  },
  setItem(key: string, value: string) {
    store.set(key, value);
  },
};

import { DataService } from './services';
import { formatINR } from './utils/currency';
import type { User } from './types';

test('getHealthCareSummaries aggregates pet health, vaccinations, meds, and feedings', () => {
  DataService.resetAllData();
  const summaries = DataService.getHealthCareSummaries();
  assert.ok(summaries.length >= 5, 'Should return summaries for seeded pets');

  const barnaby = summaries.find((s) => s.petName === 'Barnaby');
  assert.ok(barnaby, 'Barnaby summary should exist');
  assert.equal(barnaby.vaccinationStatus, 'Due Soon');
  assert.equal(barnaby.activeMedicationsCount, 1);
  assert.equal(barnaby.medicationsGivenToday, 1);
  assert.equal(barnaby.totalFeedingsToday, 2);
  assert.equal(barnaby.completedFeedingsToday, 1);
  assert.equal(barnaby.careStatus, 'Under Treatment');
});

test('getHealthCareSummaries enforces owner pet isolation and hides clinical diagnosis', () => {
  DataService.resetAllData();
  const ownerUser: User = {
    id: 'usr_owner_01',
    email: 'owner@petcare.com',
    name: 'David & Clara Miller',
    role: 'owner',
    createdAt: '2025-01-12T08:00:00Z',
  };
  const ownerSummaries = DataService.getHealthCareSummaries(ownerUser);
  assert.ok(ownerSummaries.length > 0);
  assert.ok(ownerSummaries.every((s) => s.latestDiagnosis === undefined));
  assert.equal(ownerSummaries.some((s) => s.petName === 'Cooper'), false);
});

test('formatINR formats Indian numbering currency accurately', () => {
  assert.equal(formatINR(19116), '₹19,116');
  assert.equal(formatINR(125000), '₹1,25,000');
});
