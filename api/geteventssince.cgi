#!/usr/bin/env python

import cgitb, cgi
cgitb.enable()


form = cgi.FieldStorage()
if(form.has_key("eventnum")):
	eventnum = form["eventnum"].value
	# TODO: return all events since that one.

import MySQLdb


db = MySQLdb.connect("localhost", "username", "password", "amalgam")
cursor = db.cursor()
sql = "SELECT word FROM words"
cursor.execute(sql)
rows = cursor.fetchall()
cursor.close()

print "Content-type: text/plain;charset=utf-8"
# theoretically this should be served as application/json, but firefox opens up
# a "save" box for that instead of displaying it inline, which is inconvenient
# for debugging. 
print
print """[{
	type: 'words',
	words: [
		""",

# TODO: more robust escaping for the JSON
print ',\n\t\t'.join(["'" + row[0].replace("'", "\\'") + "'" for row in rows])
print """
		]
}]"""
