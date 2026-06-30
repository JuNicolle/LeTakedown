<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCommande } from '@/api/commande'
import PitTracker from '@/components/PitTracker.vue'
import type { CommandeResponse } from '@/types'

const route = useRoute()
const router = useRouter()
const commande = ref<CommandeResponse | null>(null)
const loading = ref(true)
let intervalId: ReturnType<typeof setInterval> | null = null

async function refresh() {
  commande.value = await getCommande(Number(route.params.commandeId))
  if (commande.value?.statut === 'TERMINEE' && intervalId) {
    clearInterval(intervalId)
    intervalId = null
  }
}

onMounted(async () => {
  await refresh()
  loading.value = false
  if (commande.value?.statut !== 'TERMINEE') {
    intervalId = setInterval(refresh, 5000)
  }
})
onUnmounted(() => { if (intervalId) clearInterval(intervalId) })
</script>

<template>
  <div class="page-dark">
    <!-- TOPBAR -->
    <div class="topbar">
      <div class="topbar-brand" style="cursor:pointer" @click="router.push({ name: 'carte' })">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">BURNOUT BAR</div>
          <div class="topbar-sub">LIVE PIT TRACKER</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <button class="topbar-pill" @click="router.push({ name: 'carte' })">
        <span>← CARTE</span>
      </button>
    </div>

    <!-- HERO -->
    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow">
            <span class="hero-blink" />SUIVI EN DIRECT&nbsp;&nbsp;//&nbsp;&nbsp;COMMANDE #{{ route.params.commandeId }}
          </div>
          <h1 class="hero-h1">LIVE PIT<br/>TRACKER</h1>
        </div>
        <div v-if="commande" class="hero-pills">
          <div class="pill">
            <div class="pill-n" :style="{ color: commande.statut === 'TERMINEE' ? '#B6FF2E' : '#FF7A00' }">
              {{ commande.lignes.length }}
            </div>
            <div class="pill-l">COCKTAILS</div>
          </div>
          <div class="pill">
            <div class="pill-n" :style="{ color: commande.statut === 'TERMINEE' ? '#B6FF2E' : '#19C2FF' }">
              {{ commande.statut }}
            </div>
            <div class="pill-l">STATUT</div>
          </div>
        </div>
      </div>
    </div>

    <!-- TRACKER -->
    <div class="tracker-shell wrap">
      <div v-if="loading" class="empty-state">
        <div class="empty-h">CHARGEMENT...</div>
      </div>

      <template v-else-if="commande">
        <div v-if="commande.statut === 'TERMINEE'" class="ready-banner">
          🏁 COMMANDE PRÊTE — BON COCKTAIL !
        </div>

        <div class="panel tracker-panel">
          <div class="panel-head">
            <span class="panel-title">PROGRESSION PAR COCKTAIL</span>
            <span class="live-dot-wrap">
              <span class="live-dot" :style="{ background: commande.statut === 'TERMINEE' ? '#B6FF2E' : '#FF7A00' }" />
              <span class="live-label">LIVE</span>
            </span>
          </div>
          <div class="panel-body">
            <PitTracker :lignes="commande.lignes" />
          </div>
        </div>

        <p v-if="commande.statut !== 'TERMINEE'" class="refresh-info">
          // MISE À JOUR AUTOMATIQUE TOUTES LES 5 SECONDES
        </p>
      </template>
    </div>
  </div>
</template>

<style scoped>
.tracker-shell { padding: 28px 0 60px; position: relative; z-index: 1; }

.ready-banner {
  background: #B6FF2E; color: #101015;
  font: 900 18px 'Saira'; font-style: italic; letter-spacing: 1px;
  text-align: center; padding: 16px; margin-bottom: 16px;
  animation: readyPulse 1.1s ease-in-out infinite;
}

.tracker-panel { color: #F2EEE7; }
.tracker-panel.panel { border-top-color: #B6FF2E; }

.live-dot-wrap { display: flex; align-items: center; gap: 6px; }
.live-dot { width: 8px; height: 8px; border-radius: 50%; animation: blink 1s steps(1) infinite; }
.live-label { font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #8a857a; }

.refresh-info { font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #6f6a61; text-align: center; margin-top: 16px; }

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 16px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
