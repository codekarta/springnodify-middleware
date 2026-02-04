#!/usr/bin/env bash
#
# Setup GPG key and export values for GitHub Actions secrets
# (GPG_PRIVATE_KEY and GPG_PASSPHRASE for Maven Central release)
#
# Usage: ./setup-gpg-secrets.sh [KEY_ID]
#   If KEY_ID is omitted, lists existing secret keys so you can pick one.
#

set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'

info()  { echo -e "${GREEN}[INFO]${NC} $*"; }
warn()  { echo -e "${YELLOW}[WARN]${NC} $*"; }
err()   { echo -e "${RED}[ERROR]${NC} $*"; }

# --- Check GPG is available ---
if ! command -v gpg &>/dev/null; then
  err "gpg not found. Install gnupg (e.g. brew install gnupg)."
  exit 1
fi

KEY_ID="$1"

if [[ -z "$KEY_ID" ]]; then
  info "No KEY_ID given. Listing existing secret keys:"
  echo ""
  gpg --list-secret-keys --keyid-format=long
  echo ""
  warn "Run this script WITH your key ID (the part after 'rsa3072/' or 'rsa4096/' on the 'sec' line):"
  echo "  ./setup-gpg-secrets.sh C810D31453064D9D"
  echo ""
  warn "You will be prompted for your GPG passphrase. Then gpg-private-key-export.txt will be created in this folder."
  echo ""
  read -p "Create a new GPG key now? [y/N] " -n 1 -r
  echo
  if [[ $REPLY =~ ^[Yy]$ ]]; then
    gpg --full-generate-key
    info "Key created. List keys again and run: ./setup-gpg-secrets.sh YOUR_KEY_ID"
  fi
  exit 0
fi

# --- Export private key ---
OUTPUT_FILE="gpg-private-key-export.txt"
info "Exporting private key $KEY_ID to $OUTPUT_FILE ..."
if ! gpg --armor --export-secret-keys "$KEY_ID" > "$OUTPUT_FILE" 2>/dev/null; then
  err "Export failed. Check that key ID '$KEY_ID' exists (use: gpg --list-secret-keys --keyid-format=long)"
  exit 1
fi

info "Private key exported to $OUTPUT_FILE"
echo ""
echo "--- Next steps ---"
echo "1. Add GitHub repository secret GPG_PRIVATE_KEY:"
echo "   Copy the ENTIRE contents of $OUTPUT_FILE (including BEGIN/END lines)"
echo "   Repo → Settings → Secrets and variables → Actions → New repository secret"
echo ""
echo "2. Add GitHub repository secret GPG_PASSPHRASE:"
echo "   Use the passphrase you set when creating this GPG key."
echo ""
echo "3. Publish public key to a keyserver (required for Maven Central):"
read -p "   Upload public key to keys.openpgp.org now? [Y/n] " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Nn]$ ]]; then
  gpg --keyserver keys.openpgp.org --send-keys "$KEY_ID"
  info "Public key uploaded. Maven Central can now verify your signed artifacts."
fi
echo ""
warn "Delete the export file after adding the secret: rm $OUTPUT_FILE"
