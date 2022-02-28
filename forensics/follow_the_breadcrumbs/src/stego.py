#take in some text
#get a path to an image
#convert image to bitmap
#embed bits of text in lsb of each channel of each pixel

import bitarray
from bitarray.util import int2ba, ba2int
import sys
from PIL import Image
import math
import binascii

def str2bitarray(s):
    b = bitarray.bitarray()
    b.frombytes(bytes(s, "utf-8")) 

    return b + bitarray.bitarray('00000000')

def main():
    #sys.argv[1] should be text 
    #sys.argv[2] should be image to embed within
    #sys.argv[3] is optional position (default is 7)
    im = Image.open(sys.argv[2])
    text = sys.argv[1]
    bits = str2bitarray(text)

    if len(sys.argv) > 3:
        pos = int(sys.argv[3])
    else:
        pos = 7

    pixels = im.getdata()
    new_pixels = []
    numpixels = math.ceil(len(bits) / 3)
    j = 0
    for i in range(numpixels):
        if j + 3 < len(bits):
            vr, vg, vb = bits[j:j+3]
        else:
            vb = False
            if j + 2 < len(bits):
                vr, vg = bits[j:j+2]
            else:
                vr = bits[j]
                vg = False

        r,g,b = pixels[i]

        br = int2ba(r, 8)
        bg = int2ba(g, 8)
        bb = int2ba(b, 8)

        br[pos] = vr
        bg[pos] = vg
        bb[pos] = vb

        j += 3
        new_pixels.append((ba2int(br),ba2int(bg),ba2int(bb)))
    
    im.putdata(new_pixels)
    im.save('new_' + sys.argv[2])


    #pixels = Image.getdata() to convert to sequence we can modify
    #modify lsb of each channel of each pixel to be the next bit from the text
    #Image.putdata(sequence) to overwrite the original

    extracted_bits = bitarray.bitarray()
    #extract to verify
    i = 0
    im2 = Image.open('new_' + sys.argv[2])
    pixels2 = im2.getdata()

    while bitarray.bitarray('000000000') not in extracted_bits:
        r,g,b = pixels2[i]

        br = int2ba(r, 8)
        bg = int2ba(g, 8)
        bb = int2ba(b, 8)

        extracted_bits.append(br[pos])
        extracted_bits.append(bg[pos])
        extracted_bits.append(bb[pos])

        i += 1

    print(str(extracted_bits[:-8].tobytes()))

if __name__ == "__main__":
    main()