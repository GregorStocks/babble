from __future__ import absolute_import

def get_current_round(cursor):
	cursor.execute('SELECT id FROM rounds ORDER BY id DESC LIMIT 1')
	row = cursor.fetchone()
	if not row:
		return None
	else:
		return row['id']

def add_event(cursor, roundid, eventtype, value):
	cursor.execute('INSERT INTO events (roundid, eventtype, value) VALUES (%s, %s, %s)',
		(roundid, eventtype, value))
