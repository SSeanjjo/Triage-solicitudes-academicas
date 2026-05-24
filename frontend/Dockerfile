# Stage 1: Build
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Run
FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app/dist/frontend ./dist/frontend
COPY package*.json ./
RUN npm install --omit=dev
EXPOSE 4000
ENV PORT=4000
ENV API_URL=http://backend:8080
CMD ["node", "dist/frontend/server/server.mjs"]
