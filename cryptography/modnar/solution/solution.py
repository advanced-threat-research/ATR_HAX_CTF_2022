import time
import datetime
import base64
from pwn import *
from Crypto.Cipher import AES # pip3 install pycryptodome

DEBUG=False

TEST_KEY=b'\x96\x1b\x809\x80\x98n \xd3\xff^e\xa8\xe9\xa0 '

def RSA_decrypt(enc):
    decryptor = PKCS1_OAEP.new(keyPair)
    decrypted = decryptor.decrypt(encrypted)
    if DEBUG: print('Decrypted: %s' % decrypted)

def AES_decrypt(enc, key, iv):
    cipher = AES.new(key, AES.MODE_CBC, iv)
    return cipher.decrypt(enc)

def wait_till_promt(con):
    r = con.recvuntil(b'Modnar>')
    if DEBUG: print(r)
    return r

def generate_symetric_key(t):
	random.seed(t) # make sure PRNG is clean
	key = bytearray(map(random.getrandbits,(8,)*AES.block_size))
	return key

p = remote('0.cloud.chals.io', 26568)
if not p.connected:
    print('Couldn\'t connect to the server')
    exit(1)

# Wait for user to connect a debuger or what not
if DEBUG: input()

wait_till_promt(p)

p.send(b'stats\n')

# get uptime


temp = wait_till_promt(p).decode('utf-8')

uptime = int(temp.split('\n')[2].split(' ')[1]) / 1000
cur_time = int(temp.split('\n')[3].split(' ')[2]) / 1000

if DEBUG: print('Grabbed Uptime: %s' % uptime)
if DEBUG: print('Grabbed Time: %s' % cur_time)
seed_time = int(datetime.datetime.fromtimestamp( cur_time - uptime).timestamp() * 1000)

if DEBUG: print('Calculated Seed time: %s' % seed_time)

keys_to_try = []
num_of_keys = 3
step = 1
for x in range(0, num_of_keys):
    keys_to_try.append(generate_symetric_key(seed_time))
    seed_time = seed_time + step

#if DEBUG: print('Created [%s] AES keys to try' % keys_to_try)

# Download encrypted flag
p.send(b'file flag\n')

temp = wait_till_promt(p).decode('utf-8')

flag_iv, flag_enc = (temp.split(' ')[2].split('\n')[0], temp.split(' ')[3].split('\n')[0])

if DEBUG: print('Grabbed IV: %s\nGrabbed CIPHER: %s' % (flag_iv,flag_enc))

flag_iv = base64.decodebytes(flag_iv.encode('utf-8'))
flag_enc = base64.decodebytes(flag_enc.encode('utf-8'))

#p.send(b'device_key\n')

#device_key = wait_till_promt(p).split()[0]

#if DEBUG: print('Grabed Device Key: %s' % device_key)
if TEST_KEY: keys_to_try.append(TEST_KEY)
for x in keys_to_try:
    r = AES_decrypt(bytes(flag_enc), bytes(x), bytes(flag_iv))
    try:
        print('PlainText: %s' % r.decode('utf-8'))
    except UnicodeDecodeError:
        continue
    break


print('Exiting now')
p.send(b'exit')
