#!/usr/bin/env python
from __future__ import absolute_import

import lib.amalgutils as amalgutils
amalgutils.enabletb()

import cgi
import lib.template as template, lib.SQL as SQL
import lib.const.event as event
import lib.auth as auth
import lib.const.error as error

form = cgi.FieldStorage()

words = form.getlist("words")
sesskey = form.getfirst("sesskey")
roomid = form.getfirst("roomid")
errors = []
conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

if len(words) > 50: # seems reasonable
	errors.append(error.TOO_LONG)
if not sesskey:
	errors.append(error.NO_SESSKEY)
if not amalgutils.is_valid_room(cursor, roomid):
	errors.append(error.INVALID_ROOM)

if not errors:
	cursor.execute("SELECT id FROM users WHERE sesskey = %s LIMIT 1", sesskey)
	row = cursor.fetchone()
	if not row:
		errors.append(error.INVALID_SESSKEY)
	else:
		userid = row['id']

roundid = 0
if not errors:
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	if not roundid:
		errors.append(error.NO_ROUND)

if not errors:
	state = amalgutils.get_current_state(cursor, roundid)
	if state != event.ROUND_START and state != event.SENTENCE_MAKING_OVER:
		errors.append(error.NO_MAKING)

wordids = []
if not errors:
	# get all the words in the word list
	# it's kind of gross that the IN has to be constructed as a string beforehand, but
	# there doesn't seem to be any better way to do it
	sql = '''SELECT words.id AS id, words.word AS word
		FROM words JOIN roundwords ON roundwords.wordid = words.id
		JOIN rounds	ON rounds.id = roundwords.roundid
		WHERE rounds.id = %s'''
	cursor.execute(sql, roundid)
	rows = list(cursor.fetchall())
	origwords = rows[:]
	for word in words:
		found = False
		for row in rows:
			if row['word'] == word:
				wordids.append(row['id'])
				rows.remove(row)
				found = True
				break
		if not found:
			for row in rows:
				if row['word'] == '==' and not found:
					for origrow in origwords:
						if origrow['word'] == word:
							wordids.append(origrow['id'])
							rows.remove(row)
							found = True
							break
		if not found:
			errors.append(error.INVALID_WORD % word)
			break

if not errors:
	# wipe old sentence
	cursor.execute('DELETE FROM sentences WHERE userid = %s AND roundid = %s',
		(userid, roundid))
	# insert new sentence
	cursor.executemany(
		'INSERT INTO sentences (userid, wordid, roundid, hashedid) VALUES (%s, %s, %s, %s)',
		[(userid, wordid, roundid, auth.hash_pass(sesskey)) for wordid in wordids])

result = {}
if errors:
	result['status'] = 'Error'
	result['errors'] = errors
else:
	result['status'] = 'OK'

template.output_json(result)
