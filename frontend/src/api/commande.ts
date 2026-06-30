import type { CommandeResponse, Taille } from '@/types'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options)
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.error ?? `Erreur ${res.status}`)
  }
  return res.json()
}

export function getPanier(utilisateurId: number): Promise<CommandeResponse> {
  return request(`/api/commandes/panier/${utilisateurId}`)
}

export function ajouterAuPanier(utilisateurId: number, cocktailId: number, taille: Taille): Promise<CommandeResponse> {
  return request(`/api/commandes/panier/${utilisateurId}/lignes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ cocktailId, taille }),
  })
}

export function retirerDuPanier(utilisateurId: number, ligneId: number): Promise<CommandeResponse> {
  return request(`/api/commandes/panier/${utilisateurId}/lignes/${ligneId}`, { method: 'DELETE' })
}

export function lancerCommande(utilisateurId: number): Promise<CommandeResponse> {
  return request(`/api/commandes/panier/${utilisateurId}/lancer`, { method: 'POST' })
}

export function getCommandesClient(utilisateurId: number): Promise<CommandeResponse[]> {
  return request(`/api/commandes/client/${utilisateurId}`)
}

export function getCommande(id: number): Promise<CommandeResponse> {
  return request(`/api/commandes/${id}`)
}

export function getCommandesBarmaker(): Promise<CommandeResponse[]> {
  return request('/api/commandes')
}

export function avancerLigne(commandeId: number, ligneId: number): Promise<CommandeResponse> {
  return request(`/api/commandes/${commandeId}/lignes/${ligneId}/avancer`, { method: 'PATCH' })
}
