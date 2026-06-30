<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { getCocktails, getCategories } from '@/api/cocktail'
import { cocktailColor } from '@/types/colors'
import type { CocktailResponse, CategorieResponse } from '@/types'

const router = useRouter()
const auth = useAuthStore()
const cocktails = ref<CocktailResponse[]>([])
const categories = ref<CategorieResponse[]>([])
const categorieActive = ref<number | null>(null)

onMounted(async () => {
  [cocktails.value, categories.value] = await Promise.all([getCocktails(), getCategories()])
})

const cocktailsFiltres = computed(() =>
  cocktails.value
    .filter(c => c.disponible)
    .filter(c => categorieActive.value === null || c.categorie.id === categorieActive.value)
)

const cocktailsParCategorie = computed(() => {
  const map = new Map<number, { cat: CategorieResponse; items: CocktailResponse[] }>()
  for (const cat of categories.value) map.set(cat.id, { cat, items: [] })
  for (const c of cocktailsFiltres.value) {
    const entry = map.get(c.categorie.id)
    if (entry) entry.items.push(c)
  }
  return [...map.values()].filter(e => e.items.length > 0)
})

function goClient() {
  router.push(auth.isClient ? { name: 'carte' } : { name: 'onboarding' })
}
function goBarmaker() {
  router.push(auth.isBarmaker ? { name: 'barmaker-commandes' } : { name: 'barmaker-login' })
}
</script>

<template>
  <div class="home">
    <div class="decor" aria-hidden="true">
      <div class="decor-glow" />
      <div class="barrier b1" />
      <div class="barrier b2" />
    </div>

    <!-- Topbar -->
    <div class="topbar">
      <div class="topbar-brand">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">BURNOUT BAR</div>
          <div class="topbar-sub">PIT-LANE COCKTAIL HUB</div>
        </div>
      </div>
    </div>

    <!-- Hero -->
    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />BIENVENUE&nbsp;&nbsp;//&nbsp;&nbsp;CHOISISSEZ VOTRE PROFIL</div>
          <h1 class="hero-h1">SHAKE IT.<br />TAKE IT DOWN.</h1>
        </div>
      </div>
    </div>

    <!-- Choices -->
    <div class="section-wrap">
      <div class="choices">
        <div class="choice" @click="goClient">
          <div class="choice-accent" style="background:#FF2A1A" />
          <div class="choice-body">
            <div class="choice-num">01</div>
            <div class="choice-tag" style="background:#FF2A1A">CLIENT</div>
            <div class="choice-name">ENTER<br/>THE BAR</div>
            <p class="choice-desc">Parcourez la carte, composez votre sélection et lancez votre commande en un clic.</p>
          </div>
          <div class="choice-foot">
            <div class="choice-cta">REJOINDRE →</div>
          </div>
        </div>

        <div class="choice" @click="goBarmaker">
          <div class="choice-accent" style="background:#19C2FF" />
          <div class="choice-body">
            <div class="choice-num">02</div>
            <div class="choice-tag" style="background:#19C2FF">BARMAKER</div>
            <div class="choice-name">PIT<br/>CONTROL</div>
            <p class="choice-desc">Gérez les commandes, faites avancer les préparations et administrez la carte.</p>
          </div>
          <div class="choice-foot">
            <div class="choice-cta">CONNEXION →</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Menu consultatif -->
    <div class="menu-wrap">
     <div class="menu-inner">
      <div class="menu-header">
        <div class="menu-title-block">
          <span class="hero-blink" style="width:7px;height:7px;background:#FF2A1A;display:inline-block" />
          <span class="menu-eyebrow">CONSULTATION LIBRE&nbsp;&nbsp;//&nbsp;&nbsp;SANS INSCRIPTION</span>
        </div>
        <h2 class="menu-h2">LA CARTE</h2>
      </div>

      <!-- Filtres catégories -->
      <div class="menu-filtres">
        <button class="mfiltre" :class="{ active: categorieActive === null }" @click="categorieActive = null">
          TOUS
        </button>
        <button
          v-for="cat in categories" :key="cat.id"
          class="mfiltre" :class="{ active: categorieActive === cat.id }"
          :style="categorieActive === cat.id ? { background: cocktailColor(cat.id), borderColor: cocktailColor(cat.id), color: '#101015' } : {}"
          @click="categorieActive = cat.id"
        >
          {{ cat.nom }}
        </button>
      </div>

      <!-- Cocktails par catégorie -->
      <div v-if="cocktailsParCategorie.length === 0" class="menu-empty">CHARGEMENT...</div>

      <div v-for="{ cat, items } in cocktailsParCategorie" :key="cat.id" class="cat-block">
        <div class="cat-header">
          <span class="cat-stripe" :style="{ background: cocktailColor(cat.id) }" />
          <span class="cat-nom">{{ cat.nom }}</span>
          <span class="cat-count">{{ items.length }}</span>
        </div>

        <div class="cocktail-list">
          <div v-for="c in items" :key="c.id" class="cocktail-row">
            <div class="crow-left">
              <div class="crow-name">{{ c.nom }}</div>
              <div class="crow-desc">{{ c.description }}</div>
              <div class="crow-ings">
                <span v-for="ing in c.ingredients" :key="ing.id" class="crow-ing">{{ ing.nom }}</span>
              </div>
            </div>
            <div class="crow-right">
              <div v-for="p in c.prix" :key="p.id" class="crow-prix">
                <span class="crow-taille">{{ p.taille }}</span>
                <span class="crow-montant">{{ Number(p.prix).toFixed(2) }}€</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="menu-cta-banner" @click="goClient">
        <span>⟫ COMMANDER DEPUIS LA CARTE INTERACTIVE</span>
      </div>
     </div>
    </div>
  </div>
</template>

<style scoped>
.home {
  min-height: 100vh;
  background: url('@/assets/backgroundcarteview.png') center / cover fixed;
  position: relative;
}

.decor { position: absolute; inset: 0; pointer-events: none; overflow: hidden; z-index: 0; }
.decor-glow { position: absolute; inset: 0;
  background:
    radial-gradient(700px circle at 80% 10%, rgba(255,122,0,.12), transparent 55%),
    radial-gradient(600px circle at 5% 60%, rgba(25,194,255,.09), transparent 55%);
}
.barrier {
  position: absolute; opacity: .4;
  background: repeating-linear-gradient(45deg, #16161B 0 14px, #FFC400 14px 28px);
  box-shadow: 0 -3px 0 0 #16161B, 0 3px 0 0 #16161B;
}
.b1 { top: 480px; right: -60px; width: 420px; height: 22px; transform: rotate(8deg); }
.b2 { top: 760px; left: -60px; width: 380px; height: 20px; transform: rotate(-6deg); }

/* ── CHOICES ── */
.section-wrap { position: relative; z-index: 1; max-width: 1280px; margin: 0 auto; padding: 40px 24px 48px; }

.choices { display: flex; gap: 20px; flex-wrap: wrap; }

.choice {
  flex: 1; min-width: 280px;
  background: #fff; border: 1.5px solid #16161B;
  display: flex; flex-direction: column;
  cursor: pointer; overflow: hidden;
  transition: transform .15s, box-shadow .15s;
}
.choice:hover { transform: translateY(-4px); box-shadow: 0 16px 40px rgba(255,42,26,.2); }
.choice-accent { height: 8px; }
.choice-body { padding: 18px 20px 0; flex: 1; }
.choice-num { font: 700 36px 'Space Mono'; color: rgba(22,22,27,.10); line-height: .8; margin-bottom: 10px; }
.choice-tag { display: inline-block; font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #fff; padding: 3px 9px; margin-bottom: 10px; }
.choice-name { font: 900 clamp(36px,5vw,52px) 'Saira'; font-style: italic; letter-spacing: -2px; line-height: .85; margin-bottom: 14px; }
.choice-desc { font: 500 13px/1.5 'Chakra Petch'; color: #5a564d; margin-bottom: 20px; }
.choice-foot { border-top: 1.5px solid #16161B; }
.choice-cta { background: #16161B; color: #F2EEE7; font: 700 13px 'Chakra Petch'; letter-spacing: 2px; padding: 16px 20px; transition: background .15s; }
.choice:hover .choice-cta { background: #FF2A1A; color: #101015; }

/* ── MENU ── */
.menu-wrap {
  position: relative; z-index: 1;
  background: #0e0e13; /* fond solide — garantit contraste indépendamment du tunnel */
  border-top: 2px solid #FF2A1A;
}
.menu-inner {
  max-width: 1280px; margin: 0 auto; padding: 40px 24px 80px;
}

.menu-header { margin-bottom: 24px; }
.menu-eyebrow { font: 600 11px 'Chakra Petch'; letter-spacing: 3px; color: #FF7A00; }
.menu-title-block { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; }
.menu-h2 { font: 900 clamp(32px,5vw,56px) 'Saira'; font-style: italic; letter-spacing: -2px; color: #F2EEE7; text-shadow: 3px 0 #FF2A1A, -3px 0 #19C2FF; }

/* Filtres */
.menu-filtres { display: flex; gap: 6px; flex-wrap: wrap; margin-bottom: 28px; }
.mfiltre {
  background: #1a1a21; border: 1.5px solid #3a3a46;
  color: #c8c2b8; /* ~7:1 sur #0e0e13 */
  font: 700 10px 'Chakra Petch'; letter-spacing: 2px;
  padding: 6px 14px; cursor: pointer; transition: all .15s;
}
.mfiltre:hover { border-color: #FF2A1A; color: #F2EEE7; }
.mfiltre.active { background: #FF2A1A; border-color: #FF2A1A; color: #101015; }
.mfiltre:focus-visible { outline: 2px solid #B6FF2E; outline-offset: 2px; }

/* Catégorie */
.cat-block { margin-bottom: 32px; }
.cat-header {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 4px; border-bottom: 1px solid #2c2c36; padding-bottom: 10px;
}
.cat-stripe { width: 4px; height: 18px; flex-shrink: 0; }
.cat-nom { font: 700 11px 'Chakra Petch'; letter-spacing: 3px; color: #F2EEE7; }
.cat-count { font: 700 11px 'Space Mono'; color: #8a857a; margin-left: auto; } /* ~4.6:1 */

/* Cocktail rows */
.cocktail-list { display: flex; flex-direction: column; gap: 1px; }

.cocktail-row {
  display: flex; align-items: flex-start; justify-content: space-between; gap: 16px;
  padding: 14px 16px;
  background: #1a1a21;
  border-left: 3px solid transparent;
  transition: border-color .15s, background .15s;
}
.cocktail-row:hover { background: #212129; border-left-color: #FF2A1A; }

.crow-left { flex: 1; min-width: 0; }
.crow-name { font: 800 18px 'Saira'; font-style: italic; color: #F2EEE7; margin-bottom: 4px; }
.crow-desc {
  font: 500 12px/1.5 'Chakra Petch'; color: #b8b2a8; /* ~7:1 sur #1a1a21 */
  margin-bottom: 8px;
}
.crow-ings { display: flex; flex-wrap: wrap; gap: 4px; }
.crow-ing {
  font: 700 9px 'Chakra Petch'; letter-spacing: 1px;
  color: #c8c2b8; /* ~7.5:1 */
  background: #26262f; border: 1px solid #3a3a46;
  padding: 2px 7px;
}

.crow-right { display: flex; flex-direction: column; gap: 6px; align-items: flex-end; flex-shrink: 0; padding-top: 2px; }
.crow-prix { display: flex; align-items: center; gap: 8px; }
.crow-taille {
  font: 700 9px 'Chakra Petch'; letter-spacing: 1px;
  color: #8a857a; /* ~4.6:1 — élément décoratif secondaire */
}
.crow-montant { font: 700 15px 'Space Mono'; color: #F2EEE7; }

/* CTA bas */
.menu-empty { font: 700 12px 'Chakra Petch'; letter-spacing: 2px; color: #8a857a; padding: 40px 0; text-align: center; }

.menu-cta-banner {
  margin-top: 36px;
  background: #FF2A1A; color: #101015;
  font: 900 16px 'Saira'; font-style: italic; letter-spacing: 1px;
  padding: 18px; text-align: center; cursor: pointer;
  transition: background .15s;
}
.menu-cta-banner:hover { background: #d41f10; }
.menu-cta-banner:focus-visible { outline: 3px solid #B6FF2E; outline-offset: 2px; }
</style>
