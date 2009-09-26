#!/usr/bin/env python
from __future__ import absolute_import

import lib.amalgutils as amalgutils
amalgutils.enabletb()

import lib.template as template, lib.SQL as SQL

conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

cursor.execute("""SELECT rooms.id AS id, rooms.name AS name, users.username AS username
                  FROM rooms LEFT OUTER JOIN (roommembers JOIN users ON users.id = roommembers.userid)
				  ON rooms.id = roommembers.roomid""")
rows = cursor.fetchall()
rooms = {}
for row in rows:
	if row['username'] != None:
		if row['id'] in rooms:
			rooms[row['id']]['users'].append(row['username'])
		else:
			rooms[row['id']] = {'name': row['name'], 'users': [row['username']]}
	else:
		if row['id'] not in rooms:
			rooms[row['id']] = {'name': row['name'], 'users': []}

result = {}
result['status'] = 'OK'
result['rooms'] = rooms

template.output_json(result)
