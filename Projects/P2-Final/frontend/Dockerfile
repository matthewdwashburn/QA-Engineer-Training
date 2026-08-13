# ---------- Stage 1: build ----------
FROM node:22-alpine AS build

WORKDIR /app

COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci

COPY frontend/ ./

# No build args: the bundle uses relative /api paths, so one image fits every
# environment and nginx.conf decides where those land.
RUN npm run build

# ---------- Stage 2: serve ----------
FROM nginx:1.27-alpine

COPY frontend/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80
