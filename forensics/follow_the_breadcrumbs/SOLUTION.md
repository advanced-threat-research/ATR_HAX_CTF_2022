# Solution Write Up for 'Follow the Breadcrumbs'
# Expected Behavior

The user is given the `breadcrumbs.tar.bz2` file, and the prompt
> You found this file on a flash drive with a sticky note on it which says "Secret key in picture." It doesn't seem to be a picture, though...this might take a while...

# Solution
The solution to this challenge requires extracting the nested archives over and over, building a binary file from the nibbles that are output at each stage of decompression. In other words, stringing all the nibbles together as a hex string will result in an inner-most bzip file. This file will contain an image and a script which was used to embed the flag into the image. It is incomplete, however. The user must add the "extraction" function to the code in order to extract the flag.

The extract function can be run on one of 8 positions in each byte -- so a little trial and error is necessary to determine the location of the real data.

A solution for all parts can be found in the `src/` directory.

For extraction, you can use this function:
```python
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
```

And for pulling out the stego data from the image, this snippet can be used (see solution in src/ folder for context):
```python
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
```

# Flag
[//]: <> (Add the flag below)
**ATR[Say hello to my little friend]**
