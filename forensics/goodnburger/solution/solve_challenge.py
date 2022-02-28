import os
import random
import re
import hashlib
import base64
import glob

quote = u"The Cat only grinned when it saw Alice."
sentense_pattern = u'[A-Z].+[.?!]'

flag = None
list_of_books = os.listdir('backup/books')


def main(sha256_hash):
	get_books(sha256_hash)

def print_hash_b64(sentence=None):
	if not sentence:
		sentence = quote
	s = sentence.encode('utf-8')
	digest = hash_sentence(s)
	print("BYTE  : {0}".format(digest))
	b64 = base64_sentence(digest)
	print("BASE64: {0}".format(b64))
	print("HEX   : {0}".format(digest.hex()))

def hash_sentence(sentense):
	m = hashlib.sha256()
	m.update(sentense)
	digest = m.digest()
	return digest

def base64_sentence(byte_sentense):
	encoded = base64.b64encode(byte_sentense)
	return encoded

def get_books(test_hash, location='backup/books/'):
	txt_files = glob.glob("{0}*.txt".format(location))
	for t in txt_files:
		with open(t, 'r', encoding='utf-8') as book:
			text = book.read()
			sentenses = re.findall(sentense_pattern, text)
			for s in sentenses:
				h = hash_sentence(s.encode('UTF-8'))
				if test_hash == h:
					print_hash_b64(s)
					print(s)
def sha_to_bytes(s):
	return bytes.fromhex(s)

if __name__ == "__main__":
	known_hash_b64 = b'qu6pbdhtbsv+IXVRUbACZseTaFhCidFbC/5Y1rNJijY='
	decoded = base64.b64decode(known_hash_b64)
	known_hash_sha256 = "AAEEA96DD86D6ECBFE21755151B00266C79368584289D15B0BFE58D6B3498A36"
	byte_sha_value = sha_to_bytes(known_hash_sha256)
	#print_hash_b64(sentence=quote)
	get_books(byte_sha_value)
	#main(decoded)


