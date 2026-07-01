import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { Role } from '@/types'
import { setToken } from '@/api/http'

const LS_KEY = 'barapp_user'

interface AuthUser {
  id: number
  prenom: string
  role: Role
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref<AuthUser | null>(loadFromStorage())

  const isLoggedIn = computed(() => user.value !== null)
  const isClient = computed(() => user.value?.role === 'CLIENT')
  const isBarmaker = computed(() => user.value?.role === 'BARMAKER')

  function setUser(u: AuthUser & { token?: string }) {
    const { token, ...userData } = u
    user.value = userData
    localStorage.setItem(LS_KEY, JSON.stringify(userData))
    if (token) setToken(token)
  }

  function logout() {
    user.value = null
    localStorage.removeItem(LS_KEY)
    setToken(null)
  }

  return { user, isLoggedIn, isClient, isBarmaker, setUser, logout }
})

function loadFromStorage(): AuthUser | null {
  try {
    const raw = localStorage.getItem(LS_KEY)
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}
