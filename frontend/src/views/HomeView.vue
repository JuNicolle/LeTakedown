<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

function goClient() {
  router.push(auth.isClient ? { name: 'carte' } : { name: 'onboarding' })
}
function goBarmaker() {
  router.push(auth.isBarmaker ? { name: 'barmaker-commandes' } : { name: 'barmaker-login' })
}
</script>

<template>
  <div class="home">
    <!-- Decor -->
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
    <div class="choices-wrap">
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
  </div>
</template>

<style scoped>
.home { min-height: 100vh; background: #F2EEE7; position: relative; overflow: hidden; }

.decor { position: absolute; inset: 0; pointer-events: none; overflow: hidden; }
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

.choices-wrap { position: relative; z-index: 1; max-width: 1280px; margin: 0 auto; padding: 40px 24px 80px; }

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

.choice-tag {
  display: inline-block; font: 700 10px 'Chakra Petch'; letter-spacing: 2px;
  color: #fff; padding: 3px 9px; margin-bottom: 10px;
}

.choice-name { font: 900 clamp(36px,5vw,52px) 'Saira'; font-style: italic; letter-spacing: -2px; line-height: .85; margin-bottom: 14px; }

.choice-desc { font: 500 13px/1.5 'Chakra Petch'; color: #5a564d; margin-bottom: 20px; }

.choice-foot { border-top: 1.5px solid #16161B; }

.choice-cta {
  background: #16161B; color: #F2EEE7;
  font: 700 13px 'Chakra Petch'; letter-spacing: 2px;
  padding: 16px 20px; transition: background .15s;
}
.choice:hover .choice-cta { background: #FF2A1A; color: #101015; }
</style>
