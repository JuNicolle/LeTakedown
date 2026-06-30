const PALETTE = ['#FF2A1A', '#FF7A00', '#FF1FA0', '#B6FF2E', '#19C2FF', '#FFC400', '#9B6DFF']

export function cocktailColor(categorieId: number): string {
  return PALETTE[(categorieId - 1) % PALETTE.length]!
}
