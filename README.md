# Mixmicro-Registry

Mixmicro Service Registry Component

## Function

 - [x] Nacos Migration: Nacos cluster upgrade, migration, smooth application transition.
 - [ ] Mixmicro Registry: Naming Service. 

## Architecture

### Architecture Topology


```
                Cluster Version Upgrade & Migration

    +----------------+   +----------------+  +----------------+
    |Application Pod |   |Application Pod |  |Application Pod |
    +----------------+   +----------------+  +----------------+             
            |                     |                 |
            |                     |                 |
            +-----------+---------+-----------------+
                        |
                        | Service Registry & Discovery
                        |
                        \/
                +----------------+
                |Nginx Loadblance| ----------------+
                +----------------+                 |
                       |                           |
                       |  Switching Nginx Traffic  |
                       |     ---------->>>         |
                       |                           |
+----------------------|---------------------------|--------------------+
|                      \/                          \/                   |
|                +-------------+               +-------------+          |
|         +----> |NacosClusterA| < V1.1.x      |NacosClusterB| V1.3.x+  |
|         |      +-------------+               +-------------+          |
|         |         |                             ^       |             |
| Rebuild |         | Listener                    |       |             |
|         |         |    +-------------+          | Push  | Listener    |
|         |         |    | NacosConfig |          |       |             |
|         |         |    +--+----------+          |       |             |
|         |         |       ^ Pull Info           |       |             |
|         |         \/      |                     |       \/            |
|         |        +------------------------------+--------+            |
|         <--------+   NacosSync1, NacosSync2,  ....       |            |
|                  +---+-------------------------+---------+            |
|                                                                       |
+-----------------------------------------------------------------------+

```

### Requirements

Before you begin, install the following:

- 64bit OS: Linux/Unix/Mac/Windows supported, Linux/Unix/Mac recommended.
- 64bit JDK 1.8+: downloads, JAVA_HOME settings.
- Maven 3.2.x+: downloads, settings.
- MySql 5.6.+

YunLSP+ Maven Repository Configuration

> [Configuration Reference](https://github.com/misselvexu/Acmedcare-Maven-Nexus/blob/master/README.md)

### Quick Start


#### Run With `binary package` .

You can download binaries from [Release Repo](http://git.hgj.net/elve.xu/Mixmicro-Registry) or [repo.hgj.net](http://nexus.hgj.net/).

*First* : unzip release package

```bash
$ tar -zxvf *.tar.gz
```

*Second* : startup & shutdown

```bash
$ sh ./bin/startup.sh -p production  
```

*Third* : check the application log

```bash
$ tail -f logs/start.log
```

#### Run With `docker image` .

*First* : build docker image 

```bash
$ cd mixmicro-registry/sync/server
$ mvn docker:build
```

*Second* : startup docker image

```bash
$ docker run -p 2663:2663 \ 
    --rm \ 
    -e SERVER_ENV="docker" \ 
    -v /tmp/logs:/tmp/logs \
    -v /tmp/logs/mixmicro-registry-sync-server:/mixmicro-registry-sync-server/logs \ 
    --name mixmicro-registry-sync-server \
    mixmicro-registry-sync-server:1.0.0.BUILD-SNAPSHOT
```

*Third* : check docker running containers

```bash

$ docker ps

> output

CONTAINER ID        IMAGE                                                COMMAND                  CREATED             STATUS              PORTS                                                   NAMES
3205a5f72cff        mixmicro-registry-sync-server:1.0.0.BUILD-SNAPSHOT   "/bin/sh -c bin/star…"   25 minutes ago      Up 25 minutes       0.0.0.0:2663->2663/tcp                                  mixmicro-registry-sync-server

```


> param: -p (optional): `production` | `dev`

### Configuration Properties

```properties

## Nacos Sync Config

### Origin Nacos Cluster Properties
# origin nacos server console address ,supported 
mixmicro.registry.sync.nacos.origin.console-addr=http://xxx.xxx.xxx.xxx:18848/
# origin nacos server address (for application node) ,supported  
mixmicro.registry.sync.nacos.origin.server-addr=xxx.xxx.xxx.xxx:18848
# origin nacos server auth username
mixmicro.registry.sync.nacos.origin.username=nacos
# origin nacos server auth password
mixmicro.registry.sync.nacos.origin.password=nacos

### Dest Nacos Cluster Properties
# dest nacos server console address ,supported 
mixmicro.registry.sync.nacos.destination.console-addr=http://xxx.xxx.xxx.xxx:28848/
# dest nacos server address (for application node) ,supported
mixmicro.registry.sync.nacos.destination.server-addr=xxx.xxx.xxx.xxx:28848
# dest nacos server auth username
mixmicro.registry.sync.nacos.destination.username=nacos
# dest nacos server auth password
mixmicro.registry.sync.nacos.destination.password=nacos

### Sync Rule
# namespace rule , eg: namespace1;namespace2;....
mixmicro.registry.sync.nacos.sync-rule.namespace-regular=*
# namespace rule , eg: service-name-1;service-name-2;....
mixmicro.registry.sync.nacos.sync-rule.service-regular=*

### When there are new namespaces, service points changes in the main cluster, they are automatically recognized and added to the execution plan.
# default value is true
mixmicro.registry.sync.nacos.fix.enabled=true

### If or not to enable anti-registration from cluster service, default value: false
### Recommended opening
mixmicro.registry.sync.nacos.deregister=true

### Core Config
mixmicro.registry.sync.nacos.core.config-server-addr=xxx.xxx.xxx.xxx:8848

```

### Building from Source

You don’t need to build from source to use `Mix Micro Service Components` (binaries in [repo.hgj.net](http://nexus.hgj.net)), 
but if you want to try out the latest and greatest, 
`Mix Micro Service Components` can be easily built with the maven wrapper. You also need JDK 1.8.

*First* : git clone source from gitlab
 
```bash
$ git clone http://git.hgj.net/mixmicro/mixmicro-registry.git
```

*Second* : build

```bash
$ mvn clean install
```

If you want to build with the regular `mvn` command, you will need [Maven v3.5.0 or above](https://maven.apache.org/run-maven/index.html).



### License
 
```
                   GNU LESSER GENERAL PUBLIC LICENSE
                       Version 3, 29 June 2007

 Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>
 Everyone is permitted to copy and distribute verbatim copies
 of this license document, but changing it is not allowed.

```



