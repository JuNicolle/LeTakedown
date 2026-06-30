<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCommandesBarmaker } from '@/api/commande'
import PitTracker from '@/components/PitTracker.vue'
import type { CommandeResponse } from '@/types'

const router = useRouter()
const auth = useAuthStore()
const commandes = ref<CommandeResponse[]>([])
const loading = ref(true)
let timer: ReturnType<typeof setInterval> | null = null

const actives = computed(() => commandes.value.filter((c) => c.statut === 'COMMANDEE' || c.statut === 'EN_COURS'))
const terminees = computed(() => commandes.value.filter((c) => c.statut === 'TERMINEE'))

async function refresh() { commandes.value = await getCommandesBarmaker() }

onMounted(async () => { await refresh(); loading.value = false; timer = setInterval(refresh, 5000) })
onUnmounted(() => { if (timer) clearInterval(timer) })

function logout() { auth.logout(); router.push({ name: 'home' }) }

const STATUT_COLORS: Record<string, string> = {
  COMMANDEE: '#60a5fa', EN_COURS: '#FF7A00', TERMINEE: '#B6FF2E',
}
</script>

<template>
  <div class="page-dark">
    <div class="topbar">
      <div class="topbar-brand" style="cursor:pointer" @click="router.push({ name: 'barmaker-commandes' })">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">PIT CONTROL</div>
          <div class="topbar-sub">BARMAKER — {{ auth.user?.prenom }}</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <div class="topbar-rpm">
        <span class="topbar-rpm-l">EN COURS</span>
        <span class="topbar-rpm-v" style="color:#FF7A00">{{ actives.length }}</span>
      </div>
      <button class="topbar-pill" @click="router.push({ name: 'barmaker-carte' })">
        <span>LA CARTE</span>
      </button>
      <button class="btn-dark" style="font-size:10px;letter-spacing:1.5px" @click="logout">DÉCO</button>
    </div>

    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />COMMANDES ACTIVES&nbsp;&nbsp;//&nbsp;&nbsp;MISE À JOUR AUTO</div>
          <h1 class="hero-h1">LIVE<br/>PIT LANE</h1>
        </div>
        <div class="hero-pills">
          <div class="pill">
            <div class="pill-n" style="color:#FF7A00">{{ actives.length }}</div>
            <div class="pill-l">EN COURS</div>
          </div>
          <div class="pill">
            <div class="pill-n" style="color:#B6FF2E">{{ terminees.length }}</div>
            <div class="pill-l">TERMINÉES</div>
          </div>
        </div>
      </div>
    </div>

    <div class="main wrap">
      <div v-if="loading" class="empty-state" style="color:#F2EEE7"><div class="empty-h">CHARGEMENT...</div></div>

      <template v-else>
        <!-- Actives -->
        <div class="section">
          <div class="section-head">
            <span class="section-title">EN ATTENTE / EN COURS</span>
            <span class="count-badge" style="background:#FF7A00">{{ actives.length }}</span>
          </div>

          <div v-if="actives.length === 0" class="empty-state" style="color:#F2EEE7">
            <div class="empty-h">PIT VIDE</div>
            <p class="empty-p">Aucune commande active pour le moment.</p>
          </div>

          <div class="commandes-list">
            <div
              v-for="c in actives" :key="c.id"
              class="commande-card"
              @click="router.push({ name: 'barmaker-commande-detail', params: { id: c.id } })"
            >
              <div class="card-left">
                <div class="card-id-badge" :style="{ background: STATUT_COLORS[c.statut] }">#{{ c.id }}</div>
                <div>
                  <div class="card-cocktails">{{ c.lignes.length }} COCKTAIL{{ c.lignes.length > 1 ? 'S' : '' }}</div>
                  <div class="card-statut" :style="{ color: STATUT_COLORS[c.statut] }">{{ c.statut }}</div>
                </div>
              </div>
              <div class="card-right">
                <div class="card-pit">
                  <PitTracker :lignes="c.lignes" compact />
                </div>
                <span class="card-arrow">→</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Terminées -->
        <div class="section" style="margin-top:28px">
          <div class="section-head">
            <span class="section-title">TERMINÉES</span>
            <span class="count-badge" style="background:#B6FF2E;color:#101015">{{ terminees.length }}</span>
          </div>
          <div class="commandes-list">
            <div
              v-for="c in terminees" :key="c.id"
              class="commande-card terminated"
              @click="router.push({ name: 'barmaker-commande-detail', params: { id: c.id } })"
            >
              <div class="card-left">
                <div class="card-id-badge" style="background:#B6FF2E;color:#101015">#{{ c.id }}</div>
                <div>
                  <div class="card-cocktails">{{ c.lignes.length }} COCKTAIL{{ c.lignes.length > 1 ? 'S' : '' }}</div>
                  <div class="card-statut" style="color:#B6FF2E">TERMINÉE</div>
                </div>
              </div>
              <span class="card-arrow">→</span>
            </div>
          </div>
        </div>
      </template>

      <p class="refresh-info">// MISE À JOUR AUTOMATIQUE TOUTES LES 5 SECONDES</p>
    </div>
  </div>
</template>

<style scoped>
.main { padding: 24px 0 60px; position: relative; z-index: 1; }

.section-head { display: flex; align-items: center; gap: 10px; margin-bottom: 12px; }
.section-title { font: 700 11px 'Chakra Petch'; letter-spacing: 3px; color: #8a857a; }

.commandes-list { display: flex; flex-direction: column; gap: 8px; }

.commande-card {
  background: #1a1a21; border: 1.5px solid #2c2c36;
  display: flex; align-items: center; gap: 14px;
  padding: 14px 16px; cursor: pointer;
  transition: border-color .15s;
}
.commande-card:hover { border-color: #FF2A1A; }
.commande-card.terminated { opacity: .65; }

.card-left { display: flex; align-items: center; gap: 12px; flex-shrink: 0; }
.card-id-badge { font: 700 13px 'Space Mono'; color: #101015; padding: 4px 9px; }
.card-cocktails { font: 700 12px 'Chakra Petch'; letter-spacing: 1px; color: #F2EEE7; }
.card-statut { font: 700 10px 'Chakra Petch'; letter-spacing: 1.5px; margin-top: 2px; }
.card-right { flex: 1; display: flex; align-items: center; gap: 12px; min-width: 0; }
.card-pit { flex: 1; min-width: 0; }
.card-arrow { color: #6f6a61; font-size: 16px; flex-shrink: 0; }

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 26px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }

.refresh-info { font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #3a3a46; text-align: center; margin-top: 28px; }
</style>
