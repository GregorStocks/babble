#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.SQL as SQL, json, random, lib.template as template
import lib.amalgutils as amalgutils
import lib.const.event as event
import lib.const.config as config

def get_word_list(conn, roundid):
	cursor = SQL.get_cursor(conn)
	cursor.execute('''SELECT words.word AS word
	FROM words JOIN roundwords ON roundwords.wordid = words.id
	JOIN rounds ON rounds.id = roundwords.roundid
	WHERE rounds.id = %s''', roundid)
	rows = cursor.fetchall()
	return [row['word'] for row in rows]

def get_events_since(conn, eventid):
	cursor = SQL.get_cursor(conn)
	roundid = amalgutils.get_current_round(cursor)
	events = []
	cursor.execute(
		'''SELECT eventtype, value, id FROM events WHERE roundid = %s AND id > %s''',
		(roundid, eventid))
	for row in cursor.fetchall():
		ev = {'eventid': row['id']}
		eventtype = row['eventtype']
		if eventtype == event.ROUND_START:
			ev['type'] = 'new round'
			ev['words'] = sorted(get_word_list(conn, roundid), key=str.lower)
		events.append(ev)
	return events

conn = SQL.get_conn()

form = cgi.FieldStorage()
eventnum = 0
if form.has_key("eventid"):
	eventid = form.getfirst('eventid')

events = get_events_since(conn, eventid)

template.output_json(events)
