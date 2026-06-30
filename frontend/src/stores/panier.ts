import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { CommandeResponse } from '@/types'

export const usePanierStore = defineStore('panier', () => {
  const panier = ref<CommandeResponse | null>(null)

  const total = computed(() => {
    if (!panier.value) return 0
    return panier.value.lignes.reduce((sum, l) => sum + Number(l.prixUnitaire), 0)
  })

  const nbArticles = computed(() => panier.value?.lignes.length ?? 0)

  function setPanier(c: CommandeResponse) {
    panier.value = c
  }

  function clear() {
    panier.value = null
  }

  return { panier, total, nbArticles, setPanier, clear }
})
