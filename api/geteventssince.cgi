#!/usr/bin/env python

import cgitb, cgi
cgitb.enable()

form = cgi.FieldStorage()
if(form.has_key("eventnum")):
	eventnum = form["eventnum"].value
	# TODO: return all events since that one.

import MySQLdb, json, random

def getConn():
	return MySQLdb.connect("localhost", "username", "password", "amalgam")

def getCursor(conn):
	return conn.cursor(MySQLdb.cursors.DictCursor)

def newWordList(conn):
	cursor = getCursor(conn)
	cursor.execute('SELECT * FROM words')
	rows = cursor.fetchall()
	wordrows = []
	for row in rows:
		for i in range(row["minnum"]):
			wordrows.append(row)

	numwords = 60

	while len(wordrows) < numwords:
		wordrows.append(random.choice(rows))

	# add it to the SQL server
	# WARNING! This probably only works on MySQL
	# TODO: make this use transaction?
	sql = 'INSERT INTO rounds () VALUES (); INSERT INTO roundwords (roundid, wordid) VALUES'
	sql += ', '.join(['(LAST_INSERT_ID(), ' + str(wordrow['id']) + ')' for wordrow in wordrows])
	cursor.execute(sql)
	return [wordrow['word'] for wordrow in wordrows]

def getWordList(roundnum, conn):
	cursor = getCursor(conn)
	cursor.execute('SELECT words.word FROM words JOIN roundwords ON roundwords.wordid = words.id JOIN rounds ON rounds.id = roundwords.roundid WHERE rounds.id = ' + str(roundnum)) # TODO: use prepared statements you ape
	rows = cursor.fetchall()
	return [row['word'] for row in rows]

conn = getConn()
words = newWordList(9, conn)
conn.close()

print "Content-type: text/plain;charset=utf-8"
# theoretically this should be served as application/json, but firefox opens up
# a "save" box for that instead of displaying it inline, which is inconvenient
# for debugging. 
print
print json.dumps([{'type': 'words', 'words': sorted(words, key=str.lower)}], indent=4)
