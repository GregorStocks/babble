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
-- probably some of these arent really in the right part of speech because i
-- dont understand english and also didnt initially sort em all right. deal w/it
-- nouns
('butt', 0), ('human', 0), ('pair', 0), ('body', 0), ('time', 0), ('person', 0),
('year', 0), ('way', 0), ('day', 0), ('thing', 0), ('man', 0), ('world', 0),
('life', 0), ('hand', 0), ('part', 0), ('child', 0), ('eye', 0), ('woman', 0),
('place', 0), ('work', 0), ('case', 0), ('point', 0),
('government', 0), ('company', 0), ('number', 0), ('group', 0), ('problem', 0),
('fact', 0), ('inch', 0), ('street', 0), ('surface', 0), ('ocean', 0),
('class', 0), ('note', 0), ('scientist', 0), ('wheel', 0), ('island', 0),
('week', 0), ('machine', 0), ('base', 0), ('plane', 0), ('system', 0),
('round', 0), ('boat', 0), ('game', 0), ('force', 0), ('language', 0),
('shape', 0), ('equation', 0), ('government', 0), ('heat', 0), ('check', 0),
('object', 0), ('rule', 0), ('noun', 0), ('power', 0), ('size', 0), ('ball', 0),
('material', 0), ('fine', 0), ('circle', 0), ('matter', 0), ('square', 0),
('syllable', 0), ('bill', 0), ('test', 0), ('direction', 0), ('center', 0),
('farmer', 0), ('general', 0), ('energy', 0), ('moon', 0), ('region', 0),
('dance', 0), ('member', 0), ('cell', 0), ('paint', 0), ('mind', 0), ('love', 0),
('cause', 0), ('rain', 0), ('exercise', 0), ('egg', 0), ('train', 0), ('wish', 0),
('drop', 0), ('window', 0), ('difference', 0), ('distance', 0), ('heart', 0),
('sum', 0), ('summer', 0), ('wall', 0), ('forest', 0), ('leg', 0), ('winter', 0),
('arm', 0), ('brother', 0), ('race', 0), ('store', 0), ('job', 0), ('edge', 0),
('sign', 0), ('record', 0), ('sky', 0), ('glass', 0), ('weather', 0), ('root', 0),
('instrument', 0), ('third', 0), ('month', 0), ('paragraph', 0), ('clothes', 0),
('flower', 0), ('teacher', 0), ('cross', 0), ('metal', 0), ('son', 0), ('ice', 0),
('sleep', 0), ('village', 0), ('factor', 0), ('result', 0), ('snow', 0), ('ride', 0),
('floor', 0), ('hill', 0), ('baby', 0), ('century', 0), ('everything', 0),
('tail', 0), ('phrase', 0), ('soil', 0), ('bed', 0), ('copy', 0), ('hope', 0),
('spring', 0), ('case', 0), ('nation', 0), ('type', 0), ('temperature', 0),
('lead', 0), ('everyone', 0), ('method', 0), ('section', 0), ('lake', 0), ('consonant', 0),
('dictionary', 0), ('hair', 0), ('age', 0), ('scale', 0), ('pound', 0), ('moment', 0),
('gold', 0), ('milk', 0), ('stone', 0), ('speed', 0), ('cat', 0), ('sail', 0),
('bear', 0), ('angle', 0), ('fraction', 0), ('melody', 0), ('bottom', 0), ('trip', 0),
('hole', 0), ('fight', 0), ('surprise', 0), ('dress', 0), ('iron', 0), ('finger', 0),
('beer', 0), ('rum', 0), ('liquor', 0), ('tequila', 0), ('wine', 0), ('bud', 0),
('cognac', 0), ('pot', 0), ('weed', 0), ('drug', 0), ('cocaine', 0), ('heroin', 0),
('meth', 0), ('money', 0), ('cash', 0), ('cow', 0), ('genie', 0), ('mongoose', 0),
('octopus', 0), ('ox', 0), ('herpes', 0), ('aid', 0), ('diabetes', 0), ('measles', 0),
('asbestos', 0), ('rhino', 0), ('albino', 0), ('latex', 0), ('generalissimo', 0),
('photograph', 0), ('bacteria', 0), ('virus', 0), ('gym', 0), ('succubus', 0),
('lieutenant', 0), ('captain', 0), ('corporal', 0), ('sergeant', 0), ('general', 0),
('major', 0), ('Africa', 0), ('Europe', 0), ('America', 0), ('Australia', 0),
('Asia', 0), ('Antarctica', 0), ('Albania', 0), ('Angola', 0), ('Austria', 0),
('Belgium', 0), ('Canada', 0), ('China', 0), ('Denmark', 0), ('Ethiopia', 0),
('Finland', 0), ('France', 0), ('Georgia', 0), ('Germany', 0), ('Greece', 0), ('Hungary', 0),
('India', 0), ('Iraq', 0), ('Israel', 0), ('Italy', 0), ('Japan', 0),
('Korea', 0), ('Laos', 0), ('Liechtenstein', 0), ('Mali', 0), ('Mexico', 0),
('Montenegro', 0), ('New Zealand', 0), ('Pakistan', 0), ('Paraguay', 0), ('Poland', 0),
('Russia', 0), ('Samoa', 0), ('Spain', 0), ('Syria', 0), ('Tunisia', 0), ('Turkey', 0),
('USA', 0), ('Vatican', 0), ('Zimbabwe', 0), ('California', 0), ('Utah', 0),
('Texas', 0), ('Nebraska', 0), ('Illinois', 0), ('Ohio', 0), ('Virginia', 0),
('Florida', 0), ('Pennsylvania', 0), ('Massachusetts', 0), ('Maine', 0), ('pi', 0),
('felony', 0), ('crime, 0),

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
('zero', 0), ('one', 0), ('two', 0), ('three', 0), ('four', 0), ('five', 0),
('six', 0), ('seven', 0), ('eight', 0), ('nine', 0), ('ten', 0), ('eleven', 0),
('twelve', 0), ('thirteen', 0), ('fourteen', 0), ('fifteen', 0), ('sixteen', 0),
('seventeen', 0), ('eighteen', 0), ('nineteen', 0), ('twenty', 0),
('thirty', 0), ('forty', 0), ('fifty', 0), ('sixty', 0), ('seventy', 0),
('eighty', 0), ('ninety', 0), ('hundred', 0), ('thousand', 0), ('million', 0),
('billion', 0), ('trillion', 0), ('another', 0), ('certain', 0), ('less', 0),
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
('-cide', 0), ('-er', 0), ('-fy', 0), ('-ism', 0), ('-ize', 0),

-- special dealies
('==', 1), ('^', 1), ('++', 0);

-- add a room
INSERT INTO rooms (name) VALUES ("Room 1");
