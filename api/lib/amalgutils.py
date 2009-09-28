from __future__ import absolute_import
import lib.const.config as config
import lib.const.event as event
import cgitb

def enabletb():
	cgitb.enable(format = "text")

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
	FROM votes JOIN users voters ON votes.userid = voters.id
	JOIN users votees ON votes.voteid = votees.id
	WHERE votes.roundid = %s ORDER BY votes.id''', roundid)
	rows = cursor.fetchall()
	votes = {}
	votecounts = {}
	for row in rows:
		voter = row['votername']
		votee = row['voteename']
		votes[voter] = votee
	
	for voter in votes:
		votee = votes[voter]
		if votee in votes:
			if votee in votecounts:
				votecounts[votee] += 1
			else:
				votecounts[votee] = 1
	
	# got the votes, now get the sentences
	cursor.execute('''
	SELECT words.word AS word, sentences.id AS id, users.username as username
	FROM sentences JOIN rounds ON sentences.roundid = rounds.id
	JOIN users ON sentences.userid = users.id
	JOIN words ON sentences.wordid = words.id
	WHERE rounds.id = %s ORDER BY sentences.id''', roundid)

	sentences_by_user = {}
	rows = cursor.fetchall()
	for row in rows:
		if row['username'] in sentences_by_user:
			sentences_by_user[row['username']].append(row['word'])
		else:
			sentences_by_user[row['username']] = [row['word']]
	
	data = {}
	for username in sentences_by_user:
		dat = {}
		dat['sentence'] = sentences_by_user[username]
		
		if username in votecounts:
			dat['votes'] = votecounts[username]
		else:
			dat['votes'] = 0
		
		if username in votes:
			dat['vote'] = votes[username]
			dat['points'] = dat['votes']
		else:
			dat['vote'] = None
			dat['points'] = 0
		
		dat['iswinner'] = False
		
		data[username] = dat
	
	longestlength = 0
	mostvotes = 0
	winner = None
	for username in votecounts:
		if username not in votes: # didn't vote, can't win
			continue
		if username not in sentences_by_user: # shouldn't happen
			continue
		if votecounts[username] > mostvotes:
			winner = username
			longestlength = len(sentences_by_user[username])
			mostvotes = votecounts[username]
		elif votecounts[username] == mostvotes and len(sentences_by_user[username]) > longestlength:
			winner = username
	
	if winner:
		data[winner]['iswinner'] = True
		data[winner]['points'] += config.POINTS_FOR_WINNING_ROUND
		for username in data:
			if data[username]['vote'] == winner:
				data[username]['points'] += config.POINTS_FOR_VOTING_WINNER

	# Note that currently usernames can only be alphanumeric + _, so there's no
	# need to sanitize, either here or clientside.
	return data

def get_scores(cursor, roomid):
	# TODO: benchmark, denormalizing could make this way more efficient probably
	cursor.execute('''SELECT users.username AS username
	FROM roommembers JOIN users ON roommembers.userid = users.id
	WHERE roommembers.roomid = %s''', roomid)
	rows = cursor.fetchall()
	points = {}
	for row in rows:
		points[row['username']] = 0
			
	curgameid = get_current_game_id(cursor, roomid)
	cursor.execute('SELECT id FROM rounds WHERE gameid = %s', curgameid)
	rows = cursor.fetchall()
	for row in rows:
		roundid = row['id']
		data = get_winner_data(cursor, roundid)
		for username in data:
			if username in points:
				points[username] = data[username]['points']
	return points

def username_from_userid(cursor, userid):
	cursor.execute('SELECT username FROM users WHERE id = %s', userid)
	row = cursor.fetchone()
	if row:
		return row['username']
	else:
		return None

def get_room_member_names(cursor, roomid):
	cursor.execute('''SELECT users.username AS username
		FROM users JOIN roommembers ON users.id = roommembers.userid
		WHERE roommembers.roomid = %s''', roomid)
	rows = cursor.fetchall()
	names = []
	for row in rows:
		names.append(row['username'])
	return names

def chatmessage_from_id(cursor, id):
	cursor.execute('''SELECT users.username AS username, chatmessages.text AS text
		FROM chatmessages JOIN users ON users.id = chatmessages.userid
		WHERE chatmessages.id = %s''', id)
	return cursor.fetchone()

def get_current_state(cursor, roundid):
	cursor.execute(
		'SELECT eventtype FROM events WHERE roundid = %s AND eventtype <= %s ORDER BY id DESC',
		(roundid, event.GAME_OVER))
	row = cursor.fetchone()
	if row:
		return row['eventtype']
	else:
		return None

def get_event(cursor, roundid, roomid, row):
	ev = {'eventid': row['id']}
	eventtype = row['eventtype']
	times = {
		event.ROUND_START: config.SENTENCE_MAKING_TIME,
		event.SENTENCE_MAKING_OVER: config.SENTENCE_COLLECTING_TIME,
		event.COLLECTING_OVER: config.VOTING_TIME,
		event.VOTING_OVER: config.VOTE_COLLECTING_TIME,
		event.VOTE_COLLECTING_OVER: config.WINNER_VIEWING_TIME,
		event.GAME_OVER: config.GAME_WINNER_VIEWING_TIME
	}
	if eventtype in times:
		ev["timeleft"] = times[eventtype] - row["timespent"]
	if eventtype == event.ROUND_START:
		ev['type'] = 'new round'
		ev['words'] = get_word_list(cursor, roundid)
	elif eventtype == event.SENTENCE_MAKING_OVER:
		ev['type'] = 'collecting'
	elif eventtype == event.COLLECTING_OVER:
		ev['type'] = 'vote'
		ev['sentences'] = get_sentences(cursor, roundid)
	elif eventtype == event.VOTING_OVER:
		ev['type'] = 'voting over' 
	elif eventtype == event.VOTE_COLLECTING_OVER:
		ev['type'] = 'winner'
		ev['data'] = get_winner_data(cursor, roundid)
	elif eventtype == event.GAME_OVER:
		ev['type'] = 'game over'
	elif eventtype == event.JOIN:
		ev['type'] = 'join'
		username = username_from_userid(cursor, row['value']) 
		ev['score'] = get_scores(cursor, roomid)[username]
		ev['name'] = username
	elif eventtype == event.PART:
		ev['type'] = 'part'
		ev['name'] = username_from_userid(cursor, row['value']) 
	elif eventtype == event.CHAT:
		ev['type'] = 'chat'
		msg = chatmessage_from_id(cursor, row['value'])
		ev['username'] = msg['username']
		ev['text'] = msg['text']
	return ev

def get_word_list(cursor, roundid):
	cursor.execute('''SELECT words.word AS word
	FROM words JOIN roundwords ON roundwords.wordid = words.id
	JOIN rounds ON rounds.id = roundwords.roundid
	WHERE rounds.id = %s ORDER BY roundwords.id''', roundid)
	rows = cursor.fetchall()
	return [row['word'] for row in rows]

def get_sentences(cursor, roundid):
	cursor.execute('''
	SELECT words.word AS word, sentences.hashedid AS id, sentences.userid as userid
	FROM sentences JOIN rounds ON sentences.roundid = rounds.id
	JOIN words ON sentences.wordid = words.id
	WHERE rounds.id = %s ORDER BY sentences.id''', roundid)

	# combine sentences
	sentences_by_user = {}
	rows = cursor.fetchall()
	for row in rows:
		if row['userid'] in sentences_by_user:
			sentences_by_user[row['userid']].append(row['word'])
		else:
			sentences_by_user[row['userid']] = [row['word']]
	# give arbitrary IDs so mean clients can't do mean things
	sentences = {}
	for row in rows:
		if row['userid'] in sentences_by_user:
			sentences[str(row['id'])] = sentences_by_user[row['userid']]
			del sentences_by_user[row['userid']]
	return sentences
