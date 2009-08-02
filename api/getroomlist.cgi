#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import lib.template as template, lib.SQL as SQL, lib.amalgutils as amalgutils

conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

cursor.execute("SELECT id, name FROM rooms")
rows = cursor.fetchall()
rooms = dict([(str(row['id']), row['name']) for row in rows])
template.output_json(rooms)
