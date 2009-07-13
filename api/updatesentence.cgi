#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.template as template, lib.SQL as SQL

form = cgi.FieldStorage()

words = form.getlist("words")
sesskey = form.getfirst("sesskey")
errors = []
conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

if len(words) > 50:
	errors.append("This sentence is too long!")
if not sesskey:
	errors.append("No session key.")

if not errors:
	cursor.execute("SELECT id FROM users WHERE sesskey = %s LIMIT 1", sesskey)
	row = cursor.fetchone()
	if not row:
		errors.append("Invalid session key.")
	else:
		userid = row['id']

roundid = 0
if not errors:
	# get current roundid
	cursor.execute('SELECT id FROM rounds ORDER BY id DESC LIMIT 1')
	row = cursor.fetchone()
	if not row:
		errors.append("There don't seem to be any rounds at all!")
	else:
		roundid = row['id']

wordids = []
if not errors:
	# get all the words in the word list
	# it's kind of gross that the IN has to be constructed as a string beforehand, but
	# there doesn't seem to be any better way to do it
	wordstr = '(%s)' % ', '.join("'%s'" % conn.escape_string(word) for word in words)
	sql = '''SELECT words.id AS id, words.word AS word
		FROM words JOIN roundwords ON roundwords.wordid = words.id
		JOIN rounds	ON rounds.id = roundwords.roundid
		WHERE rounds.id = %s AND words.word IN ''' + wordstr
	cursor.execute(sql, roundid)
	rows = cursor.fetchall()
	for word in words:
		found = False
		for row in rows:
			if row['word'] == word:
				wordids.append(row['id'])
				found = True
				break
		if not found:
			errors.append("Invalid word: %s" % word) # TODO: fix this potential XSS vulnerability
			break

if not errors:
	# wipe old sentence
	cursor.execute('DELETE FROM sentences WHERE userid = %s AND roundid = %s',
		(userid, roundid))
	# insert new sentence
	cursor.executemany(
		'INSERT INTO sentences (userid, wordid, roundid) VALUES (%s, %s, %s)',
		[(userid, wordid, roundid) for wordid in wordids])

result = {}
if errors:
	result['status'] = 'Error'
	result['errors'] = errors
else:
	result['status'] = 'OK'

template.output_json(result)
