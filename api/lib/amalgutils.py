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
	if not roomid:
		return False
	cursor.execute('SELECT id FROM rooms WHERE id = %s', roomid)
	if cursor.fetchone():
		return True
	return False

def get_winner_data(cursor, roundid):
	cursor.execute('''
	SELECT voters.username AS votername, votees.username AS voteename
	FROM votes
	JOIN users voters ON votes.userid = voters.id
	JOIN users votees ON votes.userid = votees.id
	WHERE votes.roundid = %s ORDER BY votes.id''', roundid)
	rows = cursor.fetchall()
	votes = {}
	votecounts = {}
	mostvotes = 0
	winner = None
	# TODO: a better winning algorithm than "whoever was first to get more 
	# votes than everyone else"
	# TODO: make it work with the people who got no votes too
	# TODO: make it return the sentences too
	for row in rows:
		voter = row['votername']
		votee = row['voteename']
		votes[voter] = votee

		if votee in votecounts:
			votecounts[votee] += 1
		else:
			votecounts[votee] = 1
		if votecounts[votee] > mostvotes:
			mostvotes = votecounts[votee]
			winner = votee
	# Note that currently usernames can only be alphanumeric + _, so there's no
	# need to sanitize, either here or clientside.
	return (winner, votes)
