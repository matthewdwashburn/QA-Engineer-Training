import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    react(),
    babel({ presets: [reactCompilerPreset()] })
  ],
  server: {
    host: '127.0.0.1',
    // Mirrors frontend/nginx.conf so dev and prod share one relative-path config.
    proxy: {
      '/api/employee': {
        target: 'http://127.0.0.1:5000',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/employee/, ''),
      },
      '/api/manager': {
        target: 'http://127.0.0.1:7001',
        changeOrigin: true,
        rewrite: (p) => p.replace(/^\/api\/manager/, ''),
      },
    },
  },
})
