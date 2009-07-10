#!/usr/bin/env python

import cgitb, cgi
cgitb.enable()


form = cgi.FieldStorage()
if(form.has_key("eventnum")):
	eventnum = form["eventnum"].value
	# TODO: return all events since that one.

import MySQLdb, json

db = MySQLdb.connect("localhost", "username", "password", "amalgam")
cursor = db.cursor(MySQLdb.cursors.DictCursor)
cursor.execute("SELECT word FROM words")
rows = cursor.fetchall()
cursor.close()

print "Content-type: text/plain;charset=utf-8"
# theoretically this should be served as application/json, but firefox opens up
# a "save" box for that instead of displaying it inline, which is inconvenient
# for debugging. 
print
print json.dumps([{'type': 'words', 'words': [row["word"] for row in rows]}], indent=4)
