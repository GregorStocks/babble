#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.template as template, lib.SQL as SQL, lib.amalgutils as amalgutils
import lib.const.event as event

form = cgi.FieldStorage()

sesskey = form.getfirst("sesskey")
roomid = form.getfirst("roomid")
text = form.getfirst("text")
errors = []
conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

if not sesskey:
	errors.append("No session key.")
if not amalgutils.is_valid_room(cursor, roomid):
	errors.append("Invalid room.")
if not text.strip():
	errors.append("No text.")

if not errors:
	cursor.execute("SELECT id FROM users WHERE sesskey = %s LIMIT 1", sesskey)
	row = cursor.fetchone()
	if not row:
		errors.append("Invalid session key.")
	else:
		userid = row['id']

if not errors:
	cursor.execute(
		"SELECT userid FROM roommembers WHERE userid = %s AND roomid = %s",
		(userid, roomid))
	if not cursor.fetchone():
		errors.append("You are not in this room!")

if not errors:
	cursor.execute("INSERT INTO chatmessages (userid, text) VALUES (%s, %s)",
		(userid, text))
	cursor.execute("SELECT LAST_INSERT_ID()")
	row = cursor.fetchone()
	if row:
		msgid = row['LAST_INSERT_ID()']
		roundid = amalgutils.get_current_round_id(cursor, roomid)
		if roundid:
			amalgutils.add_event(cursor, roundid, event.CHAT, msgid)

result = {}
if errors:
	result['status'] = 'Error'
	result['errors'] = errors
else:
	result['status'] = 'OK'

template.output_json(result)
