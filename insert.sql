DELETE FROM publicationmeta3 CASCADE;
DELETE FROM users CASCADE;
DELETE FROM adminmessages CASCADE;


INSERT INTO users VALUES
('Lucas', 'true', 'teste'),
('Antonio', 'true', 'teste'),
('Nilu', 'true', 'teste'),
('Rudy', 'true', 'teste')
;

INSERT INTO adminmessages VALUES
('REGISTER joao teste'),
('REGISTER miguel teste'),
('ADD_PUBLICATION Mixed_Method_Research Healthcare January Nilu'),
('UPDATE_PUBLICATION publication MudeiType Novemba Lucas')
;


INSERT INTO publicationmeta3 VALUES
('publication', 'July 2020', 'Data Science', 'Lucas'),
('Clustering Algorithm in Data Science', 'July 2020', 'Data Science', 'Nilu'),
('Java Server Pages (JSP)', 'November 2020', 'Computer Science', 'Nilu'),
('Cyber Security Terminology', 'October 2020', 'Cyber Security', 'Nilu'),
('Principal Component Analysis in Data Science', 'August 2020', 'Data Science', 'Antonio'),
('Clustering in Data Science', 'July 2020', 'Data Science', 'Rudy'),
('A Liturgical Relation with Spatial Config and Architectural Form of the Catholic Church', 'April 2020', 'Architecture', 'Rudy'),
('Emergency Shelter Design For Disaster Preparation', 'July 2020', 'Architecture', 'Rudy')
;
