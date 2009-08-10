#!/usr/bin/env python
from __future__ import absolute_import

import lib.amalgutils as amalgutils
amalgutils.enabletb()

import cgi
import lib.template as template, lib.SQL as SQL
import lib.const.event as event
import lib.const.error as error

form = cgi.FieldStorage()

sesskey = form.getfirst("sesskey")
roomid = form.getfirst("roomid")
errors = []
conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

if not sesskey:
	errors.append(error.NO_SESSKEY)
if not amalgutils.is_valid_room(cursor, roomid):
	errors.append(error.INVALID_ROOM)

userid = 0
if not errors:
	cursor.execute("SELECT id FROM users WHERE sesskey = %s LIMIT 1", sesskey)
	row = cursor.fetchone()
	if not row:
		errors.append(error.INVALID_SESSKEY)
	else:
		userid = row['id']

if not errors:
	cursor.execute(
		"SELECT userid FROM roommembers WHERE userid = %s AND roomid = %s",
		(userid, roomid))
	if not cursor.fetchone():
		errors.append(error.NOT_IN_ROOM)

if not errors:
	cursor.execute("DELETE FROM roommembers WHERE userid = %s AND roomid = %s",
		(userid, roomid))
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	if roundid:
		amalgutils.add_event(cursor, roundid, event.PART, userid)

result = {}
if errors:
	result['status'] = 'Error'
	result['errors'] = errors
else:
	result['status'] = 'OK'

template.output_json(result)
