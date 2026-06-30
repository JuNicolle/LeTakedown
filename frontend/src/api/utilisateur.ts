import type { UtilisateurResponse } from '@/types'

async function post<T>(url: string, body: unknown): Promise<T> {
  const res = await fetch(url, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  })
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.error ?? `Erreur ${res.status}`)
  }
  return res.json()
}

export function rejoindreCommeClient(prenom: string): Promise<UtilisateurResponse> {
  return post('/api/utilisateurs/client', { prenom })
}

export function loginBarmaker(prenom: string, motDePasse: string): Promise<UtilisateurResponse> {
  return post('/api/utilisateurs/barmaker/login', { prenom, motDePasse })
}
