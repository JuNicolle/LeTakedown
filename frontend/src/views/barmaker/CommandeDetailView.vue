<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getCommande, avancerLigne } from '@/api/commande'
import PitTracker from '@/components/PitTracker.vue'
import type { CommandeResponse, StatutLigneCommande } from '@/types'

const route = useRoute()
const router = useRouter()
const commande = ref<CommandeResponse | null>(null)
const loading = ref(true)
const advancing = ref<number | null>(null)

const NEXT_LABELS: Partial<Record<StatutLigneCommande, string>> = {
  PREPARATION_INGREDIENTS: "⟫ PASSER À L'ASSEMBLAGE",
  ASSEMBLAGE:              '⟫ PASSER AU DRESSAGE',
  DRESSAGE:                '⟫ TERMINER CE COCKTAIL',
}

onMounted(async () => {
  commande.value = await getCommande(Number(route.params.id))
  loading.value = false
})

async function avancer(ligneId: number) {
  if (!commande.value) return
  advancing.value = ligneId
  try { commande.value = await avancerLigne(commande.value.id, ligneId) }
  finally { advancing.value = null }
}
</script>

<template>
  <div class="page-dark">
    <div class="topbar">
      <div class="topbar-brand">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">COMMANDE #{{ route.params.id }}</div>
          <div class="topbar-sub">PIT CONTROL — DÉTAIL</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <button class="topbar-pill" @click="router.push({ name: 'barmaker-commandes' })">
        <span>← COMMANDES</span>
      </button>
    </div>

    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow">
            <span class="hero-blink" v-if="commande?.statut !== 'TERMINEE'" />
            COMMANDE #{{ route.params.id }}&nbsp;&nbsp;//&nbsp;&nbsp;{{ commande?.statut ?? '...' }}
          </div>
          <h1 class="hero-h1">AVANCER<br/>LES ÉTAPES</h1>
        </div>
        <div v-if="commande" class="hero-pills">
          <div class="pill">
            <div class="pill-n" style="color:#FF7A00">{{ commande.lignes.length }}</div>
            <div class="pill-l">COCKTAILS</div>
          </div>
          <div class="pill">
            <div class="pill-n" style="color:#B6FF2E">
              {{ commande.lignes.filter(l => l.statut === 'TERMINEE').length }}
            </div>
            <div class="pill-l">TERMINÉS</div>
          </div>
        </div>
      </div>
    </div>

    <div class="main wrap">
      <div v-if="loading" class="empty-state" style="color:#F2EEE7"><div class="empty-h">CHARGEMENT...</div></div>

      <template v-else-if="commande">
        <div v-if="commande.statut === 'TERMINEE'" class="ready-banner">✓ COMMANDE TERMINÉE</div>

        <!-- Vue d'ensemble PitTracker -->
        <div class="panel overview-panel">
          <div class="panel-head"><span class="panel-title">VUE D'ENSEMBLE</span></div>
          <div class="panel-body">
            <PitTracker :lignes="commande.lignes" />
          </div>
        </div>

        <!-- Actions par ligne -->
        <div class="lignes-section">
          <div class="section-head">
            <span class="section-title">AVANCER LES COCKTAILS</span>
          </div>

          <div class="lignes-list">
            <div v-for="ligne in commande.lignes" :key="ligne.id" class="ligne-card">
              <div class="ligne-head">
                <div class="ligne-id">#{{ ligne.id }}</div>
                <div class="ligne-nom">{{ ligne.cocktailNom }}</div>
                <div class="ligne-taille">{{ ligne.taille }}</div>
              </div>

              <div v-if="ligne.statut !== 'TERMINEE'" class="ligne-action">
                <button
                  class="btn-launch"
                  :disabled="advancing === ligne.id"
                  @click="avancer(ligne.id)"
                >
                  {{ advancing === ligne.id ? '...' : NEXT_LABELS[ligne.statut] }}
                </button>
              </div>
              <div v-else class="ligne-done">✓ PRÊT À SERVIR</div>
            </div>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.main { padding: 24px 0 60px; display: flex; flex-direction: column; gap: 20px; }

.ready-banner {
  background: #B6FF2E; color: #101015;
  font: 900 16px 'Saira'; font-style: italic; letter-spacing: 1px;
  text-align: center; padding: 14px;
  animation: readyPulse 1.1s ease-in-out infinite;
}

.overview-panel { color: #F2EEE7; }
.overview-panel.panel { border-top-color: #B6FF2E; }

.section-head { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.section-title { font: 700 11px 'Chakra Petch'; letter-spacing: 3px; color: #8a857a; }

.lignes-list { display: flex; flex-direction: column; gap: 10px; }

.ligne-card { background: #1a1a21; border: 1.5px solid #2c2c36; padding: 16px; }

.ligne-head { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.ligne-id { font: 700 11px 'Space Mono'; color: #6f6a61; }
.ligne-nom { font: 900 18px 'Saira'; font-style: italic; color: #F2EEE7; flex: 1; }
.ligne-taille {
  font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #101015;
  background: #8a857a; padding: 3px 8px;
}

.ligne-action .btn-launch { background: #FF2A1A; }
.ligne-action .btn-launch:disabled { opacity: .5; cursor: not-allowed; }

.ligne-done {
  background: #B6FF2E; color: #101015;
  font: 900 14px 'Saira'; font-style: italic;
  text-align: center; padding: 10px;
}

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 26px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
