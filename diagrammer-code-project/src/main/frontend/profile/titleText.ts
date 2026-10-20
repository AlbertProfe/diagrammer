export const TITLE_MIN = 3;
export const TITLE_MAX = 80;

/** Trimmed title of 3 to 80 characters, or undefined when the field is not a set title. */
export function acceptedTitle(raw: string): string | undefined {
  const trimmed = raw.trim();
  if (trimmed.length < TITLE_MIN || trimmed.length > TITLE_MAX) {
    return undefined;
  }
  return trimmed;
}

/**
 * Length errors show as soon as the trimmed text is non-empty and out of range.
 * An empty field stays quiet until the user has typed in it and left it.
 */
export function titleError(
  raw: string,
  typed: boolean,
  blurred: boolean,
  tooShortOrLong: string,
  empty: string,
): string {
  const trimmed = raw.trim();
  if (trimmed.length === 0) {
    return typed && blurred ? empty : '';
  }
  if (trimmed.length < TITLE_MIN || trimmed.length > TITLE_MAX) {
    return tooShortOrLong;
  }
  return '';
}
