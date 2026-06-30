<script setup lang="ts">
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePanierStore } from '@/stores/panier'
import { getCocktails, getCategories } from '@/api/cocktail'
import { retirerDuPanier, lancerCommande, getPanier } from '@/api/commande'
import CocktailCard from '@/components/CocktailCard.vue'
import UserPanel from '@/components/UserPanel.vue'
import type { CocktailResponse, CategorieResponse } from '@/types'

const router = useRouter()
const auth = useAuthStore()
const panierStore = usePanierStore()

const cocktails = ref<CocktailResponse[]>([])
const categories = ref<CategorieResponse[]>([])
const categorieActive = ref<number | null>(null)
const loading = ref(true)
const lancing = ref(false)
const rpm = ref(920)
const toast = ref('')
let toastTimer: ReturnType<typeof setTimeout> | null = null
let rpmTimer: ReturnType<typeof setInterval> | null = null

const cocktailsFiltres = computed(() =>
  categorieActive.value === null
    ? cocktails.value
    : cocktails.value.filter((c) => c.categorie.id === categorieActive.value),
)

onMounted(async () => {
  const [cs, cats] = await Promise.all([getCocktails(), getCategories()])
  cocktails.value = cs
  categories.value = cats
  if (auth.user) {
    try {
      panierStore.setPanier(await getPanier(auth.user.id))
    } catch {}
  }
  loading.value = false
  rpmTimer = setInterval(() => {
    rpm.value = panierStore.nbArticles > 0
      ? 1600 + Math.round(Math.random() * 2300)
      : 880 + Math.round(Math.random() * 130)
  }, 110)
})

onUnmounted(() => { if (rpmTimer) clearInterval(rpmTimer) })

function showToast(nom: string) {
  toast.value = nom
  if (toastTimer) clearTimeout(toastTimer)
  toastTimer = setTimeout(() => (toast.value = ''), 1500)
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
    panierStore.clear()
    router.push({ name: 'suivi', params: { commandeId: commande.id } })
  } catch {
    lancing.value = false
  }
}
</script>

<template>
  <div class="page-cream">
    <!-- DECOR -->
    <div class="decor" aria-hidden="true">
      <div class="decor-glow" />
      <div class="decor-barrier" style="top:420px;right:-50px;width:400px;height:22px;transform:rotate(7deg)" />
      <div class="decor-barrier" style="top:980px;left:-50px;width:340px;height:20px;transform:rotate(-5deg)" />
    </div>

    <!-- TOPBAR -->
    <div class="topbar">
      <div class="topbar-brand">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">BAR'APP</div>
          <div class="topbar-sub">PIT-LANE COCKTAIL HUB</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <div class="topbar-rpm">
        <span class="topbar-rpm-l">RPM</span>
        <span class="topbar-rpm-v">{{ rpm }}</span>
      </div>
      <UserPanel />
    </div>

    <!-- HERO -->
    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow">
            <span class="hero-blink" />LA CARTE&nbsp;&nbsp;//&nbsp;&nbsp;CHOISISSEZ VOTRE COCKTAIL
          </div>
          <h1 class="hero-h1">PRENEZ LA<br/>LIGNE. FONCEZ.</h1>
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
        <!-- Filtres catégories -->
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
            @click="router.push({ name: 'cocktail-detail', params: { id: c.id } })"
          />
        </div>
      </div>

      <!-- RAIL PANIER -->
      <div class="rail">
        <div class="panel">
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
.main {
  position: relative; z-index: 1;
  display: flex; flex-wrap: wrap; gap: 20px; align-items: flex-start;
  padding-top: 26px; padding-bottom: 70px;
}

.carte-col { flex: 1; min-width: 320px; }

.filtres { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 20px; }

.filtre {
  background: transparent; border: 1.5px solid #d0cbc2;
  color: #8a857a; font: 700 10px 'Chakra Petch'; letter-spacing: 2px;
  padding: 6px 14px; cursor: pointer; transition: all .15s;
}
.filtre:hover, .filtre.active {
  background: #FF2A1A; border-color: #FF2A1A; color: #101015;
}

.carte-head { display: flex; align-items: baseline; gap: 12px; margin-bottom: 16px; }
.carte-head h2 { font: 900 26px 'Saira'; font-style: italic; letter-spacing: -1px; margin: 0; }
.hint { font: 700 10px 'Space Mono'; color: #8a857a; }

.grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 14px; }

.rail { width: 360px; flex-grow: 1; position: sticky; top: 74px; align-self: flex-start; }

.cart-row { display: flex; align-items: center; gap: 10px; padding: 10px 4px; border-bottom: 1px solid #2c2c36; }
.cart-accent { width: 4px; align-self: stretch; flex-shrink: 0; }
.cart-info { flex: 1; min-width: 0; }
.cart-name { font: 800 14px 'Saira'; font-style: italic; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.cart-meta { font: 700 10px 'Space Mono'; color: #8a857a; }

.total-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 4px; font: 700 11px 'Chakra Petch'; letter-spacing: 2px; color: #8a857a; }
.total-v { font: 700 22px 'Space Mono'; color: #F2EEE7; }

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 26px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
