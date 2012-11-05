DROP TABLE Profile CASCADE CONSTRAINTS;
DROP TABLE Friends CASCADE CONSTRAINTS;
DROP TABLE PendingFriends CASCADE CONSTRAINTS;
DROP TABLE Groups CASCADE CONSTRAINTS;
DROP TABLE GroupMembership CASCADE CONSTRAINTS;
DROP TABLE Messages CASCADE CONSTRAINTS;
DROP TABLE MessageRecipients CASCADE CONSTRAINTS;

CREATE TABLE Profile (
	userID number(10) NOT NULL,
	name varchar2(32) ,
	email varchar2(32),
	password varchar2(32),
	date_of_birth date,
	picture_URL varchar2(64),
	aboutme varchar2(32),
	lastlogin date,
	CONSTRAINT Profile_PK PRIMARY KEY (userID)
);

CREATE TABLE Friends(
	userID1 number(10),
	userID2 number(10),
	JDate date,
	message varchar(1028),
	CONSTRAINT Friends_FK1 FOREIGN KEY (userID1) REFERENCES Profile (userID),
	CONSTRAINT Friends_FK2 FOREIGN KEY (userID2) REFERENCES Profile (userID)
);

CREATE TABLE PendingFriends(
	fromID number(10),
	toID number(10),
	message varchar(1028),
	CONSTRAINT PendingFriends_FK1 FOREIGN KEY (fromID) REFERENCES Profile (userID),
	CONSTRAINT PendingFriends_FK2 FOREIGN KEY (toID) REFERENCES Profile (userID)
);

CREATE TABLE Groups(
	gID number(10),
	name varchar(32),
	description varchar(1028),
	CONSTRAINT Groups_PK PRIMARY KEY (gID)
);

CREATE TABLE GroupMembership(
	gID number(10),
	userID number(10),
	CONSTRAINT GroupMembership_FK1 FOREIGN KEY (gID) REFERENCES Groups (gID),
	CONSTRAINT GroupMembership_FK2 FOREIGN KEY (userID) REFERENCES Profile (userID)
);

CREATE TABLE Messages(
	msgID number(10) NOT NULL,
	fromID number (10),
	message varchar2(1028),
	ToUserID number (10) DEFAULT NULL,
	ToGroupID number (10) DEFAULT NULL,
	dateSent date,
	CONSTRAINT Messages_PK PRIMARY KEY (msgID),
	CONSTRAINT Messages_FK1 FOREIGN KEY (fromID) REFERENCES Profile (userID),
	CONSTRAINT Messages_FK2 FOREIGN KEY (ToUserID) REFERENCES Profile (userID),
	CONSTRAINT Messages_FK3 FOREIGN KEY (ToGroupID) REFERENCES Groups (gID)
);

CREATE TABLE MessageRecipients(
	userID number(10),
	msgID number(10),
	CONSTRAINT MessageRecipients_FK1 FOREIGN KEY (msgID) REFERENCES Messages (msgID),
	CONSTRAINT MessageRecipients_FK2 FOREIGN KEY (userID) REFERENCES Profile (userID)
);

INSERT INTO Profile VALUES (1, 'Shenoda', 'shg@pitt.edu', 'shpwd', '13-OCT-1977', '/afs/pitt.edu/home/s/g/shg18/public/photo.jpg', 'CS 1555 TA', '11-NOV-2012');
INSERT INTO Profile VALUES (2, 'Lory', 'lra@pitt.edu', 'lpwd', '08-MAR-1986', NULL, 'Member of ADMT Lab', '10-NOV-2012');
INSERT INTO Profile VALUES (3, 'Peter', 'pdj@pitt.edu', 'ppwd', '09-JAN-1984', 'http://www.cs.pitt.edu/~peter', 'Graduate Student in CS dept.', '10-NOV-2012');
INSERT INTO Profile VALUES (4,'Alexandrie', 'alx@pitt.edu', 'apwd', '21-AUG-1975', NULL, 'Architecture researcher', '11-NOV-2012');
INSERT INTO Profile VALUES (5, 'Panickos', 'pnk@pitt.edu', 'kpwd', '08-SEP-1989', NULL, 'ADMT Lab researcher', '08-NOV-2012');
INSERT INTO Profile VALUES (6, 'Socratis', 'soc@pitt.edu', 'spwd', '17-MAY-1981', NULL, 'TA in CS dept', '09-NOV-2012');
INSERT INTO Profile VALUES (7, 'Yaw', 'yaw@pitt.edu', 'ypwd', '27-FEB-1987', NULL, 'Staff at CS dept', '07-NOV-2012');

INSERT INTO Friends VALUES (1,2, '06-JAN-2008', 'Hey, it is me  Shenoda!');
INSERT INTO Friends VALUES (1,5, '15-JAN-2011', 'Hey, it is me  Shenoda!');
INSERT INTO Friends VALUES (2,3,'23-AUG-2007', 'Hey, it is me  Lory!');
INSERT INTO Friends VALUES (2,4,'17-FEB-2008', 'Hey, it is me  Lory!');
INSERT INTO Friends VALUES (3,4,'16-SEP-2010', 'Hey, it is me  Peter!');
INSERT INTO Friends VALUES (4,6,'06-OCT-2010', 'Hey, it is me  Alexandrie!');
INSERT INTO Friends VALUES (6,7,'13-SEP-2012', 'Hey, it is me  Socratis!');

INSERT INTO PendingFriends VALUES (7,4,'Hey, it is me Yaw');
INSERT INTO PendingFriends VALUES (5,2,'Hey, it is me Panickos');
INSERT INTO PendingFriends VALUES (2,6,'Hey, it is me Lory');

INSERT INTO Groups VALUES (1, 'Grads at CS', 'list of all graduate students');
INSERT INTO Groups VALUES (2, 'DB Group', 'member of the ADMT Lab.');

INSERT INTO GroupMembership VALUES (1,1);
INSERT INTO GroupMembership VALUES (1,2);
INSERT INTO GroupMembership VALUES (1,3);
INSERT INTO GroupMembership VALUES (1,4);
INSERT INTO GroupMembership VALUES (1,5);
INSERT INTO GroupMembership VALUES (1,6);
INSERT INTO GroupMembership VALUES (1,7);
INSERT INTO GroupMembership VALUES (2,1);
INSERT INTO GroupMembership VALUES (2,2);
INSERT INTO GroupMembership VALUES (2,5);

INSERT INTO Messages VALUES (1, 1, 'are we meeting tomorrow for the project?', 2, NULL, '09-NOV-2012');
INSERT INTO Messages VALUES (2, 1, 'Peters pub tomorrow?', 5, NULL, '07-NOV-2012');
INSERT INTO Messages VALUES (3, 2, 'Please join our DB Group forum tomorrow', NULL, 1, '06-NOV-2012');
INSERT INTO Messages VALUES (4, 5, 'Here is the paper I will present tomorrow', NULL, 2, '04-NOV-2012');

INSERT INTO MessageRecipients VALUES (2,1);
INSERT INTO MessageRecipients VALUES (5,2);
INSERT INTO MessageRecipients VALUES (1,3);
INSERT INTO MessageRecipients VALUES (2,3);
INSERT INTO MessageRecipients VALUES (3,3);
INSERT INTO MessageRecipients VALUES (4,3);
INSERT INTO MessageRecipients VALUES (5,3);
INSERT INTO MessageRecipients VALUES (6,3);
INSERT INTO MessageRecipients VALUES (7,3);
INSERT INTO MessageRecipients VALUES (1,4);
INSERT INTO MessageRecipients VALUES (2,4);
INSERT INTO MessageRecipients VALUES (5,4);