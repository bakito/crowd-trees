# A tree visualization of crowd user and group dependencies

## build
```bash
mvn clean package
```

## run image 
```bash
docker run --name=crowd-trees \
           -e CROWD_URL=http://localhost:8095/crowd/\
           -e CROWD_APPLICATION_USER=crowd-trees \
           -e CROWD_APPLICATION_PASSWORD=crowd-trees \
           -p 8080:8080 \
           --rm -it \
           bakito/crowd-trees
```