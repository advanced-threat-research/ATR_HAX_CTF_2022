# Solution Write Up for 'Find the Firmware'
# Expected Behavior

The user is given a pcap file, along with the challenge prompt of
> You happened to be running a packet capture test on your network when your roommate mentions that he updated the firmware of one of the IoT devices you use in the house on the 192.168.86.0/24 subnet. Curious, you decide to set out and determine if you were able to snag the firmware! Maybe there are some secrets buried in the firmware file system! Some information that you came across separately seems to be an md5 checksum: "7aa6a7ebcbd98ce19539b668ff790655 download.bin". Given that, you figure any file that might be a firmware you get that doesn't match that checksum likely has some missing information.

# Solution

There are several solutions. One such solution is
```python:src/extraction.py
from scapy.all import *
from scapy.layers.http import *
import base64
import collections


def main():
    file = "firmware.pcap"
    load_layer("http")

    sessions = sniff(offline=file,
                     session=TCPSession,
                     filter="host 172.16.0.54").sessions()
    '''
    This first part parses the TCP file requests and stores every offset in a dict. The offset will
    contain the data that will be written to bin file after the responses are processed"

    Some ports have multiple requests. The port dict stores the offset numbers for each port so that data
    can be sorted to the proper offset
    '''
    offsets = {}  # this will store final data to be put into file
    port = {}  # every port has one or more offsets associated
    for k, v in sessions.items():
        for p in v:  # iterate through session
            if Raw in p:
                if p[IP].src == "172.16.0.62" and p[IP].dst == "172.16.0.54":
                    t = p.load.decode().split("offset=")[1]
                    t2 = t.split("&")[0]
                    offsets[t2] = -1  # store offsets, load from responses will be placed here later
                    if p[IP].sport in port:  # store port and offsets associated with ports
                        port[p[IP].sport].append(t2)
                    else:
                        port[p[IP].sport] = [t2]
    '''
    Now process responses. For each response, store the session data in a dict, containing the seq number
    and load. If the given port for the session has multiple offsets, place the data for the lower sequence number
    into the lower offset
    '''
    for k, v in sessions.items():
        session_data = []  # list here for possibility of multiple streams of data in one session
        dest_port = ""
        for p in v:
            if Raw in p:
                if p[IP].src == "172.16.0.54" and p[IP].dst == "172.16.0.62":
                    dest_port = p[IP].dport

                    if "HTTP/1.0 200" in p.load.decode():
                        continue
                    elif "\r\n\r\n" in p.load.decode():
                        trimmedLoad = p.load.decode().split('\r\n\r\n')[1]
                        pLoad = trimmedLoad
                        if p.load.decode().find("==") >= 0 :
                            session_data.append(pLoad)
                    else:
                        if pLoad.find("==") == -1 : # this filters out tcp packets that are out of order
                            pLoad = pLoad + p.load.decode()
                            session_data.append(pLoad)

        # store data in offset dict
        if len(session_data) == 1 :
            offset_number = port[dest_port][0]
            offsets[offset_number] = session_data[0]
        elif len(session_data) > 1:
            offset_numbers = port[dest_port]
            for i in range(len(session_data)):
                offsets[offset_numbers[i]] = session_data[i]

    # write data to file
    o = {int(k): v for k, v in offsets.items()}  # change offset numbers to ints
    sorted_offset = collections.OrderedDict(sorted(o.items()))
    f = open("download.bin", "wb")
    for k, v in sorted_offset.items():
        if v != -1:
            f.write(base64.b64decode(v))
    f.close()

if __name__ == '__main__':
    main()
```

Another option would be to use wireshark:

1. Open file in Wireshark
2. Filter on source IP address
3. Export all HTTP objects in visible packets
4. Combine the objects into a single file, using a python or shell script, using the name of the exported option as the block location in firmware.
5. Verify checksum matches what was given in the prompt.
6. Run `binwalk -M firmware.bin` to obtain the squash file system
7. Actual flag is in the squashfs in /root/.secret/

# Flag
[//]: <> (Add the flag below)
**Flag: ATR[F1rMW4r315N750H4rD]**
