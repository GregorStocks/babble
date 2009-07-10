#!/usr/bin/env python

import cgitb, cgi
cgitb.enable()


form = cgi.FieldStorage()
if(form.has_key("eventnum")):
	eventnum = form["eventnum"].value
	# TODO: return all events since that one.

import MySQLdb, json, random

db = MySQLdb.connect("localhost", "username", "password", "amalgam")
cursor = db.cursor(MySQLdb.cursors.DictCursor)
cursor.execute("SELECT word, minnum FROM words")
rows = cursor.fetchall()
cursor.close()

words = []
for row in rows:
	for i in range(row["minnum"]):
		words.append(row["word"])

numwords = 60

for i in range(numwords - len(words)):
	words.append(random.choice(rows)["word"])

print "Content-type: text/plain;charset=utf-8"
# theoretically this should be served as application/json, but firefox opens up
# a "save" box for that instead of displaying it inline, which is inconvenient
# for debugging. 
print
print json.dumps([{'type': 'words', 'words': sorted(words, key=str.lower)}], indent=4)
