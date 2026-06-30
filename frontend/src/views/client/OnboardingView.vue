<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { rejoindreCommeClient } from '@/api/utilisateur'

const router = useRouter()
const auth = useAuthStore()

const prenom = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  if (!prenom.value.trim()) { error.value = 'Prénom requis'; return }
  loading.value = true
  error.value = ''
  try {
    auth.setUser(await rejoindreCommeClient(prenom.value.trim()))
    router.push({ name: 'carte' })
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Erreur'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-dark">
    <div class="topbar">
      <div class="topbar-brand">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">BURNOUT BAR</div>
          <div class="topbar-sub">PIT-LANE COCKTAIL HUB</div>
        </div>
      </div>
    </div>

    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />IDENTIFICATION PILOTE</div>
          <h1 class="hero-h1">IDENTIFIE TOI</h1>
        </div>
      </div>
    </div>

    <div class="form-shell">
      <div class="form-card">
        <p class="form-hint">// ENTREZ VOTRE PRÉNOM POUR REJOINDRE LE BAR</p>

        <form @submit.prevent="submit">
          <div class="field" style="margin-bottom:1.25rem">
            <label>PRÉNOM</label>
            <input v-model="prenom" type="text" placeholder="Ex : ALEX" autofocus :disabled="loading" />
            <p v-if="error" class="err">{{ error }}</p>
          </div>
          <button type="submit" class="btn-launch" :disabled="loading">
            {{ loading ? 'CHARGEMENT...' : '⟫ ENTRER DANS LE BAR' }}
          </button>
        </form>

        <button class="back-btn" @click="$router.push({ name: 'home' })">← RETOUR</button>
      </div>
    </div>
  </div>
</template>

<style scoped>
.form-shell {
  display: flex; align-items: center; justify-content: center;
  min-height: calc(100vh - 58px - 120px);
  padding: 40px 24px;
}

.form-card {
  width: 100%; max-width: 420px;
  background: #1a1a21; border: 1.5px solid #2c2c36; border-top: 3px solid #FF2A1A;
  padding: 28px;
}

.form-hint {
  font: 700 10px 'Chakra Petch'; letter-spacing: 2px; color: #6f6a61;
  margin-bottom: 24px;
}

.back-btn {
  background: transparent; color: #6f6a61;
  font: 700 10px 'Chakra Petch'; letter-spacing: 1.5px;
  padding: 0; margin-top: 16px; border: none; cursor: pointer;
}
.back-btn:hover { color: #F2EEE7; }
</style>
