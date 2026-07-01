import type { CocktailResponse, CategorieResponse, IngredientResponse } from '@/types'
import { http } from '@/api/http'

export function getCocktails(): Promise<CocktailResponse[]> {
  return http('/api/cocktails')
}

export function getCocktail(id: number): Promise<CocktailResponse> {
  return http(`/api/cocktails/${id}`)
}

export function createCocktail(data: unknown): Promise<CocktailResponse> {
  return http('/api/cocktails', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
}

export function updateCocktail(id: number, data: unknown): Promise<CocktailResponse> {
  return http(`/api/cocktails/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
}

export function toggleDisponibilite(id: number): Promise<CocktailResponse> {
  return http(`/api/cocktails/${id}/disponibilite`, { method: 'PATCH' })
}

export function deleteCocktail(id: number): Promise<void> {
  return http(`/api/cocktails/${id}`, { method: 'DELETE' })
}

export function getCategories(): Promise<CategorieResponse[]> {
  return http('/api/categories')
}

export function createCategorie(nom: string): Promise<CategorieResponse> {
  return http('/api/categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nom }),
  })
}

export function deleteCategorie(id: number): Promise<void> {
  return http(`/api/categories/${id}`, { method: 'DELETE' })
}

export function getIngredients(): Promise<IngredientResponse[]> {
  return http('/api/ingredients')
}

export function createIngredient(nom: string): Promise<IngredientResponse> {
  return http('/api/ingredients', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nom }),
  })
}

export function deleteIngredient(id: number): Promise<void> {
  return http(`/api/ingredients/${id}`, { method: 'DELETE' })
}
