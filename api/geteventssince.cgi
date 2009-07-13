#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

form = cgi.FieldStorage()
if(form.has_key("eventnum")):
	eventnum = form["eventnum"].value
	# TODO: return all events since that one.

import lib.SQL as SQL, json, random, lib.template as template

def newWordList(conn):
	cursor = SQL.get_cursor(conn)
	cursor.execute('SELECT word, minnum, id FROM words')
	rows = cursor.fetchall()
	wordrows = []
	for row in rows:
		for i in range(row["minnum"]):
			wordrows.append(row)

	numwords = 60

	while len(wordrows) < numwords:
		wordrows.append(random.choice(rows))

	# add it to the SQL server
	# WARNING! This probably only works on MySQL.
	# TODO: make this use cursor.executemany by storing last_insert_id
	sql = 'INSERT INTO rounds () VALUES ();'
	roundwords = ', '.join(['(LAST_INSERT_ID(), %s)' for wordrow in wordrows])
	sql += 'INSERT INTO roundwords (roundid, wordid) VALUES %s' % roundwords
	cursor.execute(sql, [wordrow['id'] for wordrow in wordrows])
	return [wordrow['word'] for wordrow in wordrows]

def getWordList(roundnum, conn):
	cursor = SQL.get_cursor(conn)
	cursor.execute('''SELECT words.word AS word
	FROM words JOIN roundwords ON roundwords.wordid = words.id
	JOIN rounds ON rounds.id = roundwords.roundid
	WHERE rounds.id = %s''', roundnum)
	rows = cursor.fetchall()
	return [row['word'] for row in rows]

conn = SQL.get_conn()
words = newWordList(conn)

template.output_json([{'type': 'words', 'words': sorted(words, key=str.lower)}])
