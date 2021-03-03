#!/bin/bash

yum install -y java-1.8.0-openjdk-headless.x86_64

ls installSystemdService.sh
if [ $? -ne 0 ]; then
    echo "Could not find the installSystemdService.sh in the current dir. Ensure when you run this script that your in the Bash dir."
    exit -1
fi

cd ../target
Working_Directory="$(pwd)"

applicationName="druidRollUpUI"

adduser -r -s /bin/nologin ${applicationName}
chown -R ${applicationName}:${applicationName} ${Working_Directory}

read -p "Enter zookeeperHosts: " zookeeperHosts
read -p "Enher rootZNode: " rootZNode
read -p "Enter configRootZNode: " configRootZNode

cat > /usr/lib/systemd/system/${applicationName}.service <<EOF
[Unit]
Description=${applicationName} Service

[Service]
Type=simple
WorkingDirectory=$Working_Directory
SyslogIdentifier=$applicationName
User=$applicationName
Group=$applicationName
LimitNOFILE=infinity
ExecStart=/bin/java -jar DruidRollupWebServer-0.0.1-SNAPSHOT.jar --zookeeperHosts=${zookeeperHosts} --rootZNode=${rootZNode} --configRootZNode=${configRootZNode}
Restart=always
TimeoutSec=60

[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable ${applicationName}.service
systemctl start ${applicationName}.service

