FROM node:17-alpine3.14 as build
WORKDIR /src
COPY . .
RUN npm run build

FROM httpd:2.4.52-alpine
COPY --from=build /src/public /usr/local/apache2/htdocs
CMD echo $BACKEND_URL > /usr/local/apache2/htdocs/backend.json\
    && httpd-foreground
