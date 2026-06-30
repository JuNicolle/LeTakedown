<script setup lang="ts">
import type { LigneCommandeResponse, StatutLigneCommande } from '@/types'

defineProps<{
  lignes: LigneCommandeResponse[]
  compact?: boolean
}>()

const STAGES: Record<StatutLigneCommande, { label: string; color: string; pct: number }> = {
  PREPARATION_INGREDIENTS: { label: 'PREPARATION', color: '#8A8578', pct: 15 },
  ASSEMBLAGE:              { label: 'ASSEMBLAGE',  color: '#19C2FF', pct: 50 },
  DRESSAGE:                { label: 'DRESSAGE',    color: '#FF7A00', pct: 80 },
  TERMINEE:                { label: 'PRÊT',        color: '#B6FF2E', pct: 100 },
}
</script>

<template>
  <div class="track-list" :class="{ compact }">
    <div v-for="ligne in lignes" :key="ligne.id" class="order">
      <div class="order-top">
        <div class="id-name">
          <span class="oid">#{{ ligne.id }}</span>
          <span class="oname">{{ ligne.cocktailNom }}</span>
          <span class="taille-t">{{ ligne.taille }}</span>
        </div>
        <div class="stage-wrap">
          <span class="stage" :style="{ background: STAGES[ligne.statut].color }">
            {{ STAGES[ligne.statut].label }}
          </span>
          <span class="pct" :style="{ color: STAGES[ligne.statut].color }">
            {{ STAGES[ligne.statut].pct }}%
          </span>
        </div>
      </div>

      <div class="bar">
        <div class="fill" :style="{
          width: STAGES[ligne.statut].pct + '%',
          background: `linear-gradient(90deg, #FF2A1A, ${STAGES[ligne.statut].color})`
        }" />
        <div v-if="!compact" class="gate" style="left:28%" />
        <div v-if="!compact" class="gate" style="left:60%" />
        <div class="marker" :style="{ left: STAGES[ligne.statut].pct + '%' }" />
      </div>

      <div v-if="!compact" class="legend">
        <span>PRÉPA</span><span>ASSEMBLAGE</span><span>DRESSAGE</span><span>PRÊT</span>
      </div>

      <div v-if="ligne.statut === 'TERMINEE'" class="ready">✓ PRÊT À SERVIR</div>
      <div v-else-if="ligne.statut === 'DRESSAGE'" class="boost">
        <span class="dot" />FINITION EN COURS
      </div>
    </div>
  </div>
</template>

<style scoped>
.track-list { display: flex; flex-direction: column; gap: 14px; }
.track-list.compact { gap: 8px; }

.order { background: #13131a; border: 1px solid #2c2c36; padding: 12px 14px; }
.compact .order { padding: 8px 10px; }

.order-top { display: flex; justify-content: space-between; align-items: center; gap: 8px; margin-bottom: 10px; }
.compact .order-top { margin-bottom: 6px; }

.id-name { display: flex; align-items: center; gap: 8px; min-width: 0; }
.oid { font: 700 10px 'Space Mono'; color: #6f6a61; }
.oname { font: 800 16px 'Saira'; font-style: italic; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; color: #F2EEE7; }
.compact .oname { font-size: 13px; }
.taille-t { font: 700 9px 'Chakra Petch'; letter-spacing: 1px; color: #6f6a61; }

.stage-wrap { display: flex; align-items: center; gap: 8px; flex-shrink: 0; }
.stage { font: 700 10px 'Chakra Petch'; letter-spacing: 1.5px; color: #101015; padding: 3px 8px; }
.compact .stage { font-size: 9px; padding: 2px 6px; }
.pct { font: 700 14px 'Space Mono'; width: 44px; text-align: right; }
.compact .pct { font-size: 11px; width: 36px; }

.bar { position: relative; height: 14px; background: #26262f; border: 1px solid #2c2c36; }
.compact .bar { height: 10px; }
.fill { position: absolute; left: 0; top: 0; bottom: 0; transition: width .4s ease; }
.gate { position: absolute; top: 0; bottom: 0; width: 1px; background: #3a3a46; }
.marker {
  position: absolute; top: -3px; width: 10px; height: 20px;
  background: #F2EEE7; transform: translateX(-50%);
  clip-path: polygon(0 0, 100% 50%, 0 100%);
  box-shadow: 0 0 10px rgba(255,255,255,.6);
  transition: left .4s ease;
}
.compact .marker { width: 7px; height: 14px; top: -2px; }

.legend { display: flex; justify-content: space-between; font: 600 9px 'Chakra Petch'; letter-spacing: 1px; color: #6f6a61; margin-top: 6px; }

.ready {
  margin-top: 10px; background: #B6FF2E; color: #101015;
  font: 900 13px 'Saira'; font-style: italic; letter-spacing: 1px;
  text-align: center; padding: 8px; animation: readyPulse 1.1s ease-in-out infinite;
}
.compact .ready { font-size: 10px; padding: 5px; margin-top: 6px; }

.boost { margin-top: 8px; display: flex; align-items: center; gap: 6px; font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #FF7A00; }
.boost .dot { width: 7px; height: 7px; border-radius: 50%; background: #FF7A00; animation: boostBlink .5s ease-in-out infinite; }
</style>
