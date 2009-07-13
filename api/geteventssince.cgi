#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.SQL as SQL, json, random, lib.template as template
import lib.amalgutils as amalgutils
import lib.const.event as event
import lib.const.config as config

def start_new_round(conn):
	cursor = SQL.get_cursor(conn)
	cursor.execute('SELECT word, minnum, id FROM words')
	rows = cursor.fetchall()
	wordrows = []
	for row in rows:
		for i in range(row["minnum"]):
			wordrows.append(row)

	while len(wordrows) < config.WORDS_PER_ROUND:
		wordrows.append(random.choice(rows))

	# add it to the SQL server
	# this is not actually a race condition - last_insert_id is per-connection
	# note that this will only work unmodified on MySQL
	cursor.execute('INSERT INTO rounds () VALUES ()')
	cursor.execute('SELECT LAST_INSERT_ID()')
	row = cursor.fetchone()
	roundid = row['LAST_INSERT_ID()']
	cursor.executemany(
		'INSERT INTO roundwords (roundid, wordid) VALUES (%s, %s)',
		[(roundid, wordrow['id']) for wordrow in wordrows])

	amalgutils.add_event(cursor, roundid, event.ROUND_START, None)

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
		ev = {'id': row['id']}
		eventtype = row['eventtype']
		if eventtype == event.ROUND_START:
			ev['type'] = 'new round'
			ev['words'] = sorted(get_word_list(conn, roundid), key=str.lower)
		events.append(ev)
	return events

conn = SQL.get_conn()

form = cgi.FieldStorage()
if form.has_key("eventnum"):
	eventnum = form.getfirst('eventnum')
	if eventnum.strip() == '10':
		start_new_round(conn)
	# TODO: return all events since that one.

events = get_events_since(conn, 0)

template.output_json(events)
