import MySQLdb

def getConn():
	return MySQLdb.connect("localhost", "username", "password", "amalgam")

def getCursor(conn):
	return conn.cursor(MySQLdb.cursors.DictCursor)
