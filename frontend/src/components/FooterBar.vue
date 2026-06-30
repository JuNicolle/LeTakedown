<template>
  <footer class="footer">
    <div class="footer-inner">

      <!-- BRAND -->
      <div class="footer-brand">
        <div class="footer-mark">B</div>
        <div>
          <div class="footer-name">BURNOUT BAR</div>
          <div class="footer-sub">PIT-LANE COCKTAIL HUB</div>
        </div>
      </div>

      <!-- COLONNES -->
      <div class="footer-cols">

        <!-- NOUS TROUVER -->
        <div class="footer-col">
          <div class="col-title">NOUS TROUVER</div>
          <p class="col-line">42 Rue du Circuit</p>
          <p class="col-line">59000 Lille, France</p>
          <p class="col-line" style="margin-top:8px">Lun–Jeu : 18h–02h</p>
          <p class="col-line">Ven–Sam : 18h–04h</p>
          <p class="col-line">Dim : 20h–00h</p>
        </div>

        <!-- CONTACT -->
        <div class="footer-col">
          <div class="col-title">CONTACT</div>
          <p class="col-line">contact@burnoutbar.fr</p>
          <p class="col-line">+33 3 28 00 42 00</p>
          <p class="col-line" style="margin-top:8px">Réservations groupes :</p>
          <p class="col-line">resa@burnoutbar.fr</p>
        </div>

        <!-- RÉSEAUX -->
        <div class="footer-col">
          <div class="col-title">SUIVEZ-NOUS</div>
          <div class="socials">
            <a class="social-link" href="#" aria-label="Instagram Burnout Bar">
              <span class="social-icon">IG</span>
              <span>@burnoutbar</span>
            </a>
            <a class="social-link" href="#" aria-label="TikTok Burnout Bar">
              <span class="social-icon">TK</span>
              <span>@burnoutbar</span>
            </a>
            <a class="social-link" href="#" aria-label="Facebook Burnout Bar">
              <span class="social-icon">FB</span>
              <span>Burnout Bar Lille</span>
            </a>
            <a class="social-link" href="#" aria-label="X / Twitter Burnout Bar">
              <span class="social-icon">X</span>
              <span>@burnoutbar_fr</span>
            </a>
          </div>
        </div>

        <!-- MENTIONS LÉGALES -->
        <div class="footer-col">
          <div class="col-title">MENTIONS LÉGALES</div>
          <p class="col-line"><span class="col-label">Raison sociale</span>BURNOUT BAR SAS</p>
          <p class="col-line"><span class="col-label">SIRET</span>987 654 321 00042</p>
          <p class="col-line"><span class="col-label">APE</span>5630Z — Débits de boissons</p>
          <p class="col-line"><span class="col-label">Licence</span>IV – Grande licence restaurant</p>
          <p class="col-line"><span class="col-label">Gérant</span>Julien Nicolle</p>
          <p class="col-line"><span class="col-label">Hébergement</span>OVH SAS — Roubaix, France</p>
        </div>

      </div>

      <!-- BARRE BAS -->
      <div class="footer-bottom">
        <div class="footer-legal">
          <span>© {{ year }} Burnout Bar SAS — Tous droits réservés</span>
          <span class="sep">·</span>
          <span>Politique de confidentialité</span>
          <span class="sep">·</span>
          <span>CGU</span>
          <span class="sep">·</span>
          <span>Cookies</span>
        </div>
        <div class="footer-alcool">
          ⚠ L'ABUS D'ALCOOL EST DANGEREUX POUR LA SANTÉ — À CONSOMMER AVEC MODÉRATION
        </div>
      </div>

    </div>
  </footer>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
.footer {
  background: #080810;
  border-top: 2px solid #FF2A1A;
  color: #F2EEE7;
  position: relative;
  z-index: 10;
}

.footer-inner {
  max-width: 1280px; margin: 0 auto;
  padding: 48px 24px 0;
}

/* ── BRAND ── */
.footer-brand {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 40px;
}
.footer-mark {
  width: 34px; height: 34px; background: #FF2A1A;
  display: grid; place-items: center;
  transform: skewX(-9deg);
  font: 900 22px 'Saira'; color: #101015; flex-shrink: 0;
}
.footer-name { font: 900 20px 'Saira'; font-style: italic; letter-spacing: .5px; line-height: .9; }
.footer-sub  { font: 600 9px 'Chakra Petch'; letter-spacing: 3px; color: #FF7A00; margin-top: 3px; }

/* ── COLONNES ── */
.footer-cols {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 36px 24px;
  padding-bottom: 40px;
  border-bottom: 1px solid #1e1e26;
}

.col-title {
  font: 700 10px 'Chakra Petch'; letter-spacing: 3px;
  color: #FF2A1A; margin-bottom: 14px;
}

.col-line {
  font: 500 12px/1.8 'Chakra Petch';
  color: #b8b2a8; /* ~7:1 sur #080810 */
}
.col-label {
  display: block;
  font: 700 9px 'Chakra Petch'; letter-spacing: 1px;
  color: #6f6a61; margin-bottom: 1px;
}

/* ── RÉSEAUX ── */
.socials { display: flex; flex-direction: column; gap: 8px; }
.social-link {
  display: flex; align-items: center; gap: 10px;
  text-decoration: none; color: #b8b2a8;
  font: 500 12px 'Chakra Petch';
  transition: color .15s;
}
.social-link:hover { color: #F2EEE7; }
.social-link:focus-visible { outline: 2px solid #B6FF2E; outline-offset: 3px; }
.social-icon {
  width: 28px; height: 28px;
  background: #1a1a21; border: 1.5px solid #2c2c36;
  display: grid; place-items: center;
  font: 700 10px 'Space Mono'; color: #F2EEE7;
  flex-shrink: 0; transition: border-color .15s, background .15s;
}
.social-link:hover .social-icon { border-color: #FF2A1A; background: #FF2A1A; color: #101015; }

/* ── BARRE BAS ── */
.footer-bottom {
  padding: 20px 0 24px;
  display: flex; flex-direction: column; gap: 10px;
}

.footer-legal {
  display: flex; flex-wrap: wrap; align-items: center; gap: 6px 12px;
  font: 500 11px 'Chakra Petch'; color: #6f6a61;
}
.sep { color: #3a3a46; }

.footer-alcool {
  font: 700 9px 'Chakra Petch'; letter-spacing: 2px;
  color: #FF7A00;
  padding: 8px 12px;
  border-left: 3px solid #FF7A00;
  background: rgba(255,122,0,.06);
}
</style>
