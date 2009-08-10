#!/usr/bin/env python
from __future__ import absolute_import

import lib.amalgutils as amalgutils
amalgutils.enabletb()

import lib.template as template, lib.SQL as SQL

conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

cursor.execute("SELECT id, name FROM rooms")
rows = cursor.fetchall()
rooms = dict([(str(row['id']), row['name']) for row in rows])

result = {}
result['status'] = 'OK'
result['rooms'] = rooms

template.output_json(result)
