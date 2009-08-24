from __future__ import absolute_import

import MySQLdb
from .const import config
import sqlalchemy.pool as pool

mysql = pool.manage(MySQLdb)

def get_conn():
	return mysql.connect(config.SQL_NAME, config.SQL_USERNAME,
		config.SQL_PASSWORD, config.SQL_DATABASE)

def get_cursor(conn):
	return conn.cursor(mysql.cursors.DictCursor)
