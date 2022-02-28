# Solution Write Up for 'goodnburger'
# Expected Behavior
The challenger is given a series of books in .txt files. They only need that and the challenge description.

TRIVIA: "Good N Burger" is phonetically similar to Gutenburg from the Gutenburg project. All of the books included come from the Gutenburg project.


# Solution

There are a few ways to solve this problem. I would start with simple regex expressions and loop through the books with python. Technically, the regex would be more complicated to account for all possible formats of sentences but I've purposefully avoided those scenarios. The expectation is that the challenger would start off with simpler solutions with the intention of resolving more complex sentences later.

1. Create simple regex expression to capture a sentence such as **\[A-Z\].+\[.?!\]**
2. Loop each sentence for each book. I've included a python solution in solution/solve_challenge.py
3. You should get only one output. Put the output in the flag format.
4. flag = ATR\[The Cat only grinned when it saw Alice.\]

## Supporting solution info
[//]: <> (Add your full sulution scripts as files if applicable or use this section here to add a code block.)
```python
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
	known_hash_sha256 = "AAEEA96DD86D6ECBFE21755151B00266C79368584289D15B0BFE58D6B3498A36"
	byte_sha_value = sha_to_bytes(known_hash_sha256)
	get_books(byte_sha_value)
```
### Usage: ```python3 ./solve_challenge.py```

### Expected output:

```The Cat only grinned when it saw Alice.```

[//]: <> (Give an explination of the code and how to run it here. Make sure to explain the correct output so that anyone following allong can verify that it is running correctly.)
The code should run in Python3 without the need to install additional modules. You will need to place the books in **backup/books/** folder to run. The code does the following.

1. Converts the given SHA256 to bytes
2. Loads all the txt files.
3. Reads each in UTF-8 format.
4. Uses a Regex pattern to split the text into a list of sentences.
5. Converts each sentence into a SHA256 hash_sentence
6. Compairs against the known hash.
7. Outputs when an equivalent hash is found.

# Flag
[//]: <> (Add the flag below)
**ATR[The Cat only grinned when it saw Alice.]**
