FROM node:18-alpine

WORKDIR /app

# Note: Build context is repo root, so paths must include subdirectory
COPY pdf-service/package.json .
RUN npm install

COPY pdf-service/server.js .

EXPOSE 5100

CMD ["node", "server.js"]

