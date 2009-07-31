WORDS_PER_ROUND = 80

# all times are in seconds
SENTENCE_MAKING_TIME = 30

# can't change sentences, but ones made just before the deadline are accepted
# potentially exploitable by badly-behaved clients, so shouldn't be very big
SENTENCE_COLLECTING_TIME = 2 

VOTING_TIME = 20

VOTE_COLLECTING_TIME = 2

WINNER_VIEWING_TIME = 10

GAME_WINNER_VIEWING_TIME = 30

POINTS_TO_WIN = 30
POINTS_FOR_WINNING_ROUND = 3
POINTS_FOR_VOTING_WINNER = 1

# if someone doesn't ping for this long, they're auto-kicked
IDLE_TIMEOUT = 30

SQL_NAME = "localhost"
SQL_USERNAME = "username"
SQL_PASSWORD = "password"
SQL_DATABASE = "amalgam"
