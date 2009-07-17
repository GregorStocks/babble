from __future__ import absolute_import
import lib.template as template

def get_current_game_id(cursor, roomid):
	cursor.execute('''SELECT id FROM games
	WHERE roomid = %s ORDER BY id DESC LIMIT 1''', roomid)
	row = cursor.fetchone()
	if not row:
		return None
	else:
		return row['id']

def get_current_round_id(cursor, roomid):
	row = get_current_round_data(cursor, roomid)
	if not row:
		return None
	else:
		return row['id']

def get_current_round_data(cursor, roomid):
	cursor.execute('''
	SELECT rounds.id, rounds.starttime
	FROM rounds JOIN games ON rounds.gameid = games.id
	WHERE games.roomid = %s 
	ORDER BY rounds.id DESC LIMIT 1''', roomid)
	row = cursor.fetchone()
	if not row:
		return None
	else:
		return row

def add_event(cursor, roundid, eventtype, value = None):
	cursor.execute('INSERT INTO events (roundid, eventtype, value) VALUES (%s, %s, %s)',
		(roundid, eventtype, value))

def is_valid_room(cursor, roomid):
	cursor.execute('SELECT id FROM rooms WHERE id = %s', roomid)
	if cursor.fetchone():
		return True
	return False
