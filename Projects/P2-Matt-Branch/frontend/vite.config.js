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
    // Vite rejects requests whose Host header it does not recognize. Served
    // from EC2 the browser sends the instance's public address, which would
    // otherwise be refused with "Blocked request. This host is not allowed."
    // Set VITE_ALLOWED_HOSTS to a comma-separated list, or "true" for any host.
    allowedHosts:
      process.env.VITE_ALLOWED_HOSTS === 'true'
        ? true
        : (process.env.VITE_ALLOWED_HOSTS ?? '')
            .split(',')
            .map((host) => host.trim())
            .filter(Boolean),
  },
})
