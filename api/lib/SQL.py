from __future__ import absolute_import

import MySQLdb
from .const import config

def get_conn():
	return MySQLdb.connect(config.SQL_NAME, config.SQL_USERNAME,
		config.SQL_PASSWORD, config.SQL_DATABASE)

def get_cursor(conn):
	return conn.cursor(MySQLdb.cursors.DictCursor)
