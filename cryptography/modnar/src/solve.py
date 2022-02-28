import base64
import random
from Crypto.Cipher import AES  #pip install pycrypto
import binascii

# Copy data obtained from challenge....
IV= "XNEI/TMjhtmTkJStsblGVQ=="
CIPHER= "dNA8Yg2UzooCv83U4xkXjhuoVF4mKdDlxonyj5WhBeJqNOwrThjpmdTDXdx8dCmbYMi3bI4sfp0U8I8idxwpHQ=="
uptime  =  431423
current_time =  1637364754671

DEBUG_KEY = None # Put the AES_KEY printed by the server if you want to verify decrypt function and that the recovering algo works
#DEBUG_KEY = b"34d5e4c7ceefd57e6e2f160862ff46ad"




def decrypt(cipher_text, IV, key):
	iv = base64.b64decode(IV)
	cipher_ = base64.b64decode(cipher_text)
	cipher_config = AES.new(key, AES.MODE_CBC, iv)
	return cipher_config.decrypt(cipher_)

if DEBUG_KEY:
	print("TEST DECRYPT WITH ACTUAL KEY")
	print (decrypt(CIPHER, IV, binascii.unhexlify(DEBUG_KEY)))
	#raise 
	
def generate_symetric_key(seed):
	random.seed(seed) 
	key = bytearray(map(random.getrandbits,(8,)*AES.block_size))
	return key
	
	
for i in range(0, 1000*3):
	
	start_time = current_time - uptime
	key = generate_symetric_key(start_time + i ) #Just to be safe -5 
	
	# If DEBUG_KEY is not none then we'll compare at each iteration
	# To see if we found the actual key
	# Probably unnecessary unless you want to increase number of keys to try
	# and want to speed up testing by not having to call decrypt
	if DEBUG_KEY:
		key_hex = binascii.hexlify(key)
		if DEBUG_KEY == key_hex:
			print("DEBUG: ALGO WORKS")
			print("DEBUG KEY FOUND")
			print("DEBUG DECODE:")
			print(decrypt(CIPHER, IV, key))

	res = decrypt(CIPHER, IV, key)
	if b"ATR" in res[:4]:
		print(i)
		print(res)
		print("KEY: {}".format(binascii.hexlify(key)))
		break
