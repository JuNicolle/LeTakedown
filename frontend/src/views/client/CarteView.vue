<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePanierStore } from '@/stores/panier'
import { getCocktails, getCategories } from '@/api/cocktail'
import { ajouterAuPanier, retirerDuPanier, lancerCommande, getPanier, getCommande } from '@/api/commande'
import CocktailCard from '@/components/CocktailCard.vue'
import UserPanel from '@/components/UserPanel.vue'
import PitTracker from '@/components/PitTracker.vue'
import type { CocktailResponse, CategorieResponse, CommandeResponse, Taille } from '@/types'

const router = useRouter()
const auth = useAuthStore()
const panierStore = usePanierStore()

const cocktails = ref<CocktailResponse[]>([])
const categories = ref<CategorieResponse[]>([])
const categorieActive = ref<number | null>(null)
const loading = ref(true)
const lancing = ref(false)
const toast = ref('')
const commandesSuivies = ref<CommandeResponse[]>([])
const suiviReduits = ref<number[]>([])
let toastTimer: ReturnType<typeof setTimeout> | null = null
let suiviTimer: ReturnType<typeof setInterval> | null = null

const cocktailsFiltres = computed(() =>
  cocktails.value
    .filter(c => c.disponible)
    .filter(c => categorieActive.value === null || c.categorie.id === categorieActive.value)
)

onMounted(async () => {
  const [cs, cats] = await Promise.all([getCocktails(), getCategories()])
  cocktails.value = cs
  categories.value = cats
  if (auth.user) {
    try { panierStore.setPanier(await getPanier(auth.user.id)) } catch {}
  }
  loading.value = false
})

onUnmounted(() => {
  if (suiviTimer) clearInterval(suiviTimer)
})

function showToast(nom: string) {
  toast.value = nom
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = ''), 1500)
}

async function ajouter(cocktail: CocktailResponse, taille: Taille) {
  if (!auth.user) return
  panierStore.setPanier(await ajouterAuPanier(auth.user.id, cocktail.id, taille))
  showToast(cocktail.nom)
}

async function retirer(ligneId: number) {
  if (!auth.user) return
  panierStore.setPanier(await retirerDuPanier(auth.user.id, ligneId))
}

async function lancer() {
  if (!auth.user) return
  lancing.value = true
  try {
    const commande = await lancerCommande(auth.user.id)
    commandesSuivies.value.push(commande)
    panierStore.clear()
    // le backend auto-crée un nouveau PANIER vide au prochain ajout
    startSuivi()
  } catch {
    // rien
  } finally {
    lancing.value = false
  }
}

function startSuivi() {
  if (suiviTimer) return // déjà en cours
  suiviTimer = setInterval(async () => {
    const actives = commandesSuivies.value.filter(c => c.statut !== 'TERMINEE')
    if (actives.length === 0) {
      clearInterval(suiviTimer!); suiviTimer = null; return
    }
    const mises = await Promise.all(actives.map(c => getCommande(c.id)))
    for (const maj of mises) {
      const idx = commandesSuivies.value.findIndex(c => c.id === maj.id)
      if (idx !== -1) commandesSuivies.value[idx] = maj
    }
  }, 5000)
}

function toggleSuivi(id: number) {
  const idx = suiviReduits.value.indexOf(id)
  if (idx >= 0) suiviReduits.value.splice(idx, 1)
  else suiviReduits.value.push(id)
}

function fermerSuivi(id: number) {
  commandesSuivies.value = commandesSuivies.value.filter(c => c.id !== id)
  suiviReduits.value = suiviReduits.value.filter(i => i !== id)
  if (commandesSuivies.value.every(c => c.statut === 'TERMINEE') || commandesSuivies.value.length === 0) {
    if (suiviTimer) { clearInterval(suiviTimer); suiviTimer = null }
  }
}
</script>

<template>
  <div class="page-cream carte-view">
    <!-- DECOR -->
    <div class="decor" aria-hidden="true">
      <div class="decor-glow" />
      <div class="decor-barrier" style="top:420px;right:-50px;width:400px;height:22px;transform:rotate(7deg)" />
      <div class="decor-barrier" style="top:980px;left:-50px;width:340px;height:20px;transform:rotate(-5deg)" />
    </div>

    <!-- TOPBAR -->
    <div class="topbar">
      <div class="topbar-brand" style="cursor:pointer" @click="router.push({ name: 'carte' })">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">BURNOUT BAR</div>
          <div class="topbar-sub">PIT-LANE COCKTAIL HUB</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <UserPanel />
    </div>

    <!-- HERO -->
    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow">
            <span class="hero-blink" />LA CARTE&nbsp;&nbsp;//&nbsp;&nbsp;CHOISISSEZ VOTRE COCKTAIL
          </div>
          <h1 class="hero-h1">PRENEZ LE<br/>DEPART. FONCEZ.</h1>
          <p class="hero-warning">⚠ CRASHBREAKER AUTORISÉ ICI — PAS AU VOLANT</p>
        </div>
        <div class="hero-pills">
          <div class="pill">
            <div class="pill-n" style="color:#FF2A1A">{{ cocktails.length }}</div>
            <div class="pill-l">COCKTAILS</div>
          </div>
          <div class="pill">
            <div class="pill-n" style="color:#B6FF2E">{{ panierStore.nbArticles }}</div>
            <div class="pill-l">AU GARAGE</div>
          </div>
        </div>
      </div>
    </div>

    <!-- MAIN -->
    <div class="main wrap">
      <!-- CARTE -->
      <div class="carte-col">
        <div class="filtres">
          <button class="filtre" :class="{ active: categorieActive === null }" @click="categorieActive = null">
            TOUS
          </button>
          <button
            v-for="cat in categories" :key="cat.id"
            class="filtre" :class="{ active: categorieActive === cat.id }"
            @click="categorieActive = cat.id"
          >
            {{ cat.nom }}
          </button>
        </div>

        <div class="carte-head">
          <h2>LA CARTE</h2>
          <span class="hint">// TAP POUR CHOISIR UN COCKTAIL</span>
        </div>

        <div v-if="loading" class="empty-state">
          <div class="empty-h">CHARGEMENT...</div>
        </div>

        <div v-else class="grid">
          <CocktailCard
            v-for="(c, i) in cocktailsFiltres" :key="c.id"
            :cocktail="c" :num="i + 1"
            @add="(taille) => ajouter(c, taille)"
          />
        </div>
      </div>

      <!-- RAIL -->
      <div class="rail">
        <!-- PIT TRACKERS -->
        <div
          v-for="commande in commandesSuivies" :key="commande.id"
          class="panel suivi-panel"
        >
          <div class="panel-head suivi-head" @click="toggleSuivi(commande.id)">
            <span class="panel-title">PIT TRACKER</span>
            <span class="suivi-id">#{{ commande.id }}</span>
            <span class="suivi-chevron">{{ suiviReduits.includes(commande.id) ? '▼' : '▲' }}</span>
          </div>
          <div v-show="!suiviReduits.includes(commande.id)" class="panel-body">
            <div v-if="commande.statut === 'TERMINEE'" class="ready-banner">🏁 COMMANDE PRÊTE !</div>
            <div v-else class="suivi-pulse">
              <span class="dot" />EN PRÉPARATION
            </div>
            <PitTracker :lignes="commande.lignes" compact />
            <button
              v-if="commande.statut === 'TERMINEE'"
              class="btn-dark"
              style="margin-top:16px;width:100%;text-align:center"
              @click.stop="fermerSuivi(commande.id)"
            >
              ✓ FERMER
            </button>
          </div>
        </div>

        <!-- GARAGE CART -->
        <div class="panel" :style="commandesSuivies.length ? 'margin-top:16px' : ''">
          <div class="panel-head">
            <span class="panel-title">GARAGE CART</span>
            <span class="count-badge">{{ panierStore.nbArticles }}</span>
          </div>
          <div class="panel-body">
            <template v-if="panierStore.panier && panierStore.panier.lignes.length > 0">
              <div v-for="ligne in panierStore.panier.lignes" :key="ligne.id" class="cart-row">
                <span class="cart-accent" style="background:#FF2A1A" />
                <div class="cart-info">
                  <div class="cart-name">{{ ligne.cocktailNom }}</div>
                  <div class="cart-meta">{{ ligne.taille }} — {{ Number(ligne.prixUnitaire).toFixed(2) }}€</div>
                </div>
                <button class="btn-step" @click="retirer(ligne.id)">✕</button>
              </div>
              <div class="total-row">
                <span>TOTAL</span>
                <span class="total-v">{{ panierStore.total.toFixed(2) }}€</span>
              </div>
              <button class="btn-launch" :disabled="lancing" @click="lancer">
                {{ lancing ? 'ENVOI...' : '⟫ LANCER LA COMMANDE' }}
              </button>
            </template>
            <div v-else class="empty-state">
              <div class="empty-h">VIDE</div>
              <p class="empty-p">Ajoutez des cocktails depuis la carte.</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- TOAST -->
    <div v-if="toast" class="toast">⟫ {{ toast }} AJOUTÉ AU GARAGE</div>
  </div>
</template>

<style scoped>
.carte-view {
  background: url('@/assets/backgroundcarteview.png') center / cover fixed;
}

.hero-warning {
  margin-top: 10px;
  font: 700 10px 'Chakra Petch';
  letter-spacing: 2px;
  color: #FF7A00;
  opacity: .8;
}

.main {
  position: relative; z-index: 1;
  display: flex; flex-wrap: wrap; gap: 20px; align-items: flex-start;
  padding-top: 26px; padding-bottom: 70px;
}

.carte-col { flex: 1; min-width: 320px; }

@media (min-width: 769px) {
  .rail { padding-top: 94px; }
}

@media (max-width: 768px) {
  .hero { padding-bottom: 10px; }
  .main { flex-direction: column; padding-top: 6px; gap: 8px; }
  .rail { width: 100%; position: static; order: -1; }
  .rail .panel:first-child { margin-top: 0; }
}


.filtres { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 20px; }

.filtre {
  background: transparent; border: 1.5px solid #d0cbc2;
  color: #8a857a; font: 700 10px 'Chakra Petch'; letter-spacing: 2px;
  padding: 6px 14px; cursor: pointer; transition: all .15s;
}
.filtre:hover, .filtre.active {
  background: #FF2A1A; border-color: #FF2A1A; color: #101015;
}

.carte-head { display: flex; align-items: baseline; gap: 12px; margin-bottom: 16px;color: #F2EEE7 }
.carte-head h2 { font: 900 26px 'Saira'; font-style: italic; letter-spacing: -1px; margin: 0; }
.hint { font: 700 10px 'Space Mono'; color: #8a857a; }

.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px; }
.panel {margin-top: 12px;}
.rail { width: 360px; position: sticky; align-self: flex-start; }

.cart-row { display: flex; align-items: center; gap: 10px; padding: 10px 4px; border-bottom: 1px solid #2c2c36; }
.cart-accent { width: 4px; align-self: stretch; flex-shrink: 0; }
.cart-info { flex: 1; min-width: 0; }
.cart-name { font: 800 14px 'Saira'; font-style: italic; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cart-meta { font: 700 10px 'Space Mono'; color: #8a857a; }

.total-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 4px; font: 700 11px 'Chakra Petch'; letter-spacing: 2px; color: #8a857a; }
.total-v { font: 700 22px 'Space Mono'; color: #F2EEE7; }

.suivi-panel { border-top-color: #B6FF2E; }
.suivi-id { font: 700 12px 'Space Mono'; color: #B6FF2E; }
.suivi-head { cursor: pointer; user-select: none; }
.suivi-head:hover { background: #1f1f28; }
.suivi-chevron { font: 700 10px 'Space Mono'; color: #B6FF2E; margin-left: auto; }

.suivi-pulse {
  display: flex; align-items: center; gap: 8px;
  font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #FF7A00;
  margin-bottom: 14px;
}
.suivi-pulse .dot {
  width: 8px; height: 8px; border-radius: 50%; background: #FF7A00;
  animation: boostBlink .5s ease-in-out infinite;
}

.ready-banner {
  background: #B6FF2E; color: #101015;
  font: 900 14px 'Saira'; font-style: italic;
  text-align: center; padding: 10px; margin-bottom: 14px;
  animation: readyPulse 1.1s ease-in-out infinite;
}

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 26px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
