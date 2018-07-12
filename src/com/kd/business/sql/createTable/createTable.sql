create table ${TABLENAME} (code varchar(20),organ_code INT,value FLOAT,datatype varchar(10),
updatetime timestamp,statistime timestamp,dimname varchar(10),dimvalue varchar(50),pos INT IDENTITY(1, 1),FLAG INT DEFAULT 0,
PRIMARY KEY(POS))
