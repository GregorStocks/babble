CREATE TABLE IF NOT EXISTS rounds (id INT NOT NULL AUTO_INCREMENT, starttime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, gameid INT NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS words (id INT NOT NULL AUTO_INCREMENT, word TEXT NOT NULL, minnum INT NOT NULL DEFAULT 0, PRIMARY KEY (id), UNIQUE (word(50)));
CREATE TABLE IF NOT EXISTS roundwords (id INT NOT NULL AUTO_INCREMENT, roundid INT NOT NULL, wordid INT NOT NULL,  PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, username TEXT NOT NULL, passhash TEXT NOT NULL, email TEXT DEFAULT NULL, sesskey TEXT DEFAULT NULL, regtime TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id), UNIQUE (username(20))); CREATE TABLE IF NOT EXISTS sentences (id INT NOT NULL AUTO_INCREMENT, roundid INT NOT NULL, wordid INT NOT NULL, userid INT NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS events (id INT NOT NULL AUTO_INCREMENT, roundid INT NOT NULL, eventtype INT NOT NULL, value INT DEFAULT NULL, time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS votes (id INT NOT NULL AUTO_INCREMENT, userid INT NOT NULL, voteid INT NOT NULL, roundid INT NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS rooms (id INT NOT NULL AUTO_INCREMENT, name TEXT NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS roommembers (id INT NOT NULL AUTO_INCREMENT, userid INT NOT NULL, roomid INT NOT NULL, lastping TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS games (id INT NOT NULL AUTO_INCREMENT, roomid INT NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS chatmessages (id INT NOT NULL AUTO_INCREMENT, userid INT NOT NULL, text TEXT NOT NULL, PRIMARY KEY (id));

-- add the default wordlist
INSERT INTO words (word, minnum) VALUES
-- probably some of these arent really in the right part of speech because i dont understand english and also didnt initially sort
-- em all right. deal w/it
-- nouns
('butt', 0), ('human', 0), ('pair', 0), ('thing', 0),

-- verbs
('is', 1), ('are', 1), ('eat', 0), ('kill', 0), ('cuss', 0), ('ain''t', 0)

-- adjectives
('other', 0), ('groggy', 0), ('ugly', 0), ('dumb', 0), ('fat', 0), ('phat', 0),

-- interjections
('hello', 0), ('bye', 0), ('goodbye', 0), ('cheers', 0), ('yes', 0), ('no', 0),
('okay', 0), ('sorry', 0), ('whew', 0), ('''sblood', 0), ('amen', 0),

-- prepositions
('through', 0), ('on', 0), ('top', 0), ('of', 0), ('with', 0), ('for', 0),
('to', 0), ('from', 0), ('until', 0), ('even', 0), ('so', 0), ('well', 0),
('still', 0), ('yet', 0), ('also', 0), ('and', 0), ('too', 0), ('but', 0),
('while', 0),

-- pronouns
('I', 1), ('you', 1), ('he', 0), ('she', 0), ('they', 0), ('it', 0), ('we', 0),
('me', 0), ('him', 0), ('her', 0), ('them', 0), ('it', 0),
('myself', 0), ('yourself', 0), ('himself', 0), ('herself', 0), ('themself', 0),
('itself', 0), 
('my', 0), ('your', 0), ('his', 0), ('their', 0),
('who', 0), 

-- determiners and probably a few other thigns who knows
('a', 2), ('the', 1),
('this', 0), ('that', 0), ('these', 0), ('those', 0), ('which', 0),
('all', 0), ('both', 0), ('few', 0), ('many', 0), ('some', 0), ('every', 0),
('any', 0), ('each', 0), ('either', 0), ('neither', 0),
('nine', 0), ('four', 0), ('six', 0), ('eighty', 0),
('another', 0), ('certain', 0), ('less', 0),
('more', 0), ('that', 0), ('these', 0), ('those', 0),
('which', 0),

-- punctuation
('!', 1), ('.', 1), ('?', 1), (',', 1), (';', 0), (':', 0), ('...', 0),
('-', 0), ('/', 0), ('''', 0),

-- prefixes
('pro-', 0), ('anti-', 0), ('post-', 0), ('pre-', 0), ('de-', 0), ('super-', 0),
('ex-', 0), ('un-', 0), ('tri-', 0), ('centi-', 0), ('kilo-', 0), ('milli-', 0),
('multi-', 0), ('semi-', 0), ('mis-', 0), ('ultra-', 0),

-- suffixes
('-ize', 0), ('-ify', 0), ('-s', 2), ('-y', 1), ('-ly', 1), ('-ed', 1), ('-ing', 0),
('-able', 0), ('-ic', 0), ('-est', 0), ('-ness', 0), ('-icious', 0), ('-al', 0),
('-ful', 0), ('-holic', 0), ('-ism', 0), ('-ist', 0), ('-itude', 0),
('-less', 0), ('-phagia', 0), ('-ee', 0), ('-ese', 0), ('-ate', 0),
('-cide', 0), ('-er', 0), ('-fy', 0), ('-ism', 0), ('-ize', 0);

-- add a room
INSERT INTO rooms (name) VALUES ("Room 1");
