-- ----------------------------------------------------------------------------
-- Model
-------------------------------------------------------------------------------

DROP TABLE Answer;
DROP TABLE Event;

----- Event -----
CREATE TABLE Event ( eventId BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) COLLATE latin1_bin NOT NULL,
    description VARCHAR(1024) COLLATE latin1_bin NOT NULL,
    celebrationDate DATETIME NOT NULL,
    duration SMALLINT NOT NULL,
    creationDate DATETIME NOT NULL,
    numberAttend SMALLINT NOT NULL,
    numberNotAttend SMALLINT NOT NULL,
    isCancelled BIT NOT NULL,
    CONSTRAINT EventPK PRIMARY KEY(eventId),
    CONSTRAINT validDuration CHECK ( duration >= 0 AND duration <= 1000 ),
    CONSTRAINT validAttend CHECK ( numberAttend >= 0 ),
    CONSTRAINT validNotAttend CHECK ( numberNotAttend >= 0 ) ) ENGINE = InnoDB;

----- Answer -----
CREATE TABLE Answer ( answerId BIGINT NOT NULL AUTO_INCREMENT,
    eventId BIGINT NOT NULL,
    employeeEmail VARCHAR(255) NOT NULL,
    answerDate DATETIME NOT NULL,
    attendance BIT NOT NULL,
    CONSTRAINT answerPK PRIMARY KEY(answerId),
    CONSTRAINT answerEventIdFK FOREIGN KEY(eventId)
        REFERENCES Event(eventId) ON DELETE CASCADE) ENGINE = InnoDB;
