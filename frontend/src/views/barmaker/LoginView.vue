<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { loginBarmaker } from '@/api/utilisateur'

const router = useRouter()
const auth = useAuthStore()
const prenom = ref('')
const motDePasse = ref('')
const error = ref('')
const loading = ref(false)

async function submit() {
  if (!prenom.value.trim() || !motDePasse.value) { error.value = 'Tous les champs sont requis'; return }
  loading.value = true; error.value = ''
  try {
    auth.setUser(await loginBarmaker(prenom.value.trim(), motDePasse.value))
    router.push({ name: 'barmaker-commandes' })
  } catch (e: unknown) {
    error.value = e instanceof Error ? e.message : 'Identifiants incorrects'
  } finally { loading.value = false }
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
        <div class="panel-head"><span class="panel-title">IDENTIFICATION</span></div>
        <div class="panel-body">
          <form @submit.prevent="submit">
            <div class="field" style="margin-bottom:1rem">
              <label>PRÉNOM</label>
              <input v-model="prenom" type="text" placeholder="BurnoutBar" autofocus :disabled="loading" />
            </div>
            <div class="field" style="margin-bottom:1.25rem">
              <label>MOT DE PASSE</label>
              <input v-model="motDePasse" type="password" placeholder="••••••••" :disabled="loading" />
            </div>
            <p v-if="error" class="err" style="margin-bottom:.75rem">{{ error }}</p>
            <button type="submit" class="btn-launch" :disabled="loading">
              {{ loading ? 'CONNEXION...' : '⟫ ACCÉDER AU PIT' }}
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
.back-btn { background: transparent; color: #6f6a61; font: 700 10px 'Chakra Petch'; letter-spacing: 1.5px; padding: 0; margin-top: 16px; border: none; cursor: pointer; }
.back-btn:hover { color: #F2EEE7; }
</style>
