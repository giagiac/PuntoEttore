#!/bin/bash

# Nome del file di archivio compresso e criptato
archivio="./ExtraFiles.7z"

simmetric_keyfile="./symmetric_keyfile.key"

# Nome del file di archivio compresso e criptato
archivio="./ExtraFiles.7z"

function decriptFileCriptato() {
    # Crittografa l'archivio con OpenSSL usando il file PKCS#12
    openssl aes-256-cbc -d -a -pbkdf2 -in $archivio.enc -out $archivio.dec.7z -kfile $simmetric_keyfile
    
    echo "File decriptato correttamente 💪"
}

cartella=../../../../

function unzipArchive() {
    # Decomprimi la cartella mantenendo la struttura delle directory
    unzip -: -o $archivio.dec.7z -d $cartella

    echo "Cartella decompressa con successo in $cartella"

    rm -rf $simmetric_keyfile
    rm -rf $archivio.dec.7z
    
    echo "Archivio rimosso 💪"
}

decriptFileCriptato && unzipArchive