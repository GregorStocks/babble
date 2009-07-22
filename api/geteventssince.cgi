#!/usr/bin/env python
from __future__ import absolute_import

import cgitb, cgi
cgitb.enable()

import random
import lib.SQL as SQL
import lib.template as template
import lib.amalgutils as amalgutils
import lib.const.event as event
import lib.const.config as config
import lib.update as update

def get_word_list(cursor, roundid):
	cursor.execute('''SELECT words.word AS word
	FROM words JOIN roundwords ON roundwords.wordid = words.id
	JOIN rounds ON rounds.id = roundwords.roundid
	WHERE rounds.id = %s''', roundid)
	rows = cursor.fetchall()
	return [row['word'] for row in rows]

def get_sentences(cursor, roundid):
	cursor.execute('''
	SELECT words.word AS word, sentences.id AS id, sentences.userid as userid
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

def get_events_since(cursor, eventid, roomid):
	roundid = amalgutils.get_current_round_id(cursor, roomid)
	if not roundid:
		return []
	events = []
	cursor.execute(
		'''SELECT eventtype, value, id FROM events WHERE roundid = %s AND id > %s''',
		(roundid, eventid))
	for row in cursor.fetchall():
		ev = {'eventid': row['id']}
		eventtype = row['eventtype']
		if eventtype == event.ROUND_START:
			ev['type'] = 'new round'
			ev['words'] = sorted(get_word_list(cursor, roundid), key=str.lower)
			ev['scores'] = amalgutils.get_scores(cursor, roomid)
			ev['players'] = amalgutils.get_room_member_names(cursor, roomid)
		elif eventtype == event.SENTENCE_MAKING_OVER:
			ev['type'] = 'collecting'
		elif eventtype == event.COLLECTING_OVER:
			ev['type'] = 'vote'
			ev['sentences'] = get_sentences(cursor, roundid)
		elif eventtype == event.VOTING_OVER:
			ev['type'] = 'voting over' 
		elif eventtype == event.VOTE_COLLECTING_OVER:
			ev['type'] = 'winner'
			ev['winner'], ev['votes'] = amalgutils.get_winner_data(cursor, roundid)
			ev['scores'] = amalgutils.get_scores(cursor, roomid)
		elif eventtype == event.GAME_OVER:
			ev['type'] = 'game over'
			ev['scores'] = amalgutils.get_scores(cursor, roomid)
		elif eventtype == event.JOIN:
			ev['type'] = 'join'
			ev['name'] = amalgutils.username_from_userid(cursor, row['value']) 
		elif eventtype == event.PART:
			ev['type'] = 'part'
			ev['name'] = amalgutils.username_from_userid(cursor, row['value']) 
		elif eventtype == event.CHAT:
			ev['type'] = 'chat'
			msg = amalgutils.chatmessage_from_id(cursor, row['value'])
			ev['username'] = msg['username']
			ev['text'] = msg['text']
		events.append(ev)
	return events

conn = SQL.get_conn()
cursor = SQL.get_cursor(conn)

form = cgi.FieldStorage()
eventid = 0
roomid = 0
if form.has_key("eventid"): 
	eventid = form.getfirst('eventid')
if form.has_key("roomid"):
	roomid = form.getfirst('roomid')

update.update_room(cursor, roomid)
events = get_events_since(cursor, eventid, roomid)

template.output_json(events)
