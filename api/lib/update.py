#!/usr/bin/env python
from __future__ import absolute_import

import lib.SQL as SQL 
import lib.amalgutils as amalgutils
import lib.const.event as event
import lib.const.config as config
import lib.template as template
import lib.const.event as event
import lib.const.wordtype as wordtype
import random
import datetime

def start_new_game(cursor, roomid):
	cursor.execute('INSERT INTO games (roomid) VALUES (%s)', roomid)
	start_new_round(cursor, roomid)

def game_is_over(cursor, roomid):
	points = amalgutils.get_scores(cursor, roomid)
	for username in points:
		if points[username] >= config.POINTS_TO_WIN:
			return True
	return False

def round_end(cursor, roomid):
	if game_is_over(cursor, roomid):
		roundid = amalgutils.get_current_round_id(cursor, roomid)
		amalgutils.add_event(cursor, roundid, event.GAME_OVER)
	else:
		start_new_round(cursor, roomid)

def collected(cursor, roomid):
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	cursor.execute('SELECT id FROM sentences WHERE roundid = %s LIMIT 1', roundid)
	if cursor.fetchone():
		amalgutils.add_event(cursor, roundid, event.COLLECTING_OVER)
	else:
		round_end(cursor, roomid)

def start_new_round(cursor, roomid):
	cursor.execute('SELECT word, minnum, id FROM words WHERE minnum > 0')
	# TODO: do this without having to fetch every single word from the database
	rows = cursor.fetchall()
	wordrows = []
	for row in rows:
		for i in range(row["minnum"]):
			wordrows.append(row)
	
	for x in xrange(len(wordtype.TYPES)):
		# I'm pretty confident there's a better way to do this but I'm also pretty confident that this works well enough
		template.debug(config.WORDS_PER_ROUND)
		template.debug(len(wordrows))
		template.debug(len(wordtype.TYPES))
		template.debug(x)
		template.debug(round((config.WORDS_PER_ROUND - len(wordrows) / (len(wordtype.TYPES) - x))))
		cursor.execute('SELECT word, minnum, id FROM words WHERE wordtype = %s ORDER BY RAND() LIMIT %s',
			(x, round((config.WORDS_PER_ROUND - len(wordrows)) / (len(wordtype.TYPES) - x))))
		rows = cursor.fetchall()
		for row in rows:
			wordrows.append(row)

	# add it to the database
	# this is not actually a race condition - last_insert_id is per-connection
	# note that this will only work unmodified on MySQL
	gameid = amalgutils.get_current_game_id(cursor, roomid)
	cursor.execute('INSERT INTO rounds (gameid) VALUES (%s)', gameid)
	cursor.execute('SELECT LAST_INSERT_ID()')
	row = cursor.fetchone()
	roundid = row['LAST_INSERT_ID()']
	cursor.executemany(
		'INSERT INTO roundwords (roundid, wordid) VALUES (%s, %s)',
		[(roundid, wordrow['id']) for wordrow in wordrows])

	amalgutils.add_event(cursor, roundid, event.ROUND_START)

def update_room(cursor, roomid):
	# TODO: make this work properly when you call it a bunch of times
	if not amalgutils.is_valid_room(cursor, roomid):
		return
	gameid = amalgutils.get_current_game_id(cursor, roomid)
	round = amalgutils.get_current_round_data(cursor, roomid)
	if not gameid:
		start_new_game(cursor, roomid)
		return
	if not round:
		start_new_round(cursor, roomid)
		return
	# kick everyone who hasnt pinged recently
	cursor.execute('''SELECT id, userid FROM roommembers
		WHERE TIMESTAMPDIFF(SECOND, lastping, CURRENT_TIMESTAMP) > %s''',
		config.IDLE_TIMEOUT)
	rows = cursor.fetchall()
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	if roundid:
		for row in rows:
			amalgutils.add_event(cursor, roundid, event.PART, row['userid'])
			cursor.execute('DELETE FROM roommembers WHERE id = %s', row['id'])

	cursor.execute('''
		SELECT eventtype, value, id, time FROM events WHERE roundid = %s
		AND eventtype <= %s ORDER BY time DESC''',
		(round['id'], event.GAME_OVER))
	row = cursor.fetchone()
	if row:
		cureventtime = row['time']
		cureventtype = row['eventtype']
		eventtypes = (
			(event.ROUND_START, config.SENTENCE_MAKING_TIME, event.SENTENCE_MAKING_OVER, None),
			(event.SENTENCE_MAKING_OVER, config.SENTENCE_COLLECTING_TIME, None, collected),
			(event.COLLECTING_OVER, config.VOTING_TIME, event.VOTING_OVER, None),
			(event.VOTING_OVER, config.VOTE_COLLECTING_TIME, event.VOTE_COLLECTING_OVER, None),
			(event.VOTE_COLLECTING_OVER, config.WINNER_VIEWING_TIME, None, round_end),
			(event.GAME_OVER, config.GAME_WINNER_VIEWING_TIME, None, start_new_game))
		for x in eventtypes:
			eventtype, time, nexttype, action = x
			if cureventtype == eventtype:
				delta = datetime.timedelta(seconds = time) 
				if datetime.datetime.today() - cureventtime > delta:
					if nexttype:
						amalgutils.add_event(cursor, round['id'], nexttype) 
					if action:
						action(cursor, roomid)
				break
