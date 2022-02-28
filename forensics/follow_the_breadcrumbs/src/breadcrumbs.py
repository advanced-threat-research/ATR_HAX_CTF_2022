#!/usr/local/bin/python3

import tarfile
import struct
import sys
import os
import argparse
import binascii

def create_challenge(program):
    #open a binary file
    f = open(program,'rb')
    data = f.read()
    f.close()

    data = bytearray(data)
    data.reverse()
    data = bytes(data)

    #create innermost tarfile -- remember, this is INVERTED
    upper_nibble = format('%x' % (data[0] >> 4))
    lower_nibble = format('%x' % (data[0] & 0x0F))

    #requires special processing, so do it "by hand"
    f = open(lower_nibble, 'w')
    f.close()
    t = tarfile.open('data.tar', 'w:')
    t.add(lower_nibble)
    t.close()
    os.remove(lower_nibble)

    f = open(upper_nibble,'w')
    f.close()
    t = tarfile.open('data_tmp.tar', 'w:')
    t.add(upper_nibble)
    t.add('data.tar')
    t.close()
    os.remove(upper_nibble)
    os.replace('data_tmp.tar', 'data.tar')

    location = len(data) - 1

    nibbles = len(data[1:]) * 2
    print(nibbles)

    for datum in data[1:]:
        upper_nibble = format('%x' % (datum >> 4))
        lower_nibble = format('%x' % (datum & 0x0F))

        print(location)
        location -= 1

        #create and add lower nibble to archive first
        f = open(lower_nibble, 'w')
        f.close()
        t = tarfile.open('data_tmp.tar', 'w:')
        t.add(lower_nibble)
        t.add('data.tar')
        t.close()
        os.replace('data_tmp.tar', 'data.tar')
        os.remove(lower_nibble)

        #then create and add upper nibble
        f = open(upper_nibble,'w')
        f.close()
        t = tarfile.open('data_tmp.tar', 'w:')
        t.add(upper_nibble)
        t.add('data.tar')
        t.close()
        os.replace('data_tmp.tar', 'data.tar')
        os.remove(upper_nibble)


def solve_challenge(output):
    data = bytes()

    t = tarfile.open('data.tar', 'r')
    t.extract('data.tar','tmp')
    files = t.getnames()
    t.close()
    os.replace('tmp/data.tar', 'data.tar')

    total = 1
    count = 0
    byte = ''

    while 'data.tar' in files:
        print(total)
        total += 1

        byte += files[0]
        count += 1
        if count % 2 == 0:
            data += binascii.unhexlify(byte)
            count = 0
            byte = ''

        t = tarfile.open('data.tar', 'r')
        files = t.getnames()
        try:
            t.extract('data.tar', 'tmp')
            t.close()
            os.replace('tmp/data.tar', 'data.tar')
        except KeyError:
            #must be the innermost, so add last byte
            byte += files[0]
            data += binascii.unhexlify(byte)

    f = open(output, 'wb')
    f.write(data)

def main():
    
    create_challenge(sys.argv[1])
    #solve_challenge(sys.argv[1])

if __name__ == "__main__":
    main()
