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

> param: -p (optional): `production` | `dev`


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



