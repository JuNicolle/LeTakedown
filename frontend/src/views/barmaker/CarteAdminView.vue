<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  getCocktails, getCategories, getIngredients,
  createCocktail, updateCocktail, deleteCocktail,
  createCategorie, deleteCategorie,
  createIngredient, deleteIngredient,
} from '@/api/cocktail'
import type { CocktailResponse, CategorieResponse, IngredientResponse } from '@/types'

const router = useRouter()

const cocktails = ref<CocktailResponse[]>([])
const categories = ref<CategorieResponse[]>([])
const ingredients = ref<IngredientResponse[]>([])
const activeTab = ref<'cocktails' | 'categories' | 'ingredients'>('cocktails')

const newCategorie = ref('')
const newIngredient = ref('')

const showCocktailForm = ref(false)
const editingCocktail = ref<CocktailResponse | null>(null)
const cocktailForm = ref({
  nom: '',
  description: '',
  imageUrl: '',
  categorieId: '' as number | '',
  ingredientIds: [] as number[],
  prix: [
    { taille: 'S' as const, prix: '' },
    { taille: 'M' as const, prix: '' },
    { taille: 'L' as const, prix: '' },
  ],
})
const formError = ref('')
const saving = ref(false)

async function load() {
  [cocktails.value, categories.value, ingredients.value] = await Promise.all([
    getCocktails(), getCategories(), getIngredients(),
  ])
}

onMounted(load)

function openCreateCocktail() {
  editingCocktail.value = null
  cocktailForm.value = {
    nom: '', description: '', imageUrl: '', categorieId: '',
    ingredientIds: [],
    prix: [{ taille: 'S', prix: '' }, { taille: 'M', prix: '' }, { taille: 'L', prix: '' }],
  }
  formError.value = ''
  showCocktailForm.value = true
}

function openEditCocktail(c: CocktailResponse) {
  editingCocktail.value = c
  const prixMap = Object.fromEntries(c.prix.map((p) => [p.taille, String(p.prix)]))
  cocktailForm.value = {
    nom: c.nom,
    description: c.description,
    imageUrl: c.imageUrl ?? '',
    categorieId: c.categorie.id,
    ingredientIds: c.ingredients.map((i) => i.id),
    prix: [
      { taille: 'S', prix: prixMap['S'] ?? '' },
      { taille: 'M', prix: prixMap['M'] ?? '' },
      { taille: 'L', prix: prixMap['L'] ?? '' },
    ],
  }
  formError.value = ''
  showCocktailForm.value = true
}

async function saveCocktail() {
  const f = cocktailForm.value
  if (!f.nom.trim() || !f.categorieId) { formError.value = 'Nom et catégorie obligatoires'; return }
  const prixValides = f.prix.filter((p) => p.prix !== '' && !isNaN(Number(p.prix)))
  if (prixValides.length === 0) { formError.value = 'Au moins un prix requis'; return }
  const payload = {
    nom: f.nom.trim(),
    description: f.description.trim(),
    imageUrl: f.imageUrl.trim() || null,
    categorieId: f.categorieId,
    ingredientIds: f.ingredientIds,
    prix: prixValides.map((p) => ({ taille: p.taille, prix: Number(p.prix) })),
  }
  formError.value = ''
  saving.value = true
  try {
    if (editingCocktail.value) {
      await updateCocktail(editingCocktail.value.id, payload)
    } else {
      await createCocktail(payload)
    }
    await load()
    showCocktailForm.value = false
  } catch (e: unknown) {
    formError.value = e instanceof Error ? e.message : 'Erreur'
  } finally {
    saving.value = false
  }
}

async function supprimerCocktail(id: number) {
  if (!confirm('Supprimer ce cocktail ?')) return
  await deleteCocktail(id); await load()
}

async function ajouterCategorie() {
  if (!newCategorie.value.trim()) return
  await createCategorie(newCategorie.value.trim()); newCategorie.value = ''; await load()
}

async function supprimerCategorie(id: number) {
  if (!confirm('Supprimer cette catégorie ?')) return
  await deleteCategorie(id); await load()
}

async function ajouterIngredient() {
  if (!newIngredient.value.trim()) return
  await createIngredient(newIngredient.value.trim()); newIngredient.value = ''; await load()
}

async function supprimerIngredient(id: number) {
  if (!confirm('Supprimer cet ingrédient ?')) return
  await deleteIngredient(id); await load()
}

function toggleIngredient(id: number) {
  const idx = cocktailForm.value.ingredientIds.indexOf(id)
  if (idx >= 0) cocktailForm.value.ingredientIds.splice(idx, 1)
  else cocktailForm.value.ingredientIds.push(id)
}
</script>

<template>
  <div class="page-dark">
    <!-- TOPBAR -->
    <div class="topbar">
      <div class="topbar-brand" style="cursor:pointer" @click="router.push({ name: 'barmaker-commandes' })">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">CARTE ADMIN</div>
          <div class="topbar-sub">PIT CONTROL — GESTION</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <button class="topbar-pill" @click="router.push({ name: 'barmaker-commandes' })">
        <span>← COMMANDES</span>
      </button>
    </div>

    <!-- HERO -->
    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />GESTION DE LA CARTE&nbsp;&nbsp;//&nbsp;&nbsp;ADMIN</div>
          <h1 class="hero-h1">TUNE<br/>THE MENU.</h1>
        </div>
        <div class="hero-pills">
          <div class="pill">
            <div class="pill-n" style="color:#FF2A1A">{{ cocktails.length }}</div>
            <div class="pill-l">COCKTAILS</div>
          </div>
          <div class="pill">
            <div class="pill-n" style="color:#19C2FF">{{ categories.length }}</div>
            <div class="pill-l">CATÉGORIES</div>
          </div>
          <div class="pill">
            <div class="pill-n" style="color:#B6FF2E">{{ ingredients.length }}</div>
            <div class="pill-l">INGRÉDIENTS</div>
          </div>
        </div>
      </div>
    </div>

    <div class="main wrap">
      <!-- TABS -->
      <div class="tabs">
        <button class="tab" :class="{ active: activeTab === 'cocktails' }" @click="activeTab = 'cocktails'">
          COCKTAILS
        </button>
        <button class="tab" :class="{ active: activeTab === 'categories' }" @click="activeTab = 'categories'">
          CATÉGORIES
        </button>
        <button class="tab" :class="{ active: activeTab === 'ingredients' }" @click="activeTab = 'ingredients'">
          INGRÉDIENTS
        </button>
      </div>

      <!-- ── COCKTAILS ── -->
      <div v-if="activeTab === 'cocktails'">
        <div class="section-bar">
          <span class="section-title">{{ cocktails.length }} COCKTAIL{{ cocktails.length > 1 ? 'S' : '' }}</span>
          <button class="btn-launch" style="padding:8px 18px;font-size:10px" @click="openCreateCocktail">
            + NOUVEAU COCKTAIL
          </button>
        </div>

        <!-- Formulaire cocktail -->
        <div v-if="showCocktailForm" class="panel form-panel">
          <div class="panel-head">
            <span class="panel-title">{{ editingCocktail ? 'MODIFIER — ' + editingCocktail.nom.toUpperCase() : 'NOUVEAU COCKTAIL' }}</span>
            <button class="close-btn" @click="showCocktailForm = false">✕</button>
          </div>
          <div class="panel-body">
            <div class="form-grid">
              <div class="field">
                <label>NOM *</label>
                <input v-model="cocktailForm.nom" placeholder="Takedown Signature" />
              </div>
              <div class="field">
                <label>CATÉGORIE *</label>
                <select v-model="cocktailForm.categorieId">
                  <option value="">— Choisir —</option>
                  <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.nom }}</option>
                </select>
              </div>
              <div class="field full">
                <label>DESCRIPTION</label>
                <textarea v-model="cocktailForm.description" rows="2" placeholder="Description du cocktail" />
              </div>
            </div>

            <div class="field" style="margin-top:16px">
              <label>PRIX PAR TAILLE</label>
              <div class="prix-row">
                <div v-for="p in cocktailForm.prix" :key="p.taille" class="prix-block">
                  <span class="prix-taille">{{ p.taille }}</span>
                  <input v-model="p.prix" type="number" step="0.01" placeholder="—" />
                  <span class="prix-unit">€</span>
                </div>
              </div>
            </div>

            <div class="field" style="margin-top:16px">
              <label>INGRÉDIENTS</label>
              <div class="ing-grid">
                <button
                  v-for="ing in ingredients" :key="ing.id"
                  class="ing-toggle"
                  :class="{ selected: cocktailForm.ingredientIds.includes(ing.id) }"
                  @click="toggleIngredient(ing.id)"
                >
                  {{ ing.nom }}
                </button>
              </div>
            </div>

            <p v-if="formError" class="form-err">⚠ {{ formError }}</p>

            <div class="form-actions">
              <button class="btn-launch" :disabled="saving" @click="saveCocktail">
                {{ saving ? 'SAUVEGARDE...' : (editingCocktail ? '⟫ ENREGISTRER' : '⟫ CRÉER') }}
              </button>
              <button class="btn-dark" @click="showCocktailForm = false">ANNULER</button>
            </div>
          </div>
        </div>

        <!-- Liste cocktails -->
        <div class="items-list">
          <div v-for="c in cocktails" :key="c.id" class="item-card">
            <div class="item-left">
              <span class="item-cat">{{ c.categorie.nom }}</span>
              <div class="item-nom">{{ c.nom }}</div>
              <div class="item-prix">
                <span v-for="p in c.prix" :key="p.taille" class="prix-chip">
                  {{ p.taille }} {{ Number(p.prix).toFixed(2) }}€
                </span>
              </div>
            </div>
            <div class="item-actions">
              <button class="btn-dark" style="font-size:9px;padding:5px 12px" @click="openEditCocktail(c)">MODIFIER</button>
              <button class="btn-delete" @click="supprimerCocktail(c.id)">✕</button>
            </div>
          </div>
        </div>
      </div>

      <!-- ── CATÉGORIES ── -->
      <div v-if="activeTab === 'categories'">
        <div class="add-bar">
          <input v-model="newCategorie" placeholder="NOM DE LA CATÉGORIE" @keyup.enter="ajouterCategorie" />
          <button class="btn-launch" style="padding:8px 18px;font-size:10px" @click="ajouterCategorie">+ AJOUTER</button>
        </div>
        <div class="items-list">
          <div v-for="cat in categories" :key="cat.id" class="item-card">
            <div class="item-nom">{{ cat.nom }}</div>
            <button class="btn-delete" @click="supprimerCategorie(cat.id)">✕</button>
          </div>
        </div>
      </div>

      <!-- ── INGRÉDIENTS ── -->
      <div v-if="activeTab === 'ingredients'">
        <div class="add-bar">
          <input v-model="newIngredient" placeholder="NOM DE L'INGRÉDIENT" @keyup.enter="ajouterIngredient" />
          <button class="btn-launch" style="padding:8px 18px;font-size:10px" @click="ajouterIngredient">+ AJOUTER</button>
        </div>
        <div class="ing-list">
          <div v-for="ing in ingredients" :key="ing.id" class="ing-item">
            <span>{{ ing.nom }}</span>
            <button class="btn-delete" @click="supprimerIngredient(ing.id)">✕</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.main { padding: 24px 0 80px; position: relative; z-index: 1; }

/* TABS */
.tabs { display: flex; gap: 0; margin-bottom: 24px; border-bottom: 1.5px solid #2c2c36; }
.tab {
  background: transparent; color: #6f6a61;
  font: 700 10px 'Chakra Petch'; letter-spacing: 3px;
  padding: 10px 20px; border: none; border-bottom: 2px solid transparent;
  cursor: pointer; transition: all .15s;
}
.tab:hover { color: #F2EEE7; }
.tab.active { color: #FF2A1A; border-bottom-color: #FF2A1A; }

/* SECTION */
.section-bar { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
.section-title { font: 700 11px 'Chakra Petch'; letter-spacing: 3px; color: #8a857a; }

/* FORM PANEL */
.form-panel { margin-bottom: 20px; border-top-color: #FF2A1A; }
.close-btn {
  background: transparent; color: #6f6a61; border: none;
  font: 700 14px 'Space Mono'; cursor: pointer; padding: 0;
}
.close-btn:hover { color: #F2EEE7; }

.form-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
.field { display: flex; flex-direction: column; gap: 6px; }
.field.full { grid-column: 1 / -1; }
.field label { font: 700 9px 'Chakra Petch'; letter-spacing: 2px; color: #6f6a61; }

.prix-row { display: flex; gap: 16px; margin-top: 8px; }
.prix-block { display: flex; align-items: center; gap: 8px; }
.prix-taille { font: 700 13px 'Space Mono'; color: #FF2A1A; width: 14px; }
.prix-block input { width: 80px; }
.prix-unit { font: 700 11px 'Space Mono'; color: #6f6a61; }

.ing-grid { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 8px; max-height: 180px; overflow-y: auto; }
.ing-toggle {
  font: 700 9px 'Chakra Petch'; letter-spacing: 1px;
  background: #13131a; color: #6f6a61;
  border: 1px solid #2c2c36; padding: 4px 10px; cursor: pointer;
  transition: all .1s;
}
.ing-toggle:hover { border-color: #FF2A1A; color: #F2EEE7; }
.ing-toggle.selected { background: #FF2A1A; color: #101015; border-color: #FF2A1A; }

.form-err { font: 700 10px 'Chakra Petch'; letter-spacing: 1px; color: #FF2A1A; margin-top: 12px; }

.form-actions { display: flex; gap: 10px; margin-top: 16px; }

/* ITEMS */
.items-list { display: flex; flex-direction: column; gap: 6px; }
.item-card {
  background: #1a1a21; border: 1.5px solid #2c2c36;
  display: flex; align-items: center; justify-content: space-between;
  padding: 12px 16px; gap: 12px;
  transition: border-color .15s;
}
.item-card:hover { border-color: #3a3a46; }

.item-left { flex: 1; min-width: 0; }
.item-cat { font: 700 9px 'Chakra Petch'; letter-spacing: 2px; color: #FF2A1A; display: block; margin-bottom: 4px; }
.item-nom { font: 800 16px 'Saira'; font-style: italic; color: #F2EEE7; }
.item-prix { display: flex; gap: 6px; margin-top: 6px; flex-wrap: wrap; }
.prix-chip { font: 700 10px 'Space Mono'; color: #8a857a; background: #13131a; padding: 2px 8px; border: 1px solid #2c2c36; }

.item-actions { display: flex; gap: 8px; align-items: center; flex-shrink: 0; }

.btn-delete {
  background: transparent; color: #6f6a61;
  border: 1.5px solid #2c2c36; font: 700 11px 'Space Mono';
  width: 30px; height: 30px; cursor: pointer; display: grid; place-items: center;
  transition: all .15s;
}
.btn-delete:hover { border-color: #FF2A1A; color: #FF2A1A; }

/* ADD BAR */
.add-bar { display: flex; gap: 10px; margin-bottom: 16px; }
.add-bar input { flex: 1; }

/* INGREDIENTS LIST */
.ing-list { display: grid; grid-template-columns: repeat(auto-fill, minmax(200px, 1fr)); gap: 6px; }
.ing-item {
  background: #1a1a21; border: 1.5px solid #2c2c36;
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 12px; font: 700 11px 'Chakra Petch'; letter-spacing: 1px; color: #F2EEE7;
}

/* PILLS */
.hero-pills { display: flex; gap: 10px; }
.pill { text-align: center; border: 1.5px solid #2c2c36; padding: 10px 14px; min-width: 80px; }
.pill-n { font: 700 26px 'Space Mono'; }
.pill-l { font: 600 9px 'Chakra Petch'; letter-spacing: 1.5px; color: #8a857a; }
</style>
