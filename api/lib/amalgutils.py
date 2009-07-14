from __future__ import absolute_import

def get_current_round_id(cursor):
	row = get_current_round_data(cursor)
	if not row:
		return None
	else:
		return row['id']

def get_current_round_data(cursor):
	cursor.execute('SELECT id, starttime FROM rounds ORDER BY id DESC LIMIT 1')
	row = cursor.fetchone()
	if not row:
		return none
	else:
		return row

def add_event(cursor, roundid, eventtype, value):
	cursor.execute('INSERT INTO events (roundid, eventtype, value) VALUES (%s, %s, %s)',
		(roundid, eventtype, value))
