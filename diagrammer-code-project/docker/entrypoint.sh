#!/bin/sh
# Production entrypoint for the Render image.
# The Maven dev profile is not part of the packaged jar. This script still
# refuses the local and test Spring profiles, because those load the fixture
# user whose password is public.
set -eu

if [ -z "${SPRING_PROFILES_ACTIVE:-}" ]; then
  SPRING_PROFILES_ACTIVE=s3
fi

old_ifs=$IFS
IFS=,
for name in $SPRING_PROFILES_ACTIVE; do
  trimmed=$(printf '%s' "$name" | tr -d '[:space:]')
  case $trimmed in
    local|test)
      echo "Refusing to start: Spring profile '${trimmed}' enables the public dev fixture login and is not allowed in this image." >&2
      exit 1
      ;;
  esac
done
IFS=$old_ifs

export SPRING_PROFILES_ACTIVE
exec java -jar /app/app.jar
