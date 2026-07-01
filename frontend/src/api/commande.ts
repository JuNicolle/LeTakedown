import type { CommandeResponse, Taille } from '@/types'
import { http } from '@/api/http'

export function getPanier(utilisateurId: number): Promise<CommandeResponse> {
  return http(`/api/commandes/panier/${utilisateurId}`)
}

export function ajouterAuPanier(utilisateurId: number, cocktailId: number, taille: Taille): Promise<CommandeResponse> {
  return http(`/api/commandes/panier/${utilisateurId}/lignes`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ cocktailId, taille }),
  })
}

export function retirerDuPanier(utilisateurId: number, ligneId: number): Promise<CommandeResponse> {
  return http(`/api/commandes/panier/${utilisateurId}/lignes/${ligneId}`, { method: 'DELETE' })
}

export function lancerCommande(utilisateurId: number): Promise<CommandeResponse> {
  return http(`/api/commandes/panier/${utilisateurId}/lancer`, { method: 'POST' })
}

export function getCommandesClient(utilisateurId: number): Promise<CommandeResponse[]> {
  return http(`/api/commandes/client/${utilisateurId}`)
}

export function getCommande(id: number): Promise<CommandeResponse> {
  return http(`/api/commandes/${id}`)
}

export function getCommandesBarmaker(): Promise<CommandeResponse[]> {
  return http('/api/commandes')
}

export function avancerLigne(commandeId: number, ligneId: number): Promise<CommandeResponse> {
  return http(`/api/commandes/${commandeId}/lignes/${ligneId}/avancer`, { method: 'PATCH' })
}
