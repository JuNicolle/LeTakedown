<script setup lang="ts">
import { computed, ref } from 'vue'
import type { CocktailResponse, Taille } from '@/types'
import { cocktailColor } from '@/types/colors'

const props = defineProps<{
  cocktail: CocktailResponse
  num: number
}>()
const emit = defineEmits<{
  (e: 'click'): void
  (e: 'add', taille: Taille): void
}>()

const color = computed(() => cocktailColor(props.cocktail.categorie.id))
const selecting = ref(false)

const ING_PALETTE = [
  { bg: '#FFE8E6', text: '#7A1000' },
  { bg: '#FFF3E0', text: '#7A3D00' },
  { bg: '#F1F8E9', text: '#2E5900' },
  { bg: '#E3F2FD', text: '#0A3D6B' },
  { bg: '#EDE7F6', text: '#4A0080' },
  { bg: '#E0F7FA', text: '#004D5C' },
  { bg: '#FCE4EC', text: '#7A003C' },
]
function ingStyle(id: number) {
  const p = ING_PALETTE[id % ING_PALETTE.length]!
  return { background: p.bg, color: p.text, borderColor: p.bg }
}

const TAILLE_ORDER: Taille[] = ['S', 'M', 'L']
const prixSorted = computed(() =>
  [...props.cocktail.prix].sort(
    (a, b) => TAILLE_ORDER.indexOf(a.taille) - TAILLE_ORDER.indexOf(b.taille),
  ),
)

const numStr = computed(() => String(props.num).padStart(2, '0'))

function handleChoisir() {
  if (prixSorted.value.length === 1) {
    emit('add', prixSorted.value[0]!.taille)
  } else {
    selecting.value = true
  }
}

function pickTaille(taille: Taille) {
  selecting.value = false
  emit('add', taille)
}
</script>

<template>
  <div class="card">
    <div class="accent" :style="{ background: color }" />
    <div class="body" @click="emit('click')">
      <div class="top">
        <span class="num">{{ numStr }}</span>
        <span class="tag" :style="{ background: color }">{{ cocktail.categorie.nom }}</span>
      </div>
      <div class="name">{{ cocktail.nom }}</div>
      <p class="desc">{{ cocktail.description }}</p>

      <div class="ings">
        <span v-for="ing in cocktail.ingredients" :key="ing.id" class="ing" :style="ingStyle(ing.id)">{{ ing.nom }}</span>
      </div>

      <div class="prix-row">
        <div v-for="p in prixSorted" :key="p.id" class="prix-chip">
          <span class="taille">{{ p.taille }}</span>
          <span class="prix">{{ Number(p.prix).toFixed(2) }}€</span>
        </div>
      </div>
    </div>

    <div class="foot" @click.stop>
      <template v-if="selecting">
        <button
          v-for="p in prixSorted" :key="p.taille"
          class="size-btn"
          @click="pickTaille(p.taille)"
        >
          {{ p.taille }} — {{ Number(p.prix).toFixed(2) }}€
        </button>
        <button class="cancel-btn" @click="selecting = false">✕</button>
      </template>
      <template v-else>
        <button
          class="cta"
          :aria-label="`Choisir ${cocktail.nom}`"
          @click="handleChoisir"
        >
          CHOISIR →
        </button>
      </template>
    </div>
  </div>
</template>

<style scoped>
.card {
  background: #fff;
  border: 1.5px solid #16161B;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  transition: transform .15s, box-shadow .15s;
}
.card:hover { transform: translateY(-3px); box-shadow: 0 12px 30px rgba(255,42,26,.18); }

.accent { height: 8px; flex-shrink: 0; }

.body { padding: 14px 16px 0; flex: 1; cursor: pointer; }

.top { display: flex; align-items: flex-start; justify-content: space-between; margin-bottom: 8px; }

.num { font: 700 28px 'Space Mono'; color: rgba(22,22,27,.12); line-height: .8; }

.tag {
  font: 700 9px 'Chakra Petch'; letter-spacing: 2px;
  color: #fff; padding: 3px 8px; align-self: flex-start;
}

.name { font: 900 24px 'Saira'; font-style: italic; letter-spacing: -1px; line-height: 1; margin-bottom: 8px; }

.desc {
  font: 500 12px/1.4 'Chakra Petch'; color: #5a564d;
  margin: 0 0 10px; min-height: 48px;
  display: -webkit-box; -webkit-line-clamp: 3; -webkit-box-orient: vertical; overflow: hidden;
}

.ings { display: flex; flex-wrap: wrap; gap: 4px; margin-bottom: 12px; }
.ing {
  font: 700 9px 'Chakra Petch'; letter-spacing: 1px;
  border: 1px solid transparent; padding: 2px 6px;
}

.prix-row { display: flex; gap: 6px; margin-bottom: 14px; flex-wrap: wrap; }
.prix-chip { display: flex; align-items: center; gap: 4px; border: 1px solid #d0cbc2; padding: 2px 7px; }
.taille { font: 700 9px 'Chakra Petch'; letter-spacing: 1px; color: #8a857a; }
.prix { font: 700 12px 'Space Mono'; color: #16161B; }

.foot {
  margin-top: auto;
  display: flex; align-items: stretch;
  border-top: 1.5px solid #16161B;
  min-height: 46px;
}

.cta {
  flex: 1; width: 100%;
  background: #FF2A1A; color: #101015;
  border: none; outline: none;
  font: 900 12px 'Chakra Petch'; letter-spacing: 3px;
  display: grid; place-items: center;
  padding: 14px; cursor: pointer;
  transition: background .15s;
}
.cta:hover { background: #d41f10; color: #fff; }
.cta:focus-visible {
  outline: 3px solid #B6FF2E;
  outline-offset: -3px;
}

.size-btn {
  flex: 1; background: #FF2A1A; color: #101015;
  font: 700 10px 'Chakra Petch'; letter-spacing: 1px;
  border: none; border-right: 1px solid #16161B;
  cursor: pointer; padding: 10px 4px;
  transition: background .1s;
}
.size-btn:hover { background: #d41f10; color: #fff; }
.size-btn:focus-visible { outline: 3px solid #B6FF2E; outline-offset: -3px; }

.cancel-btn {
  background: #16161B; color: #6f6a61;
  font: 700 11px 'Space Mono';
  border: none; cursor: pointer; padding: 10px 12px;
  transition: color .1s;
}
.cancel-btn:hover { color: #F2EEE7; }
.cancel-btn:focus-visible { outline: 3px solid #B6FF2E; outline-offset: -3px; }
</style>
