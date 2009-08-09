from __future__ import absolute_import

import hashlib, random, re

def hash_pass(password, salt = None):
	if not salt:
		salt = hex(random.randint(0, 16 ** 10))
		# hopefully only the NSA has 16^10 sets of rainbow tables for sha1
	hash = hashlib.sha1(salt + password).hexdigest()
	return 'sha1$%s$%s' % (salt, hash)

def hashes_to(password, hash):
	# extract salt
	saltmatch = re.search('\$([^$]+)\$', hash)
	if not saltmatch:
		return False
	salt = saltmatch.groups()[0]
	if hash_pass(password, salt) == hash:
		return True
	return False
