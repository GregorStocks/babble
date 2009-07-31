#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import random
import lib.SQL as SQL
import lib.template as template
import lib.amalgutils as amalgutils
import lib.const.event as event
import lib.const.config as config
import lib.update as update

def get_events_since(cursor, eventid, roomid):
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	if not roundid:
		return []
	events = []
	cursor.execute(
		'''SELECT eventtype, value, id, CURRENT_TIMESTAMP - time AS timespent
		FROM events WHERE roundid = %s AND id > %s''',
		(roundid, eventid))
	for row in cursor.fetchall():
		events.append(amalgutils.get_event(cursor, roundid, roomid, row))
	return events

conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

form = cgi.FieldStorage()
eventid = 0
roomid = 0
if form.has_key("eventid"): 
	eventid = form.getfirst('eventid')
if form.has_key("roomid"):
	roomid = form.getfirst('roomid')

update.update_room(cursor, roomid)
events = get_events_since(cursor, eventid, roomid)

template.output_json(events)
