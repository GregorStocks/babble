from __future__ import absolute_import

import hashlib, random

def hash_pass(password, salt = None):
	if not salt:
		salt = hex(random.randint(0, 16 ** 10))
		# hopefully only the NSA has 16^10 sets of rainbow tables for sha1
	hash = hashlib.sha1(salt + password).hexdigest()
	return 'sha1$' + salt + '$' + hash
