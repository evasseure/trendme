# trendme

To run `lein ring server-headless`  
To build `lein ring uberjar`  
to run build `java -jar target/trendme-0.1.0-SNAPSHOT-standalone.jar`

## Prerequisites

You will need [Leiningen][] 2.0.0 or above installed.

[leiningen]: https://github.com/technomancy/leiningen

## Run on server

Create systemd file  
`sudo touch /usr/lib/systemd/system/trendme.service`

with this content:

```
[Unit]
Description=webserver Daemon

[Service]
ExecStart=java -jar /home/erwan/trendme/target/trendme-0.1.0-SNAPSHOT-standalone.jar

[Install]
WantedBy=multi-user.target
```

Run with `sudo service trendme start`
Restart with `sudo service trendme restart`
Enable on start `sudo service trendme enable`
Disable on start `sudo service trendme disable`
