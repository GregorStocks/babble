CREATE TABLE IF NOT EXISTS rounds (id INT NOT NULL AUTO_INCREMENT, PRIMARY KEY  (id));
CREATE TABLE IF NOT EXISTS words (id INT NOT NULL AUTO_INCREMENT, word TEXT NOT NULL, PRIMARY KEY (id));
CREATE TABLE IF NOT EXISTS roundwords (id INT NOT NULL AUTO_INCREMENT, roundid INT NOT NULL, wordid INT NOT NULL,  PRIMARY KEY (id));

-- add the default wordlist
INSERT INTO words (word) VALUES
('I'), ('you'), ('he'), ('she'), ('they'), ('it'), ('we'),
('is'), ('are'), ('eat'), ('kill'), ('cuss'),
('butt'), ('human'), ('pair'),
('groggy'), ('ugly'), ('dumb'), ('fat'), ('phat'),
('-s'), ('-y'), ('-ly'), ('-ize'), ('-ify'), ('-ed'),
('pro-'), ('anti-'),
('nine'), ('four'), ('six'), ('eighty'),
('ain''t'),
('myself'), ('yourself'), ('herself'),
('my'), ('your'), ('her'), ('his'),
('other'),
('who'),
('through'), ('on'), ('top'), ('of'), ('with'), ('for'), ('to'), ('from'), ('until'),
('the'), ('even'), ('so'), ('well'),
('still'), ('yet'), ('also'), ('and'), ('too'), ('but'), ('while'),
('a'), ('a'), ('the'), ('any'), ('many'),
('!'), ('!'), ('.'), ('.'), ('?'),
(','), (';'), (':');