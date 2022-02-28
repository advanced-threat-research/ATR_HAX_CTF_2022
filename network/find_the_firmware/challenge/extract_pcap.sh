#!/bin/bash
# Github doesn't allow files over 100M and doesn't like files over 50M so we split up the pcap into parts.

# Combine all the part files
cat ./firmware.pcap.tar.xz.part* > ./firmware.pcap.tar.xz

# Extract pcap from tar file
tar xvJf ./firmware.pcap.tar.xz

