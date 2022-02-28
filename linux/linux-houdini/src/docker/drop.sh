#!/bin/bash

username=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13 ; echo '')
password=$(head /dev/urandom | tr -dc A-Za-z0-9 | head -c 13 ; echo '')

useradd -m -s /bin/bash -p $(openssl passwd -1 $password) $username

# Ref https://github.com/SinusBot/docker/pull/40
# WORKAROUND for `setpriv: libcap-ng is too old for "all" caps`, previously "-all" was used here
# create a list to drop all capabilities supported by current kernel
cap_prefix="-cap_";
caps="$cap_prefix$(seq -s ",$cap_prefix" 0 "$(cat /proc/sys/kernel/cap_last_cap)")";

setpriv --reset-env --reuid=`id -u $username` --regid=`id -g $username` --init-groups --inh-caps="${caps}" /bin/bash --login -li
