#!/bin/bash

while true; do
    if socat TCP-LISTEN:1337,reuseaddr,fork EXEC:/root/drop.sh,pty,stderr,setsid,sigint,sane; then
        echo "Failed to start socat"
        exit 1
    fi
done
