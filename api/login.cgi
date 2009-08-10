#!/usr/bin/env python
from __future__ import absolute_import

import lib.amalgutils as amalgutils
amalgutils.enabletb()

import cgi
import lib.template as template, lib.SQL as SQL
import lib.auth as auth
import random
import lib.const.error as error

form = cgi.FieldStorage()

# TODO: This has massive overlap with updatesentence, refactor em 

username = form.getfirst('username')
password = form.getfirst('password')
errors = []

if not username:
	errors.append(error.NO_USERNAME)

if not password:
	errors.append(error.NO_PASSWORD)

sesskey = None
if not errors:
	conn = SQL.get_conn()
	cursor = SQL.get_cursor(conn)
	cursor.execute('SELECT id, passhash FROM users WHERE username = %s', username)
	row = cursor.fetchone()
	if row:
		passhash = row['passhash']
		if auth.hashes_to(form.getfirst('password'), passhash):
			# new session key
			sesskey = hex(random.randint(0, 16 ** 10))
			cursor.execute('UPDATE users SET sesskey = %s WHERE id = %s', (sesskey, row['id']))
	if not sesskey:
		errors.append(error.INVALID_LOGIN)

result = {}
if errors:
	result['status'] = 'Error'
	result['errors'] = errors
else:
	result['status'] = 'OK'
	result['sesskey'] = sesskey

template.output_json(result)
