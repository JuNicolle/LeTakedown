import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue'),
    },
    {
      path: '/client',
      name: 'onboarding',
      component: () => import('@/views/client/OnboardingView.vue'),
    },
    {
      path: '/client/carte',
      name: 'carte',
      component: () => import('@/views/client/CarteView.vue'),
      meta: { requiresClient: true },
    },
    {
      path: '/client/panier',
      name: 'panier',
      component: () => import('@/views/client/PanierView.vue'),
      meta: { requiresClient: true },
    },
    {
      path: '/client/suivi/:commandeId',
      name: 'suivi',
      component: () => import('@/views/client/SuiviView.vue'),
      meta: { requiresClient: true },
    },
    {
      path: '/barmaker/login',
      name: 'barmaker-login',
      component: () => import('@/views/barmaker/LoginView.vue'),
    },
    {
      path: '/barmaker/commandes',
      name: 'barmaker-commandes',
      component: () => import('@/views/barmaker/CommandesView.vue'),
      meta: { requiresBarmaker: true },
    },
    {
      path: '/barmaker/commande/:id',
      name: 'barmaker-commande-detail',
      component: () => import('@/views/barmaker/CommandeDetailView.vue'),
      meta: { requiresBarmaker: true },
    },
    {
      path: '/barmaker/carte',
      name: 'barmaker-carte',
      component: () => import('@/views/barmaker/CarteAdminView.vue'),
      meta: { requiresBarmaker: true },
    },
    {
      path: '/barmaker/equipe',
      name: 'barmaker-equipe',
      component: () => import('@/views/barmaker/EquipeView.vue'),
      meta: { requiresBarmaker: true },
    },
  ],
})

router.beforeEach((to) => {
  const auth = useAuthStore()

  if (to.meta.requiresClient && !auth.isClient) {
    return { name: 'onboarding' }
  }
  if (to.meta.requiresBarmaker && !auth.isBarmaker) {
    return { name: 'barmaker-login' }
  }
})

export default router
