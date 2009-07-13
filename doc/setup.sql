CREATE TABLE IF NOT EXISTS rounds (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY  (id));
CREATE TABLE IF NOT EXISTS words (id INT NOT NULL AUTO_INCREMENT, word TEXT NOT NULL, minnum INT NOT NULL DEFAULT 0, PRIMARY KEY (id), UNIQUE (word(50)));
CREATE TABLE IF NOT EXISTS roundwords (id INT NOT NULL AUTO_INCREMENT, roundid INT NOT NULL, wordid INT NOT NULL,  PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS users (id INT NOT NULL AUTO_INCREMENT, username TEXT NOT NULL, passhash TEXT NOT NULL, email TEXT DEFAULT NULL, sesskey TEXT DEFAULT NULL, PRIMARY KEY (id), UNIQUE (username(20)));
CREATE TABLE IF NOT EXISTS sentences (id INT NOT NULL AUTO_INCREMENT, roundid INT NOT NULL, wordid INT NOT NULL, userid INT NOT NULL, PRIMARY KEY (id));

-- add the default wordlist
INSERT INTO words (word, minnum) VALUES
('I', 1), ('you', 1), ('he', 0), ('she', 0), ('they', 0), ('it', 0), ('we', 0),
('is', 1), ('are', 1), ('eat', 0), ('kill', 0), ('cuss', 0),
('butt', 0), ('human', 0), ('pair', 0),
('groggy', 0), ('ugly', 0), ('dumb', 0), ('fat', 0), ('phat', 0),
('-s', 2), ('-y', 1), ('-ly', 1), ('-ize', 0), ('-ify', 0), ('-ed', 1),
('pro-', 0), ('anti-', 0),
('nine', 0), ('four', 0), ('six', 0), ('eighty', 0),
('ain''t', 0),
('myself', 0), ('yourself', 0), ('herself', 0),
('my', 0), ('your', 0), ('her', 0), ('his', 0),
('other', 0),
('who', 0),
('through', 0), ('on', 0), ('top', 0), ('of', 0), ('with', 0), ('for', 0), ('to', 0), ('from', 0), ('until', 0),
('even', 0), ('so', 0), ('well', 0),
('still', 0), ('yet', 0), ('also', 0), ('and', 0), ('too', 0), ('but', 0), ('while', 0),
('a', 2), ('the', 1), ('any', 0), ('many', 0),
('!', 1), ('.', 1), ('?', 1),
(',', 1), (';', 0), (':', 0);
