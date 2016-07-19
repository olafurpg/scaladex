## How to run Scaladex:

Install sass. [sass install page](http://sass-lang.com/install)

```bash
$ git submodule init
$ git submodule update
$ sbt
> data/reStart elastic
> ~server/re-start
open http://localhost:8080
```

## How to publish the Scaladex-Plugin

``` 
$ sbt
> project sbtScaladex
> bintrayChangeCredentials
# username: scaladex
# api key: **********
```

## Sbt Plugin

The idea behind sbt-scaladex is to notify the scala index of a new libraries being released. The final goal is to merge this plugin directly into sbt.

## Deployment

```bash
sbt server/universal:packageBin
scp server/target/universal/server-0.1.3.zip scaladex@alaska.epfl.ch:app.zip
ssh scaladex@alaska.epfl.ch

# first time
# git clone git@github.com:scalacenter/scaladex.git
# git clone git@github.com:scalacenter/scaladex-credentials.git
# cd ~/scaladex
# git submodule init
# git submodule update

# Kill server
jps
# kill -9 (Server pid)
rm -rf ~/.esdata/data/
rm -rf server-*/
cd ~/scaladex
sbt data/reStart elastic

unzip app.zip

nohup ~/webapp/bin/server -Dconfig.file=scaladex-credentials/application.conf &
```