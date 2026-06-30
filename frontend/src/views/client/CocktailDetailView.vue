<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePanierStore } from '@/stores/panier'
import { getCocktail } from '@/api/cocktail'
import { ajouterAuPanier } from '@/api/commande'
import { cocktailColor } from '@/types/colors'
import type { CocktailResponse, Taille } from '@/types'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()
const panierStore = usePanierStore()

const cocktail = ref<CocktailResponse | null>(null)
const tailleSelectionnee = ref<Taille | null>(null)
const loading = ref(true)
const adding = ref(false)
const toast = ref('')
const error = ref('')

const color = computed(() => cocktail.value ? cocktailColor(cocktail.value.categorie.id) : '#FF2A1A')
const num = computed(() => String(route.params.id).padStart(2, '0'))

const TAILLE_ORDER: Taille[] = ['S', 'M', 'L']
const prixSorted = computed(() =>
  cocktail.value
    ? [...cocktail.value.prix].sort((a, b) => TAILLE_ORDER.indexOf(a.taille) - TAILLE_ORDER.indexOf(b.taille))
    : []
)

onMounted(async () => {
  cocktail.value = await getCocktail(Number(route.params.id))
  if (prixSorted.value.length > 0) tailleSelectionnee.value = prixSorted.value[0]!.taille
  loading.value = false
})

const prixSelectionnee = computed(() =>
  prixSorted.value.find((p) => p.taille === tailleSelectionnee.value),
)

async function ajouter() {
  if (!tailleSelectionnee.value || !auth.user || !cocktail.value) return
  adding.value = true
  error.value = ''
  try {
    panierStore.setPanier(await ajouterAuPanier(auth.user.id, cocktail.value.id, tailleSelectionnee.value))
    toast.value = cocktail.value.nom
    setTimeout(() => (toast.value = ''), 1500)
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Erreur'
  } finally {
    adding.value = false
  }
}
</script>

<template>
  <div class="page-cream">
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
      <button class="topbar-pill" @click="router.push({ name: 'carte' })">
        <span>← CARTE</span>
      </button>
      <button class="topbar-pill" @click="router.push({ name: 'panier' })">
        <span>GARAGE</span>
        <span class="topbar-pill-n">{{ panierStore.nbArticles }}</span>
      </button>
    </div>

    <div v-if="loading" class="loading-shell">
      <div class="empty-h">CHARGEMENT...</div>
    </div>

    <template v-else-if="cocktail">
      <!-- HERO avec couleur du cocktail -->
      <div class="detail-hero" :style="{ borderBottomColor: color }">
        <div class="hero-inner" style="position:relative">
          <div>
            <div class="hero-eyebrow">
              <span class="hero-blink" :style="{ background: color }" />
              {{ cocktail.categorie.nom }}&nbsp;&nbsp;//&nbsp;&nbsp;DÉTAIL COCKTAIL
            </div>
            <div class="detail-num">{{ num }}</div>
            <h1 class="hero-h1">{{ cocktail.nom }}</h1>
            <p class="detail-desc">{{ cocktail.description }}</p>
          </div>
          <div class="hero-pills">
            <div class="pill">
              <div class="pill-n" :style="{ color }">{{ cocktail.ingredients.length }}</div>
              <div class="pill-l">INGRÉDIENTS</div>
            </div>
            <div class="pill">
              <div class="pill-n" style="color:#B6FF2E">{{ cocktail.prix.length }}</div>
              <div class="pill-l">TAILLES</div>
            </div>
          </div>
        </div>
      </div>

      <!-- MAIN -->
      <div class="detail-main wrap">
        <!-- Ingrédients -->
        <div class="panel panel-section">
          <div class="panel-head">
            <span class="panel-title">INGRÉDIENTS</span>
            <span class="count-badge" :style="{ background: color }">{{ cocktail.ingredients.length }}</span>
          </div>
          <div class="panel-body ing-grid">
            <span v-for="ing in cocktail.ingredients" :key="ing.id" class="ing-tag">
              {{ ing.nom }}
            </span>
          </div>
        </div>

        <!-- Sélection taille -->
        <div class="panel panel-section">
          <div class="panel-head">
            <span class="panel-title">CHOISIR LA TAILLE</span>
          </div>
          <div class="panel-body">
            <div class="tailles">
              <div
                v-for="p in prixSorted" :key="p.id"
                class="taille-btn"
                :class="{ active: tailleSelectionnee === p.taille }"
                :style="tailleSelectionnee === p.taille ? { borderColor: color, background: color + '18' } : {}"
                @click="tailleSelectionnee = p.taille"
              >
                <span class="taille-l">{{ p.taille }}</span>
                <span class="taille-prix" :style="tailleSelectionnee === p.taille ? { color } : {}">
                  {{ Number(p.prix).toFixed(2) }}€
                </span>
              </div>
            </div>

            <p v-if="error" class="err" style="margin-bottom:.75rem">{{ error }}</p>

            <button
              class="btn-launch"
              :style="{ background: color }"
              :disabled="!tailleSelectionnee || adding"
              @click="ajouter"
            >
              {{ adding ? 'AJOUT...' : `⟫ AJOUTER AU GARAGE — ${prixSelectionnee ? Number(prixSelectionnee.prix).toFixed(2) + '€' : ''}` }}
            </button>
          </div>
        </div>
      </div>
    </template>

    <!-- TOAST -->
    <div v-if="toast" class="toast" :style="{ background: color }">⟫ {{ toast }} AJOUTÉ AU GARAGE</div>
  </div>
</template>

<style scoped>
.loading-shell { display: flex; align-items: center; justify-content: center; min-height: calc(100vh - 58px); }

.detail-hero {
  position: relative; overflow: hidden;
  background: #16161B; color: #F2EEE7;
  padding: 28px 24px; border-bottom: 3px solid #FF2A1A;
}
.detail-hero::before {
  content: ''; position: absolute; inset: 0;
  background: repeating-linear-gradient(115deg, rgba(255,42,26,.10) 0 3px, transparent 3px 26px);
  animation: streak 1.1s linear infinite; pointer-events: none;
}

.detail-num { font: 700 48px 'Space Mono'; color: rgba(242,238,231,.10); line-height: .8; margin-bottom: 8px; }
.detail-desc { font: 500 14px/1.5 'Chakra Petch'; color: #b0a89a; max-width: 500px; margin-top: 10px; }

.detail-main {
  display: grid; grid-template-columns: 1fr 1fr; gap: 16px;
  padding-top: 24px; padding-bottom: 60px; position: relative; z-index: 1;
}
@media (max-width: 640px) { .detail-main { grid-template-columns: 1fr; } }

.panel-section { color: #F2EEE7; }

.ing-grid { display: flex; flex-wrap: wrap; gap: 6px; padding-top: 10px; }
.ing-tag {
  background: #26262f; border: 1px solid #3a3a46;
  font: 700 10px 'Chakra Petch'; letter-spacing: 1px; color: #b0a89a;
  padding: 4px 10px;
}

.tailles { display: flex; gap: 10px; margin-bottom: 16px; flex-wrap: wrap; }

.taille-btn {
  flex: 1; min-width: 70px; display: flex; flex-direction: column; align-items: center; gap: 4px;
  padding: 12px 10px; background: #26262f; border: 2px solid #3a3a46;
  cursor: pointer; transition: all .15s;
}
.taille-btn:hover { border-color: #5a564d; }

.taille-l { font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #8a857a; }
.taille-prix { font: 700 20px 'Space Mono'; color: #F2EEE7; }

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 26px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
