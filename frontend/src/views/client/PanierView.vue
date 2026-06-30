<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { usePanierStore } from '@/stores/panier'
import { getPanier, retirerDuPanier, lancerCommande } from '@/api/commande'

const router = useRouter()
const auth = useAuthStore()
const panierStore = usePanierStore()
const loading = ref(true)
const lancing = ref(false)
const error = ref('')

onMounted(async () => {
  if (auth.user) panierStore.setPanier(await getPanier(auth.user.id))
  loading.value = false
})

async function retirer(ligneId: number) {
  if (!auth.user) return
  panierStore.setPanier(await retirerDuPanier(auth.user.id, ligneId))
}

async function lancer() {
  if (!auth.user) return
  lancing.value = true
  error.value = ''
  try {
    const commande = await lancerCommande(auth.user.id)
    panierStore.clear()
    router.push({ name: 'suivi', params: { commandeId: commande.id } })
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Erreur'
    lancing.value = false
  }
}
</script>

<template>
  <div class="page-dark">
    <div class="topbar">
      <div class="topbar-brand">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">GARAGE CART</div>
          <div class="topbar-sub">VOTRE SÉLECTION</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <button class="topbar-pill" @click="router.push({ name: 'carte' })">
        <span>← CARTE</span>
      </button>
    </div>

    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />VOTRE SÉLECTION&nbsp;&nbsp;//&nbsp;&nbsp;PRÊT À LANCER ?</div>
          <h1 class="hero-h1">GARAGE<br/>CART</h1>
        </div>
        <div class="hero-pills">
          <div class="pill">
            <div class="pill-n" style="color:#FF2A1A">{{ panierStore.nbArticles }}</div>
            <div class="pill-l">COCKTAILS</div>
          </div>
          <div class="pill">
            <div class="pill-n" style="color:#B6FF2E">{{ panierStore.total.toFixed(2) }}€</div>
            <div class="pill-l">TOTAL</div>
          </div>
        </div>
      </div>
    </div>

    <div class="shell wrap">
      <div v-if="loading" class="empty-state" style="color:#F2EEE7">
        <div class="empty-h">CHARGEMENT...</div>
      </div>

      <template v-else>
        <div v-if="!panierStore.panier || panierStore.panier.lignes.length === 0" class="panel">
          <div class="panel-body empty-state">
            <div class="empty-h">GARAGE VIDE</div>
            <p class="empty-p">Ajoutez des cocktails depuis la carte pour remplir votre garage.</p>
            <button class="btn-launch" style="margin-top:20px;max-width:300px;margin-left:auto;margin-right:auto;display:block" @click="router.push({ name: 'carte' })">
              ⟫ ALLER À LA CARTE
            </button>
          </div>
        </div>

        <div v-else class="panel">
          <div class="panel-head">
            <span class="panel-title">VOTRE SÉLECTION</span>
            <span class="count-badge">{{ panierStore.nbArticles }}</span>
          </div>
          <div class="panel-body">
            <div v-for="ligne in panierStore.panier!.lignes" :key="ligne.id" class="cart-row">
              <span class="cart-accent" style="background:#FF2A1A" />
              <div class="cart-info">
                <div class="cart-name">{{ ligne.cocktailNom }}</div>
                <div class="cart-meta">{{ ligne.taille }} — {{ Number(ligne.prixUnitaire).toFixed(2) }}€</div>
              </div>
              <div class="cart-prix">{{ Number(ligne.prixUnitaire).toFixed(2) }}€</div>
              <button class="btn-step" @click="retirer(ligne.id)">✕</button>
            </div>

            <div class="total-row">
              <span>TOTAL</span>
              <span class="total-v">{{ panierStore.total.toFixed(2) }}€</span>
            </div>

            <p v-if="error" class="err" style="margin-bottom:.75rem">{{ error }}</p>

            <button class="btn-launch" :disabled="lancing" @click="lancer">
              {{ lancing ? 'ENVOI EN COURS...' : '⟫ LANCER LA COMMANDE' }}
            </button>
          </div>
        </div>
      </template>
    </div>
  </div>
</template>

<style scoped>
.shell { padding: 24px 0 60px; }

.cart-row { display: flex; align-items: center; gap: 10px; padding: 12px 4px; border-bottom: 1px solid #2c2c36; }
.cart-accent { width: 4px; align-self: stretch; flex-shrink: 0; }
.cart-info { flex: 1; min-width: 0; }
.cart-name { font: 800 16px 'Saira'; font-style: italic; }
.cart-meta { font: 700 11px 'Space Mono'; color: #8a857a; }
.cart-prix { font: 700 16px 'Space Mono'; color: #F2EEE7; margin-right: 4px; }

.total-row { display: flex; justify-content: space-between; align-items: center; padding: 16px 4px; font: 700 12px 'Chakra Petch'; letter-spacing: 2px; color: #8a857a; margin-top: 4px; }
.total-v { font: 700 28px 'Space Mono'; color: #F2EEE7; }

.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 22px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
