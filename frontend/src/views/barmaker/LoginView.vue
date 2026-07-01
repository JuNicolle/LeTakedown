<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { loginBarmaker, registerBarmaker } from '@/api/utilisateur'

const router = useRouter()
const auth = useAuthStore()

const mode = ref<'login' | 'register'>('login')
const prenom = ref('')
const motDePasse = ref('')
const motDePasseConfirm = ref('')
const error = ref('')
const loading = ref(false)

function switchMode(m: 'login' | 'register') {
  mode.value = m
  error.value = ''
  prenom.value = ''
  motDePasse.value = ''
  motDePasseConfirm.value = ''
}

async function submit() {
  error.value = ''
  if (!prenom.value.trim() || !motDePasse.value) {
    error.value = 'Tous les champs sont requis'
    return
  }
  if (mode.value === 'register') {
    if (motDePasse.value.length < 6) {
      error.value = 'Le mot de passe doit faire au moins 6 caractères'
      return
    }
    if (motDePasse.value !== motDePasseConfirm.value) {
      error.value = 'Les mots de passe ne correspondent pas'
      return
    }
  }
  loading.value = true
  try {
    const fn = mode.value === 'login' ? loginBarmaker : registerBarmaker
    auth.setUser(await fn(prenom.value.trim(), motDePasse.value))
    router.push({ name: 'barmaker-commandes' })
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Une erreur est survenue'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="page-dark">
    <div class="topbar">
      <div class="topbar-brand" style="cursor:pointer" @click="router.push({ name: 'home' })">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">BURNOUT BAR</div>
          <div class="topbar-sub">PIT CONTROL — BARMAKER</div>
        </div>
      </div>
    </div>

    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />ACCÈS RESTREINT&nbsp;&nbsp;//&nbsp;&nbsp;BARMAKERS UNIQUEMENT</div>
          <h1 class="hero-h1">PIT<br/>CONTROL</h1>
        </div>
      </div>
    </div>

    <div class="login-shell">
      <div class="panel login-card">
        <div class="panel-head mode-tabs">
          <button
            class="mode-tab" :class="{ active: mode === 'login' }"
            @click="switchMode('login')"
          >CONNEXION</button>
          <button
            class="mode-tab" :class="{ active: mode === 'register' }"
            @click="switchMode('register')"
          >CRÉER UN COMPTE</button>
        </div>
        <div class="panel-body">
          <form @submit.prevent="submit">
            <div class="field" style="margin-bottom:1rem">
              <label>PRÉNOM</label>
              <input v-model="prenom" type="text" placeholder="BurnoutBar" autofocus :disabled="loading" />
            </div>
            <div class="field" style="margin-bottom:1rem">
              <label>MOT DE PASSE</label>
              <input v-model="motDePasse" type="password" placeholder="••••••••" :disabled="loading" />
            </div>
            <div v-if="mode === 'register'" class="field" style="margin-bottom:1.25rem">
              <label>CONFIRMER LE MOT DE PASSE</label>
              <input v-model="motDePasseConfirm" type="password" placeholder="••••••••" :disabled="loading" />
            </div>
            <div v-else style="margin-bottom:1.25rem" />
            <p v-if="error" class="err" style="margin-bottom:.75rem">{{ error }}</p>
            <button type="submit" class="btn-launch" :disabled="loading">
              {{ loading ? '...' : mode === 'login' ? '⟫ ACCÉDER AU PIT' : '⟫ CRÉER LE COMPTE' }}
            </button>
          </form>
          <button class="back-btn" @click="$router.push({ name: 'home' })">← RETOUR À L'ACCUEIL</button>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.login-shell { display: flex; align-items: center; justify-content: center; padding: 40px 24px; min-height: calc(100vh - 58px - 120px); }
.login-card { width: 100%; max-width: 400px; }

.mode-tabs { display: flex; gap: 0; padding: 0; }
.mode-tab {
  flex: 1;
  background: transparent;
  border: none;
  border-bottom: 2px solid transparent;
  color: #6f6a61;
  font: 700 10px 'Chakra Petch';
  letter-spacing: 2px;
  padding: 14px 10px;
  cursor: pointer;
  transition: all .15s;
}
.mode-tab:hover { color: #F2EEE7; }
.mode-tab.active { color: #FF2A1A; border-bottom-color: #FF2A1A; }

.back-btn { background: transparent; color: #6f6a61; font: 700 10px 'Chakra Petch'; letter-spacing: 1.5px; padding: 0; margin-top: 16px; border: none; cursor: pointer; }
.back-btn:hover { color: #F2EEE7; }
</style>
