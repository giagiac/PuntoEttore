#!/bin/bash

# ==============================================================================
# Script per l'incremento della versione e del numero di build di un'app iOS.
#
# AUTORE: Gemini
# VERSIONE: 1.0
#
# DESCRIZIONE:
# Questo script automatizza il processo di aggiornamento della versione di
# marketing (es. 1.2.3) e del numero di build (es. 15) di un progetto Xcode.
# Utilizza lo strumento `agvtool` fornito da Apple.
#
# PRE-REQUISITI:
# 1. Esegui questo script dalla directory principale del tuo progetto Xcode
#    (la stessa dove si trova il file .xcodeproj o .xcworkspace).
# 2. Assicurati che il versioning sia configurato correttamente in Xcode:
#    - Vai su "Build Settings" del tuo target.
#    - Cerca "Versioning".
#    - Imposta "Versioning System" su "Apple Generic".
#    - Assicurati che "Current Project Version" (per il build number) e
#      "Marketing Version" (per la versione pubblica) siano impostati.
#
# USO:
# ./increment_version.sh
# ==============================================================================

# Colori per un output più leggibile
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}--- Script di Incremento Versione iOS ---${NC}"

# --- VERIFICA PRE-REQUISITI ---
if ! command -v agvtool &> /dev/null; then
    echo -e "${YELLOW}ATTENZIONE: 'agvtool' non trovato. Assicurati che gli strumenti a riga di comando di Xcode siano installati.${NC}"
    exit 1
fi

# Trova il file di progetto per verificare di essere nella directory giusta
PROJECT_FILE=$(find . -maxdepth 1 -name "*.xcodeproj" -print -quit)
if [ -z "$PROJECT_FILE" ]; then
    echo -e "${YELLOW}ATTENZIONE: Nessun file .xcodeproj trovato nella directory corrente.${NC}"
    echo "Assicurati di eseguire lo script dalla root del tuo progetto."
    exit 1
fi
echo "Trovato progetto: $(basename "$PROJECT_FILE")"

# --- LEGGI VERSIONI ATTUALI ---
CURRENT_MARKETING_VERSION=$(agvtool what-marketing-version -terse1)
CURRENT_BUILD_NUMBER=$(agvtool what-version -terse)

echo -e "\nVersione attuale: ${GREEN}${CURRENT_MARKETING_VERSION} (${CURRENT_BUILD_NUMBER})${NC}"

# --- CHIEDI QUALE PARTE INCREMENTARE ---
echo -e "\n${YELLOW}Quale parte della versione vuoi incrementare?${NC}"
PS3="Scegli un'opzione: "
options=("Patch (es. 1.2.3 -> 1.2.4)" "Minor (es. 1.2.3 -> 1.3.0)" "Major (es. 1.2.3 -> 2.0.0)" "Annulla")
select opt in "${options[@]}"
do
    case $opt in
        "Patch (es. 1.2.3 -> 1.2.4)")
            VERSION_PART="patch"
            break
            ;;
        "Minor (es. 1.2.3 -> 1.3.0)")
            VERSION_PART="minor"
            break
            ;;
        "Major (es. 1.2.3 -> 2.0.0)")
            VERSION_PART="major"
            break
            ;;
        "Annulla")
            echo "Operazione annullata."
            exit 0
            ;;
        *) echo "Opzione non valida $REPLY";;
    esac
done

# --- CALCOLA NUOVA VERSIONE DI MARKETING ---
IFS='.' read -r -a version_components <<< "$CURRENT_MARKETING_VERSION"
major=${version_components[0]}
minor=${version_components[1]:-0}
patch=${version_components[2]:-0}

case $VERSION_PART in
    "patch")
        patch=$((patch + 1))
        ;;
    "minor")
        minor=$((minor + 1))
        patch=0
        ;;
    "major")
        major=$((major + 1))
        minor=0
        patch=0
        ;;
esac

NEW_MARKETING_VERSION="$major.$minor.$patch"

# --- AGGIORNA LE VERSIONI ---
echo -e "\nAggiornamento versione a ${GREEN}${NEW_MARKETING_VERSION}${NC}..."

# Aggiorna la versione di marketing
agvtool new-marketing-version "$NEW_MARKETING_VERSION"

# Incrementa il numero di build
echo "Incremento del numero di build..."
agvtool next-version -all

NEW_BUILD_NUMBER=$(agvtool what-version -terse)

echo -e "\n${GREEN}OPERAZIONE COMPLETATA!${NC}"
echo -e "Nuova versione: ${GREEN}${NEW_MARKETING_VERSION} (${NEW_BUILD_NUMBER})${NC}"

# # --- INTEGRAZIONE CON GIT (OPZIONALE) ---
# read -p $'\nVuoi creare un commit e un tag Git per questa versione? (s/n) ' -n 1 -r
# echo
# if [[ $REPLY =~ ^[Ss]$ ]]; then
#     echo "Creazione del commit Git..."
#     git add .
#     git commit -m "Bump version to v${NEW_MARKETING_VERSION} (build ${NEW_BUILD_NUMBER})"

#     echo "Creazione del tag Git..."
#     git tag -a "v${NEW_MARKETING_VERSION}" -m "Release version ${NEW_MARKETING_VERSION}"

#     echo -e "\n${GREEN}Commit e tag creati. Ricorda di fare 'git push' e 'git push --tags'.${NC}"
# fi

echo -e "\n${BLUE}--- Fine dello script ---${NC}"