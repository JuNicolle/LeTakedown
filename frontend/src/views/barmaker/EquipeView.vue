<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { registerBarmaker } from '@/api/utilisateur'

const router = useRouter()

const prenom = ref('')
const motDePasse = ref('')
const confirm = ref('')
const error = ref('')
const success = ref('')
const loading = ref(false)

async function submit() {
  error.value = ''; success.value = ''
  if (!prenom.value.trim() || !motDePasse.value) { error.value = 'Tous les champs sont requis'; return }
  if (motDePasse.value.length < 6) { error.value = 'Le mot de passe doit faire au moins 6 caractères'; return }
  if (motDePasse.value !== confirm.value) { error.value = 'Les mots de passe ne correspondent pas'; return }
  loading.value = true
  try {
    await registerBarmaker(prenom.value.trim(), motDePasse.value)
    success.value = `Compte créé pour ${prenom.value.trim()}`
    prenom.value = ''; motDePasse.value = ''; confirm.value = ''
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
      <div class="topbar-brand" style="cursor:pointer" @click="router.push({ name: 'barmaker-commandes' })">
        <div class="topbar-mark">B</div>
        <div>
          <div class="topbar-name">ÉQUIPE</div>
          <div class="topbar-sub">PIT CONTROL — GESTION</div>
        </div>
      </div>
      <div class="topbar-spacer" />
      <button class="topbar-pill" @click="router.push({ name: 'barmaker-carte' })">
        <span>LA CARTE</span>
      </button>
      <button class="topbar-pill" @click="router.push({ name: 'barmaker-commandes' })">
        <span>← COMMANDES</span>
      </button>
    </div>

    <div class="hero">
      <div class="hero-inner">
        <div>
          <div class="hero-eyebrow"><span class="hero-blink" />GESTION DE L'ÉQUIPE&nbsp;&nbsp;//&nbsp;&nbsp;ADMIN</div>
          <h1 class="hero-h1">PIT<br/>CREW.</h1>
        </div>
      </div>
    </div>

    <div class="shell">
      <div class="panel equipe-card">
        <div class="panel-head"><span class="panel-title">CRÉER UN COMPTE BARMAKER</span></div>
        <div class="panel-body">
          <form @submit.prevent="submit">
            <div class="field" style="margin-bottom:1rem">
              <label>PRÉNOM</label>
              <input v-model="prenom" type="text" placeholder="Prénom du barmaker" autofocus :disabled="loading" />
            </div>
            <div class="field" style="margin-bottom:1rem">
              <label>MOT DE PASSE</label>
              <input v-model="motDePasse" type="password" placeholder="••••••••" :disabled="loading" />
            </div>
            <div class="field" style="margin-bottom:1.25rem">
              <label>CONFIRMER LE MOT DE PASSE</label>
              <input v-model="confirm" type="password" placeholder="••••••••" :disabled="loading" />
            </div>
            <p v-if="error" class="err" style="margin-bottom:.75rem">{{ error }}</p>
            <p v-if="success" class="ok" style="margin-bottom:.75rem">✓ {{ success }}</p>
            <button type="submit" class="btn-launch" :disabled="loading">
              {{ loading ? 'CRÉATION...' : '⟫ CRÉER LE COMPTE' }}
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.shell { display: flex; align-items: center; justify-content: center; padding: 40px 24px; min-height: calc(100vh - 58px - 120px); }
.equipe-card { width: 100%; max-width: 440px; border-top-color: #19C2FF; }
.ok { font: 700 11px 'Chakra Petch'; letter-spacing: 1px; color: #B6FF2E; }
</style>
