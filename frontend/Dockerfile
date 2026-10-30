# syntax=docker/dockerfile:1

FROM node:24-bookworm AS build
WORKDIR /src
COPY package.json package-lock.json .npmrc ./
RUN --mount=type=secret,id=npmrc,target=/root/.npmrc npm ci
COPY . .
# Same auto-sign-in as the Vite dev server. The API container uses the local profile.
ENV VITE_MOCK_AUTH=true
RUN npm run build

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /src/dist /usr/share/nginx/html
EXPOSE 80
