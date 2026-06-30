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

// Nouvelle catégorie
const newCategorie = ref('')

// Nouvel ingrédient
const newIngredient = ref('')

// Nouveau / édition cocktail
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
  if (!f.nom.trim() || !f.categorieId) {
    formError.value = 'Nom et catégorie obligatoires'
    return
  }
  const prixValides = f.prix.filter((p) => p.prix !== '' && !isNaN(Number(p.prix)))
  if (prixValides.length === 0) {
    formError.value = 'Au moins un prix requis'
    return
  }
  const payload = {
    nom: f.nom.trim(),
    description: f.description.trim(),
    imageUrl: f.imageUrl.trim() || null,
    categorieId: f.categorieId,
    ingredientIds: f.ingredientIds,
    prix: prixValides.map((p) => ({ taille: p.taille, prix: Number(p.prix) })),
  }
  formError.value = ''
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
  }
}

async function supprimerCocktail(id: number) {
  if (!confirm('Supprimer ce cocktail ?')) return
  await deleteCocktail(id)
  await load()
}

async function ajouterCategorie() {
  if (!newCategorie.value.trim()) return
  await createCategorie(newCategorie.value.trim())
  newCategorie.value = ''
  await load()
}

async function supprimerCategorie(id: number) {
  if (!confirm('Supprimer cette catégorie ?')) return
  await deleteCategorie(id)
  await load()
}

async function ajouterIngredient() {
  if (!newIngredient.value.trim()) return
  await createIngredient(newIngredient.value.trim())
  newIngredient.value = ''
  await load()
}

async function supprimerIngredient(id: number) {
  if (!confirm('Supprimer cet ingrédient ?')) return
  await deleteIngredient(id)
  await load()
}

function toggleIngredient(id: number) {
  const idx = cocktailForm.value.ingredientIds.indexOf(id)
  if (idx >= 0) {
    cocktailForm.value.ingredientIds.splice(idx, 1)
  } else {
    cocktailForm.value.ingredientIds.push(id)
  }
}
</script>

<template>
  <div class="page">
    <div class="page-header">
      <h1 class="page-title">📋 Gestion de la carte</h1>
      <button class="btn-secondary btn-sm" @click="router.push({ name: 'barmaker-commandes' })">
        ← Commandes
      </button>
    </div>

    <div class="tabs">
      <button class="tab" :class="{ active: activeTab === 'cocktails' }" @click="activeTab = 'cocktails'">Cocktails</button>
      <button class="tab" :class="{ active: activeTab === 'categories' }" @click="activeTab = 'categories'">Catégories</button>
      <button class="tab" :class="{ active: activeTab === 'ingredients' }" @click="activeTab = 'ingredients'">Ingrédients</button>
    </div>

    <!-- COCKTAILS -->
    <div v-if="activeTab === 'cocktails'">
      <div style="display:flex;justify-content:flex-end;margin-bottom:1rem">
        <button class="btn-primary btn-sm" @click="openCreateCocktail">+ Nouveau cocktail</button>
      </div>

      <div v-if="showCocktailForm" class="card form-section">
        <h3 style="margin-bottom:1rem;color:var(--accent)">
          {{ editingCocktail ? 'Modifier ' + editingCocktail.nom : 'Nouveau cocktail' }}
        </h3>

        <div class="form-grid">
          <div class="field">
            <label>Nom *</label>
            <input v-model="cocktailForm.nom" placeholder="Mojito" />
          </div>
          <div class="field">
            <label>Catégorie *</label>
            <select v-model="cocktailForm.categorieId">
              <option value="">— Choisir —</option>
              <option v-for="cat in categories" :key="cat.id" :value="cat.id">{{ cat.nom }}</option>
            </select>
          </div>
          <div class="field full">
            <label>Description</label>
            <textarea v-model="cocktailForm.description" rows="2" placeholder="Description du cocktail"></textarea>
          </div>
          <div class="field full">
            <label>Image URL</label>
            <input v-model="cocktailForm.imageUrl" placeholder="https://..." />
          </div>
        </div>

        <div class="field" style="margin-top:0.75rem">
          <label>Prix par taille</label>
          <div class="prix-fields">
            <div v-for="p in cocktailForm.prix" :key="p.taille" class="prix-field">
              <span class="prix-taille">{{ p.taille }}</span>
              <input v-model="p.prix" type="number" step="0.01" placeholder="0.00" />
              <span>€</span>
            </div>
          </div>
        </div>

        <div class="field" style="margin-top:0.75rem">
          <label>Ingrédients</label>
          <div class="ingredients-checkboxes">
            <label
              v-for="ing in ingredients"
              :key="ing.id"
              class="ing-check"
              :class="{ selected: cocktailForm.ingredientIds.includes(ing.id) }"
            >
              <input
                type="checkbox"
                :checked="cocktailForm.ingredientIds.includes(ing.id)"
                @change="toggleIngredient(ing.id)"
              />
              {{ ing.nom }}
            </label>
          </div>
        </div>

        <p v-if="formError" class="error-msg">{{ formError }}</p>

        <div class="form-actions">
          <button class="btn-primary" @click="saveCocktail">
            {{ editingCocktail ? 'Enregistrer' : 'Créer' }}
          </button>
          <button class="btn-secondary" @click="showCocktailForm = false">Annuler</button>
        </div>
      </div>

      <div class="items-list">
        <div v-for="c in cocktails" :key="c.id" class="item-row card">
          <div class="item-info">
            <span class="item-cat">{{ c.categorie.nom }}</span>
            <strong>{{ c.nom }}</strong>
            <span class="item-prix">
              {{ c.prix.map(p => `${p.taille}:${Number(p.prix).toFixed(2)}€`).join(' · ') }}
            </span>
          </div>
          <div class="item-actions">
            <button class="btn-secondary btn-sm" @click="openEditCocktail(c)">Modifier</button>
            <button class="btn-danger btn-sm" @click="supprimerCocktail(c.id)">Supprimer</button>
          </div>
        </div>
      </div>
    </div>

    <!-- CATEGORIES -->
    <div v-if="activeTab === 'categories'">
      <div class="add-row">
        <input v-model="newCategorie" placeholder="Nom de la catégorie" @keyup.enter="ajouterCategorie" />
        <button class="btn-primary" @click="ajouterCategorie">Ajouter</button>
      </div>
      <div class="items-list">
        <div v-for="cat in categories" :key="cat.id" class="item-row card">
          <span>{{ cat.nom }}</span>
          <button class="btn-danger btn-sm" @click="supprimerCategorie(cat.id)">Supprimer</button>
        </div>
      </div>
    </div>

    <!-- INGREDIENTS -->
    <div v-if="activeTab === 'ingredients'">
      <div class="add-row">
        <input v-model="newIngredient" placeholder="Nom de l'ingrédient" @keyup.enter="ajouterIngredient" />
        <button class="btn-primary" @click="ajouterIngredient">Ajouter</button>
      </div>
      <div class="items-list">
        <div v-for="ing in ingredients" :key="ing.id" class="item-row card">
          <span>{{ ing.nom }}</span>
          <button class="btn-danger btn-sm" @click="supprimerIngredient(ing.id)">Supprimer</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.tabs {
  display: flex;
  gap: 0.25rem;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid var(--border);
  padding-bottom: 0;
}

.tab {
  background: none;
  color: var(--text-muted);
  border-radius: 0;
  padding: 0.5rem 1rem;
  border-bottom: 2px solid transparent;
  font-size: 0.9rem;
}

.tab.active {
  color: var(--accent);
  border-bottom-color: var(--accent);
}

.tab:hover { color: var(--text); }

.form-section {
  margin-bottom: 1.5rem;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 0.75rem;
}

.field { display: flex; flex-direction: column; gap: 0.3rem; }
.field.full { grid-column: 1 / -1; }
.field label { font-size: 0.8rem; color: var(--text-muted); }

.prix-fields {
  display: flex;
  gap: 1rem;
  align-items: center;
}

.prix-field {
  display: flex;
  align-items: center;
  gap: 0.4rem;
}

.prix-taille {
  font-weight: 700;
  color: var(--accent);
  min-width: 16px;
}

.prix-field input {
  width: 80px;
}

.ingredients-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 0.35rem;
  max-height: 160px;
  overflow-y: auto;
}

.ing-check {
  display: flex;
  align-items: center;
  gap: 0.3rem;
  background: var(--bg-input);
  border: 1px solid var(--border);
  border-radius: 20px;
  padding: 0.2rem 0.6rem;
  font-size: 0.82rem;
  cursor: pointer;
  transition: border-color 0.15s;
}

.ing-check.selected {
  border-color: var(--accent);
  background: rgba(212, 160, 23, 0.1);
}

.ing-check input { display: none; }

.form-actions {
  display: flex;
  gap: 0.75rem;
  margin-top: 1rem;
}

.items-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.item-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.8rem 1rem;
}

.item-info {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  flex-wrap: wrap;
}

.item-cat {
  font-size: 0.72rem;
  text-transform: uppercase;
  color: var(--accent);
  font-weight: 600;
}

.item-prix {
  color: var(--text-muted);
  font-size: 0.8rem;
}

.item-actions {
  display: flex;
  gap: 0.5rem;
  flex-shrink: 0;
}

.add-row {
  display: flex;
  gap: 0.75rem;
  margin-bottom: 1rem;
}

.add-row input { flex: 1; }
</style>
