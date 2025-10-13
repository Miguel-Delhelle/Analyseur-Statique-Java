// Fichier : webApp/vite.config.ts
import { defineConfig } from 'vite'

export default defineConfig({
  server: {
    // Configure Vite pour rediriger les appels /api vers le backend Spring Boot
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // L'adresse de ton serveur Spring
        changeOrigin: true, // Nécessaire pour les requêtes cross-origin
      },
    },
  },
  build: {
    // Le dossier de sortie du build
    outDir: '../main/resources/static',
    // Vider le dossier de sortie avant chaque build
    emptyOutDir: true,
  },
})