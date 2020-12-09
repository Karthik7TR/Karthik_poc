FROM tr1-docker-remote.jfrog.io/node:10

# Create app directory
WORKDIR /var/lib/project/ccng/

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
# where available (npm@5+)
COPY app/package*.json ./

RUN npm install


# Bundle app source
COPY app/ /var/lib/project/ccng/

RUN chmod 755 /var/lib/project/ccng/*

EXPOSE 8080
CMD [ "node", "app.js" ]
