import random
import time
from Crypto.Cipher import AES  #pip install pycrypto
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_OAEP
import collections

import socketserver
import sys
import threading
import json
import base64
import binascii 

# Time.clock is depercated in python3.8
# https://github.com/pycrypto/pycrypto/issues/308
time.clock = time.process_time

START_TIME = 0 # Will be used for uptime
MODNAR_SPLASH = b"" # to be loaded from file cuz it's leet
AES_KEY = b"" #will be generated at init time
CONNECTION_COUNT = 0 # will be updated when new connections are created



BIND_ADDRESS = '0.0.0.0'
PORT_DEFAULT = 1337 # set by sys.argv[1]


"""
def generate_rsa_key_pair():
	# From https://cryptobook.nakov.com/asymmetric-key-ciphers/rsa-encrypt-decrypt-examples
	keyPair = RSA.generate(3072)

	pubKey = keyPair.publickey()
	print(f"Public key:  (n={hex(pubKey.n)}, e={hex(pubKey.e)})")
	pubKeyPEM = pubKey.exportKey()
	print(pubKeyPEM.decode('ascii'))
	print(f"Private key: (n={hex(pubKey.n)}, d={hex(keyPair.d)})")
	privKeyPEM = keyPair.exportKey()
	print(privKeyPEM.decode('ascii'))

generate_rsa_key_pair()
raise
"""
RSA_KEY = collections.namedtuple("RSA_KEY", ['n','e'])
RSA_PUBLIC_KEY = RSA.construct(RSA_KEY( 0xbd3bd407ef41170640a223182c773516742f7f35119b13c33c4c334c7e049dd146fba63121b654f34c84944ceb887fdcf20c657e2c035d2974654df8bcb496b7900ae3f023637293379b8d57f98352627d90181a26cf3209f808cf9a47cd159032c714566b96a46227434e6959e8c63663ced4b711fa934e03aa94115c4e047b30c4a78d84e55b6e0accf73d80cdffb131fb86ccf8f950a34fa620ca0d4ed9d29be369ce71f24ab7bbe9688c222deb59745ff30ad37173482a5ca2090facec1f9ce256728b743e1176df3a29e304c8fb943f15d423066f8adb6af284b18772aef83baf8958237439b133758a8153e80ae66c6d8a7fd8de1122bebeee40709525397b519ac2a2004556445d0448991e36034c909a6e7924597535b75db879743421d506480aba6905a4703724c457e487061d32be4b13549b5efb767f133fcbd67cf2b9d7117cc66f97432c96e35d13fe1b58260f9e2445e948e3e7f0fcd7c40803a45596a0d0ad056e700a3a52e7d96946bfd058ce8954ce009b7ff010803af5,  0x10001))

def rsa_encrypt(msg):
	encryptor = PKCS1_OAEP.new(RSA_PUBLIC_KEY)
	encrypted = encryptor.encrypt(msg)
	print("Encrypted:", binascii.hexlify(encrypted))
	return binascii.hexlify(encrypted)
	


def pad(s):
    block_size = 16
    remainder = len(s) % block_size
    padding_needed = block_size - remainder
    return s + padding_needed * b' '


def encrypt(plain_text):
	global AES_KEY
	iv = bytes(map(random.getrandbits,(8,)*AES.block_size))
	padded_text = pad(plain_text)
	cipher_config = AES.new(AES_KEY, AES.MODE_CBC, iv)
	res = b"IV: " + base64.b64encode(iv) + b"\nCIPHER: " + base64.b64encode(cipher_config.encrypt(padded_text)) + b"\n"  
	return res


def usage():
	print("Usage: {} port".format(sys.argv[0])) 

def get_time():
	return int(time.time()*1000) # Python returns a float but just go for a int (with ms precision)
	
def load_splash():
	with open("splash.txt", "r") as f:
		return f.read()
		
def load_flag():
	with open("flag.txt", "r") as f:
		return f.read()
		
def generate_symetric_key():
	random.seed(get_time()) # make sure PRNG is clean
	key = bytes(map(random.getrandbits,(8,)*AES.block_size))
	return key
	
def whats_up_time():
	global START_TIME
	return get_time() - START_TIME

""" 
	MODNAR COMMANDS 
    Note: handler is the ModnarRequestHandler
	      and every command returns data to be sent and a flag if they want to terminate the connection
"""
def get_stats(handler, args):
	res = b"STATS\n-----\n"
	res += b"Uptime: %i\n"%(whats_up_time()) 
	res += b"Current Time: %i\n"%(get_time())
	res += b"Python Version: %i.%i\n"%(sys.version_info.major, sys.version_info.minor)
	res += b"Connection Count: %i\n"%(CONNECTION_COUNT)
	return res, False
	
def get_file(handler, args):

	if len(args) == 0:
		res = b"You need to pick a file (flag or splash)"
	elif args == b"flag":
		res = encrypt(load_flag().encode())
	elif args == b"splash":
		res = encrypt(MODNAR_SPLASH.encode())
	else:
		res = b"Unknwon file: %s"%args[0]
	return res , False
	
def get_device_key(handler, args):
	# Encrypt device_key with public key then send encrypted blob	
	return rsa_encrypt(AES_KEY), False
	
def get_help(handler, args):
	help_string = b"HELP\n----\n"
	for c in handler.commands:
		help_string += b"%s: %s\n"%(c.command_text, c.description)
	return help_string, False
	
def do_exit(handler, args):
	return b"Bye!\n", True
	
""" ----------------------------- """
	
def modnar_init():
	global MODNAR_SPLASH, AES_KEY, START_TIME
	print("STARTING....")
	START_TIME = get_time()

	MODNAR_SPLASH = load_splash()
	print (MODNAR_SPLASH)
	#time.sleep(5) # for dramatic effect # XXX: no need for dramatic effect :[
	
	print("Generating secure key....")
	AES_KEY = generate_symetric_key()
	
	

	print("DEBUG: Key: {}".format(binascii.hexlify(AES_KEY)))
	
	
class ModnarCommand():
	def __init__(self, internal_function, command_text, description=b"No description available"):
		self.internal_function = internal_function
		self.command_text = command_text
		self.description = description
	
	
class ModnarRequestHandler(socketserver.BaseRequestHandler):
	""" Request Handler instantiated per connecton """
	
	def __init__(self, request, client_address, server):
		global CONNECTION_COUNT
		print("Request Handler init")
		self.commands = []
		self.commands.append(ModnarCommand(get_file, b"file", b"Download specified file securely [flag, splash]"))
		self.commands.append(ModnarCommand(get_device_key, b"device_key", b"Return device key encrypted with our hardcoded rsa key"))
		self.commands.append(ModnarCommand(get_stats, b"stats", b"Returns general info about the running system"))
		self.commands.append(ModnarCommand(do_exit, b"exit", b"Exit Modnar shell"))
		self.commands.append(ModnarCommand(get_help, b"help", b"Get some help!"))
		self.server_key = server.aes_key
		
		CONNECTION_COUNT += 1 # Used for stats
		
		socketserver.BaseRequestHandler.__init__(self, request, client_address, server)
		
	def _process_command(self, data):
		"""  Format is: command  <space> arg1 <space> arg2 .... 
		     This doesn't suport space in input_arg :'( 
		"""
		command_string = data.split(b" ")[0]
		command_arg = data[len(command_string)+1:]  
		data_out = None
		should_exit = False # for exit function
		for c in self.commands:
			if command_string == c.command_text:
				data_out, should_exit = c.internal_function(self, command_arg)
		return data_out, should_exit
			
		
	def setup(self):
		self.request.send(MODNAR_SPLASH.encode())
	def handle(self):
		bNeedToExit = False
		while not bNeedToExit:
			self.request.send(b"\nModnar> ")
			data = self.request.recv(1024)
			data = data[:-1] #strip "\n"
			result = None
			result, bNeedToExit = self._process_command(data)
			
			if result == None:
				result = b"Unknown Command. Send help for info\n"
			self.request.send(result)
	
	

class ModnarServer(socketserver.ThreadingTCPServer):
	""" Server class instanciated once """
	def __init__(self, server_address, handler_class=ModnarRequestHandler):
		print("Server Init")
		self.aes_key  = AES_KEY
		
		socketserver.ThreadingTCPServer.__init__(self, server_address, handler_class)
		return

if (len(sys.argv) > 1):
	PORT = int(sys.argv[1])
else:
	PORT = PORT_DEFAULT

modnar_init()
print("Listening on {}".format((BIND_ADDRESS, PORT)))
server = ModnarServer((BIND_ADDRESS, PORT), ModnarRequestHandler)

t = threading.Thread(target=server.serve_forever)
t.setDaemon(True) # don't hang on exit
t.start()

while True:
	time.sleep(1)
	
	
