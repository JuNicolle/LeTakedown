<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCommandesClient } from '@/api/commande'
import type { CommandeResponse } from '@/types'

const router = useRouter()
const auth = useAuthStore()
const commandes = ref<CommandeResponse[]>([])
const open = ref(false)

const enCours = computed(() => commandes.value.filter((c) => c.statut === 'COMMANDEE' || c.statut === 'EN_COURS'))
const terminees = computed(() => commandes.value.filter((c) => c.statut === 'TERMINEE'))
const total = computed(() =>
  commandes.value.reduce((s, c) => s + c.lignes.reduce((ls, l) => ls + Number(l.prixUnitaire), 0), 0),
)

onMounted(async () => {
  if (auth.user) commandes.value = await getCommandesClient(auth.user.id)
})

function logout() { auth.logout(); router.push({ name: 'home' }) }
</script>

<template>
  <div class="user-panel">
    <button class="topbar-pill" @click="open = !open">
      <span>{{ auth.user?.prenom }}</span>
      <span class="topbar-pill-n">{{ open ? '▲' : '▼' }}</span>
    </button>

    <div v-if="open" class="dropdown panel">
      <div class="dropdown-section">
        <p class="drop-label">EN COURS ({{ enCours.length }})</p>
        <div v-if="enCours.length === 0" class="drop-empty">Aucune</div>
        <div v-for="c in enCours" :key="c.id" class="drop-row">
          <span class="drop-id">#{{ c.id }}</span>
          <span class="drop-statut" style="color:#FF7A00">{{ c.statut }}</span>
          <button class="btn-dark" style="font-size:9px;padding:3px 8px" @click="router.push({ name: 'suivi', params: { commandeId: c.id } }); open = false">
            SUIVRE
          </button>
        </div>
      </div>

      <div class="dropdown-section">
        <p class="drop-label">HISTORIQUE ({{ terminees.length }})</p>
        <div v-if="terminees.length === 0" class="drop-empty">Aucune</div>
        <div v-for="c in terminees" :key="c.id" class="drop-row">
          <span class="drop-id">#{{ c.id }}</span>
          <span style="color:#B6FF2E;font:700 9px 'Chakra Petch';letter-spacing:1px">TERMINÉE</span>
          <span class="drop-prix">{{ c.lignes.reduce((s, l) => s + Number(l.prixUnitaire), 0).toFixed(2) }}€</span>
        </div>
      </div>

      <div class="drop-total">
        <span>TOTAL COMMANDÉ</span>
        <strong>{{ total.toFixed(2) }}€</strong>
      </div>

      <button class="btn-dark" style="width:100%;text-align:center;margin-top:4px" @click="logout">
        DÉCONNEXION
      </button>
    </div>
  </div>
</template>

<style scoped>
.user-panel { position: relative; }

.dropdown {
  position: absolute; right: 0; top: calc(100% + 10px);
  width: 280px; z-index: 100;
  display: flex; flex-direction: column; gap: 12px;
  padding: 0;
}
.dropdown .panel { border-top-color: #FF2A1A; }

.dropdown-section { padding: 12px 14px 4px; border-bottom: 1px solid #2c2c36; }

.drop-label { font: 700 9px 'Chakra Petch'; letter-spacing: 2px; color: #6f6a61; margin-bottom: 8px; }
.drop-empty { font: 500 11px 'Chakra Petch'; color: #3a3a46; }

.drop-row { display: flex; align-items: center; gap: 8px; padding: 5px 0; font-size: 11px; }
.drop-id { font: 700 11px 'Space Mono'; color: #6f6a61; min-width: 32px; }
.drop-statut { font: 700 9px 'Chakra Petch'; letter-spacing: 1px; flex: 1; }
.drop-prix { font: 700 11px 'Space Mono'; color: #FF2A1A; margin-left: auto; }

.drop-total {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 14px 0; font: 700 9px 'Chakra Petch'; letter-spacing: 2px; color: #6f6a61;
}
.drop-total strong { font: 700 16px 'Space Mono'; color: #F2EEE7; }

.dropdown > .btn-dark { margin: 4px 14px 14px; width: calc(100% - 28px); }
</style>
