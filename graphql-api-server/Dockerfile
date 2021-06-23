FROM node:latest

WORKDIR /usr/src/graphql-api-server

COPY package.json ./

RUN npm install

COPY . .

EXPOSE 4000

ENTRYPOINT ["./start-server.sh"]
