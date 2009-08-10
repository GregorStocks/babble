#!/usr/bin/env python
from __future__ import absolute_import

import lib.amalgutils as amalgutils
amalgutils.enabletb()

import lib.SQL as SQL
import lib.template as template
import lib.const.event as event
import lib.update as update
import cgi

def last_event_id(cursor, roomid):
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	cursor.execute('SELECT id FROM events WHERE roundid = %s ORDER BY id DESC LIMIT 1', roundid)
	row = cursor.fetchone()
	if row:
		return row['id']
	else:
		return 0

def get_last_event(cursor, eventid, roomid):
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	if not roundid:
		return None
	cursor.execute(
		'''SELECT eventtype, value, id, CURRENT_TIMESTAMP - time AS timespent
		FROM events WHERE roundid = %s AND eventtype <= %s ORDER BY id DESC LIMIT 1''',
		(roundid, event.GAME_OVER))
	return amalgutils.get_event(cursor, roundid, roomid, cursor.fetchone())

conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

form = cgi.FieldStorage()
eventid = 0
roomid = 0
if form.has_key("roomid"):
	roomid = form.getfirst('roomid')

update.update_room(cursor, roomid)

state = {
	"event": get_last_event(cursor, eventid, roomid),
	"eventid": last_event_id(cursor, roomid),
	"scores": amalgutils.get_scores(cursor, roomid),
	"players": amalgutils.get_room_member_names(cursor, roomid)
}

result = {}
result['status'] = 'OK'
result['state'] = state

template.output_json(result)
