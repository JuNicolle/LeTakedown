import type { CocktailResponse, CategorieResponse, IngredientResponse } from '@/types'

async function request<T>(url: string, options?: RequestInit): Promise<T> {
  const res = await fetch(url, options)
  if (!res.ok) {
    const err = await res.json().catch(() => ({}))
    throw new Error(err.error ?? `Erreur ${res.status}`)
  }
  if (res.status === 204) return undefined as T
  return res.json()
}

export function getCocktails(): Promise<CocktailResponse[]> {
  return request('/api/cocktails')
}

export function getCocktail(id: number): Promise<CocktailResponse> {
  return request(`/api/cocktails/${id}`)
}

export function createCocktail(data: unknown): Promise<CocktailResponse> {
  return request('/api/cocktails', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
}

export function updateCocktail(id: number, data: unknown): Promise<CocktailResponse> {
  return request(`/api/cocktails/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(data),
  })
}

export function toggleDisponibilite(id: number): Promise<CocktailResponse> {
  return request(`/api/cocktails/${id}/disponibilite`, { method: 'PATCH' })
}

export function deleteCocktail(id: number): Promise<void> {
  return request(`/api/cocktails/${id}`, { method: 'DELETE' })
}

export function getCategories(): Promise<CategorieResponse[]> {
  return request('/api/categories')
}

export function createCategorie(nom: string): Promise<CategorieResponse> {
  return request('/api/categories', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nom }),
  })
}

export function deleteCategorie(id: number): Promise<void> {
  return request(`/api/categories/${id}`, { method: 'DELETE' })
}

export function getIngredients(): Promise<IngredientResponse[]> {
  return request('/api/ingredients')
}

export function createIngredient(nom: string): Promise<IngredientResponse> {
  return request('/api/ingredients', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ nom }),
  })
}

export function deleteIngredient(id: number): Promise<void> {
  return request(`/api/ingredients/${id}`, { method: 'DELETE' })
}
