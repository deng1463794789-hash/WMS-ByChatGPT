export function isValidUsername(str: string): boolean {
  return str.trim().length >= 2
}

export function isValidPassword(str: string): boolean {
  return str.length >= 6
}

export function isExternal(path: string): boolean {
  return /^(https?:|mailto:|tel:)/.test(path)
}

export function isValidPhone(str: string): boolean {
  return /^1[3-9]\d{9}$/.test(str)
}
