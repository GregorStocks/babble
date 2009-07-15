from __future__ import absolute_import

WORDS_PER_ROUND = 60

# all times are in seconds
SENTENCE_MAKING_TIME = 30

# can't change sentences, but ones made just before the deadline are accepted
# potentially exploitable by badly-behaved clients, so shouldn't be very big
SENTENCE_COLLECTING_TIME = 2 

VOTING_TIME = 30

VOTE_COLLECTING_TIME = 2

WINNER_VIEWING_TIME = 10

SQL_NAME = "localhost"
SQL_USERNAME = "username"
SQL_PASSWORD = "password"
SQL_DATABASE = "amalgam"
