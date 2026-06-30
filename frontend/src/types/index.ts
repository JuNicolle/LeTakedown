export type Role = 'CLIENT' | 'BARMAKER'
export type Taille = 'S' | 'M' | 'L'
export type StatutCommande = 'PANIER' | 'COMMANDEE' | 'EN_COURS' | 'TERMINEE'
export type StatutLigneCommande = 'PREPARATION_INGREDIENTS' | 'ASSEMBLAGE' | 'DRESSAGE' | 'TERMINEE'

export interface UtilisateurResponse {
  id: number
  prenom: string
  role: Role
}

export interface CategorieResponse {
  id: number
  nom: string
}

export interface IngredientResponse {
  id: number
  nom: string
}

export interface CocktailPrixResponse {
  id: number
  taille: Taille
  prix: number
}

export interface CocktailResponse {
  id: number
  nom: string
  description: string
  imageUrl: string | null
  categorie: CategorieResponse
  ingredients: IngredientResponse[]
  prix: CocktailPrixResponse[]
  disponible: boolean
}

export interface LigneCommandeResponse {
  id: number
  cocktailId: number
  cocktailNom: string
  taille: Taille
  prixUnitaire: number
  statut: StatutLigneCommande
}

export interface CommandeResponse {
  id: number
  utilisateurId: number
  utilisateurPrenom: string
  statut: StatutCommande
  dateCreation: string
  dateMiseAJour: string
  lignes: LigneCommandeResponse[]
}
