#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.template as template, lib.SQL as SQL, lib.amalgutils as amalgutils
import lib.const.event as event

form = cgi.FieldStorage()


# TODO: This has massive overlap with updatesentence, refactor em 

sesskey = form.getfirst("sesskey")
sentenceid = form.getfirst("sentenceid")
errors = []
conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)
userid = None

if not sesskey:
	errors.append(error.NO_SESSKEY)
if not sentenceid:
	errors.append(error.NO_SENTENCE)

if not errors:
	cursor.execute("SELECT id FROM users WHERE sesskey = %s LIMIT 1", sesskey)
	row = cursor.fetchone()
	if not row:
		errors.append(error.INVALID_SESSKEY)
	else:
		userid = row['id']

voteid = None
roundid = None
if not errors:
	cursor.execute("SELECT userid, roundid FROM sentences WHERE hashedid = %s", sentenceid)
	row = cursor.fetchone()
	if not row:
		errors.append(error.INVALID_SENTENCE)
	else:
		voteid = row["userid"]
		roundid = row['roundid']
		if voteid == userid:
			errors.append(error.NO_VOTE_SELF)

if not errors:
	cursor.execute("SELECT userid FROM sentences WHERE userid = %s AND roundid = %s", (userid, roundid))
	if not cursor.fetchone():
		errors.append(error.NO_SENTENCE_SUBMITTED)

if not errors:
	state = amalgutils.get_current_state(cursor, roundid)
	if state != event.COLLECTING_OVER and state != event.VOTING_OVER:
		errors.append(error.NO_VOTING)

if not errors:
	cursor.execute('DELETE FROM votes WHERE userid = %s AND roundid = %s',
		(userid, roundid))
	cursor.execute(
		'INSERT INTO votes (userid, voteid, roundid) VALUES (%s, %s, %s)',
		(userid, voteid, roundid))

result = {}
if errors:
	result['status'] = 'Error'
	result['errors'] = errors
else:
	result['status'] = 'OK'

template.output_json(result)
