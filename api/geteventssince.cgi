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
import lib.updateround as updateround

def get_word_list(conn, roundid):
	cursor = SQL.get_cursor(conn)
	cursor.execute('''SELECT words.word AS word
	FROM words JOIN roundwords ON roundwords.wordid = words.id
	JOIN rounds ON rounds.id = roundwords.roundid
	WHERE rounds.id = %s''', roundid)
	rows = cursor.fetchall()
	return [row['word'] for row in rows]

def get_sentences(conn, roundid):
	cursor = SQL.get_cursor(conn)
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

def get_winners(conn, roundid):
	cursor = SQL.get_cursor(conn)
	cursor.execute('''
	SELECT users.username AS username
	FROM votes JOIN users ON votes.voteid = users.id
	WHERE votes.roundid = %s ORDER BY votes.id''', roundid)
	rows = cursor.fetchall()
	votes = {}
	mostvotes = 0
	winner = None
	# TODO: a better winning algorithm than "whoever was first to get more 
	# votes than everyone else"
	# TODO: make it work with the people who got no votes too
	# TODO: make it return the sentences too
	for row in rows:
		user = row['username']
		if user in votes:
			votes[user] += 1
		else:
			votes[user] = 1
		if votes[user] > mostvotes:
			mostvotes = votes[user]
			winner = user
	# Note that currently usernames can only be alphanumeric + _, so there's no
	# need to sanitize, either here or clientside.
	return (winner, votes)

def get_events_since(conn, eventid):
	cursor = SQL.get_cursor(conn)
	roundid = amalgutils.get_current_round_id(cursor)
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
			ev['words'] = sorted(get_word_list(conn, roundid), key=str.lower)
		elif eventtype == event.SENTENCE_MAKING_OVER:
			ev['type'] = 'collecting'
		elif eventtype == event.COLLECTING_OVER:
			ev['type'] = 'vote'
			ev['sentences'] = get_sentences(conn, roundid)
		elif eventtype == event.VOTING_OVER:
			ev['type'] = 'voting over' 
		elif eventtype == event.VOTE_COLLECTING_OVER:
			ev['type'] = 'winner'
			ev['winner'], ev['votes'] = get_winners(conn, roundid)
		events.append(ev)
	return events

conn = SQL.get_conn()

form = cgi.FieldStorage()
eventid = 0
if form.has_key("eventid"):
	eventid = form.getfirst('eventid')

updateround.update_round(conn)
events = get_events_since(conn, eventid)

template.output_json(events)
