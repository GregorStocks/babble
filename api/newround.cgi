#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.SQL as SQL 
import lib.amalgutils as amalgutils
import lib.const.event as event
import lib.const.config as config
import lib.template as template
import random

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

conn = SQL.get_conn()
start_new_round(conn)

template.output_json(["IT'S ALL GOOD"])
