# A tree visualization of crowd user and group dependencies

## Show groups of a user
/user/{userid}  
![alt /user/{userid}](https://raw.githubusercontent.com/bakito/crowd-trees/master/doc/user-groups.png)

## Show group in hierarchy with all assigned users
/group/{groupid}  
![alt /group/{groupid}](https://raw.githubusercontent.com/bakito/crowd-trees/master/doc/group-with-users.png)

## Show group in hierarchy withoud users

/group/{groupid}?withUsers=false  
![alt /group/{groupid}?withUsers=false](https://raw.githubusercontent.com/bakito/crowd-trees/master/doc/group-without-users.png)

## build
```bash
mvn clean package
```

## run image 
```bash
docker run --name=crowd-trees \
           -e CROWD_URL=http://192.168.1.38:8095/crowd/\
           -e CROWD_APPLICATION_USER=crowd-trees \
           -e CROWD_APPLICATION_PASSWORD=crowd-trees \
           -p 8080:8080 \
           --rm -it \
           bakito/crowd-trees
```
