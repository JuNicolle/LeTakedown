<script setup lang="ts">
import type { LigneCommandeResponse, StatutLigneCommande } from '@/types'

defineProps<{
  lignes: LigneCommandeResponse[]
  compact?: boolean
}>()

const STAGES: Record<StatutLigneCommande, { label: string; color: string; pct: number; tier: 'prep' | 'mid' | 'high' | 'done' }> = {
  PREPARATION_INGREDIENTS: { label: 'PREPARATION', color: '#8A8578', pct: 15,  tier: 'prep' },
  ASSEMBLAGE:              { label: 'ASSEMBLAGE',  color: '#FF7A00', pct: 50,  tier: 'mid'  },
  DRESSAGE:                { label: 'DRESSAGE',    color: '#FF2A1A', pct: 80,  tier: 'high' },
  TERMINEE:                { label: 'PRÊT',        color: '#B6FF2E', pct: 100, tier: 'done' },
}
</script>

<template>
  <div class="track-list" :class="{ compact }">
    <div v-for="ligne in lignes" :key="ligne.id" class="order" :class="`order--${STAGES[ligne.statut].tier}`">
      <div class="order-top">
        <div class="id-name">
          <span class="oid">#{{ ligne.id }}</span>
          <span class="oname">{{ ligne.cocktailNom }}</span>
          <span class="taille-t">{{ ligne.taille }}</span>
        </div>
        <div class="stage-wrap">
          <span class="stage" :style="{ background: STAGES[ligne.statut].color, color: STAGES[ligne.statut].tier === 'done' ? '#101015' : '#101015' }">
            {{ STAGES[ligne.statut].label }}
          </span>
          <span class="pct" :style="{ color: STAGES[ligne.statut].color }">
            {{ STAGES[ligne.statut].pct }}%
          </span>
        </div>
      </div>

      <div class="bar" :class="`bar--${STAGES[ligne.statut].tier}`">
        <div
          class="fill"
          :class="`fill--${STAGES[ligne.statut].tier}`"
          :style="{ width: STAGES[ligne.statut].pct + '%' }"
        />
        <div v-if="!compact" class="gate" style="left:28%" />
        <div v-if="!compact" class="gate" style="left:60%" />
        <div class="marker" :class="`marker--${STAGES[ligne.statut].tier}`" :style="{ left: STAGES[ligne.statut].pct + '%' }" />
      </div>

      <div v-if="!compact" class="legend">
        <span>PRÉPA</span><span>ASSEMBLAGE</span><span>DRESSAGE</span><span>PRÊT</span>
      </div>

      <div v-if="ligne.statut === 'TERMINEE'" class="ready">⚡ CRASHBREAKER — PRÊT À SERVIR</div>
      <div v-else-if="ligne.statut === 'DRESSAGE'" class="boost">
        <span class="dot" />INFERNO EN COURS
      </div>
      <div v-else-if="ligne.statut === 'ASSEMBLAGE'" class="boost boost--mid">
        <span class="dot dot--mid" />CATCHING FIRE
      </div>
    </div>
  </div>
</template>

<style scoped>
.track-list { display: flex; flex-direction: column; gap: 14px; }
.track-list.compact { gap: 8px; }

.order {
  background: #13131a; border: 1px solid #2c2c36;
  padding: 12px 14px;
  transition: border-color .3s;
}
.compact .order { padding: 8px 10px; }
.order--mid  { border-color: rgba(255,122,0,.3); }
.order--high { border-color: rgba(255,42,26,.5); background: #160d0a; }
.order--done { border-color: rgba(182,255,46,.5); background: #0d160a; }

.order-top { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 10px; }
.compact .order-top { margin-bottom: 6px; }

.id-name { display: flex; align-items: center; gap: 8px; min-width: 0; }
.oid  { font: 700 10px 'Space Mono'; color: #6f6a61; }
.oname { font: 800 16px 'Saira'; font-style: italic; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #F2EEE7; }
.compact .oname { font-size: 13px; }
.taille-t { font: 700 9px 'Chakra Petch'; letter-spacing: 1px; color: #6f6a61; }

.stage-wrap { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
.stage { font: 700 10px 'Chakra Petch'; letter-spacing: 1.5px; color: #101015; padding: 3px 8px; }
.compact .stage { font-size: 9px; padding: 2px 6px; }
.pct { font: 700 14px 'Space Mono'; width: 44px; text-align: right; }
.compact .pct { font-size: 11px; width: 36px; }

/* ── BAR TRACK ── */
.bar {
  position: relative; height: 14px;
  background: #26262f; border: 1px solid #2c2c36;
  overflow: visible;
  transition: background .4s, border-color .4s;
}
.compact .bar { height: 10px; }
.bar--mid  { background: #1a1208; border-color: rgba(255,122,0,.25); }
.bar--high { background: #1a0808; border-color: rgba(255,42,26,.4); }
.bar--done { background: #081a08; border-color: rgba(182,255,46,.4); }

/* ── FILL ── */
.fill {
  position: absolute; left: 0; top: 0; bottom: 0;
  transition: width .6s cubic-bezier(.4,0,.2,1);
}

/* TIER 1 — PREPARATION: braise froide */
.fill--prep {
  background: linear-gradient(90deg, #1a0505 0%, #6b0f0f 60%, #FF2A1A 100%);
  box-shadow: 0 0 6px rgba(255,42,26,.3);
}

/* TIER 2 — ASSEMBLAGE: feu qui prend */
.fill--mid {
  background: linear-gradient(90deg, #1a0505, #FF2A1A 35%, #FF7A00 65%, #FFD700 100%);
  background-size: 200% 100%;
  animation: fireFlow 1s linear infinite, midGlow 1.4s ease-in-out infinite;
}

/* TIER 3 — DRESSAGE: enfer total */
.fill--high {
  background: linear-gradient(90deg, #6b0000, #FF2A1A 20%, #FF7A00 45%, #FFD700 65%, rgba(255,255,255,.9) 80%, #FF7A00 90%, #FF2A1A 100%);
  background-size: 300% 100%;
  animation: fireFlowFast .3s linear infinite, highGlow .35s ease-in-out infinite;
}

/* TIER 4 — TERMINEE: Crashbreaker */
.fill--done {
  background: linear-gradient(90deg, #003300, #00a000 25%, #B6FF2E 50%, #ffffff 65%, #B6FF2E 80%, #00a000 100%);
  background-size: 300% 100%;
  animation: crashBreaker .55s linear infinite, doneGlow .7s ease-in-out infinite;
}

/* ── KEYFRAMES FIRE ── */
@keyframes fireFlow {
  from { background-position: 100% 0; }
  to   { background-position:   0% 0; }
}
@keyframes fireFlowFast {
  from { background-position: 100% 0; }
  to   { background-position:   0% 0; }
}
@keyframes crashBreaker {
  from { background-position: 100% 0; }
  to   { background-position:   0% 0; }
}

/* ── KEYFRAMES GLOW ── */
@keyframes midGlow {
  0%,100% { box-shadow: 0 0 8px rgba(255,122,0,.5), 0 0 16px rgba(255,42,26,.25); }
  50%      { box-shadow: 0 0 16px rgba(255,122,0,.8), 0 0 30px rgba(255,42,26,.45); }
}
@keyframes highGlow {
  0%,100% { box-shadow: 0 0 14px rgba(255,42,26,.8), 0 0 28px rgba(255,122,0,.5), 0 0 4px rgba(255,255,255,.3); }
  50%      { box-shadow: 0 0 24px rgba(255,42,26,1),  0 0 48px rgba(255,122,0,.7), 0 0 8px rgba(255,255,255,.6); }
}
@keyframes doneGlow {
  0%,100% { box-shadow: 0 0 18px rgba(182,255,46,.8), 0 0 36px rgba(182,255,46,.4); }
  50%      { box-shadow: 0 0 32px rgba(182,255,46,1),  0 0 64px rgba(182,255,46,.6), 0 0 90px rgba(182,255,46,.25); }
}

/* ── GATES ── */
.gate { position: absolute; top: 0; bottom: 0; width: 1px; background: #3a3a46; }

/* ── MARKER / FLAME TIP ── */
.marker {
  position: absolute; top: -3px;
  width: 10px; height: 20px;
  background: #F2EEE7;
  transform: translateX(-50%);
  clip-path: polygon(0 0, 100% 50%, 0 100%);
  transition: left .6s cubic-bezier(.4,0,.2,1);
}
.compact .marker { width: 7px; height: 14px; top: -2px; }

.marker--prep {
  filter: drop-shadow(0 0 4px rgba(255,42,26,.5));
}
.marker--mid {
  background: #FF7A00;
  filter: drop-shadow(0 0 6px rgba(255,122,0,.9)) drop-shadow(0 0 3px rgba(255,42,26,.6));
  animation: markerFlicker .9s ease-in-out infinite;
}
.marker--high {
  background: #FFD700;
  filter: drop-shadow(0 0 8px rgba(255,215,0,1)) drop-shadow(0 0 4px rgba(255,122,0,.8)) drop-shadow(0 0 2px #fff);
  animation: markerFlicker .3s ease-in-out infinite;
}
.marker--done {
  background: #B6FF2E;
  filter: drop-shadow(0 0 10px rgba(182,255,46,1)) drop-shadow(0 0 6px rgba(182,255,46,.7));
  animation: markerDone .55s ease-in-out infinite;
}

@keyframes markerFlicker {
  0%,100% { opacity: 1;  transform: translateX(-50%) scaleY(1); }
  30%      { opacity: .6; transform: translateX(-50%) scaleY(.8); }
  60%      { opacity: .9; transform: translateX(-50%) scaleY(1.1); }
}
@keyframes markerDone {
  0%,100% { transform: translateX(-50%) scale(1);    filter: drop-shadow(0 0 10px rgba(182,255,46,1)); }
  50%      { transform: translateX(-50%) scale(1.25); filter: drop-shadow(0 0 18px rgba(182,255,46,1)) drop-shadow(0 0 10px #fff); }
}

/* ── LEGEND ── */
.legend {
  display: flex; justify-content: space-between;
  font: 600 9px 'Chakra Petch'; letter-spacing: 1px; color: #6f6a61;
  margin-top: 6px;
}

/* ── STATUS LABELS ── */
.ready {
  margin-top: 10px; background: #B6FF2E; color: #101015;
  font: 900 13px 'Saira'; font-style: italic; letter-spacing: 1px;
  text-align: center; padding: 8px;
  animation: readyPulse 1.1s ease-in-out infinite;
}
.compact .ready { font-size: 10px; padding: 5px; margin-top: 6px; }

.boost {
  margin-top: 8px; display: flex; align-items: center; gap: 6px;
  font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #FF2A1A;
}
.boost--mid { color: #FF7A00; }
.boost .dot {
  width: 7px; height: 7px; border-radius: 50%;
  background: #FF2A1A;
  animation: boostBlink .35s ease-in-out infinite;
}
.boost .dot--mid { background: #FF7A00; animation-duration: .6s; }
</style>
