from __future__ import absolute_import

import MySQLdb

def get_conn():
	return MySQLdb.connect("localhost", "username", "password", "amalgam")

def get_cursor(conn):
	return conn.cursor(MySQLdb.cursors.DictCursor)
