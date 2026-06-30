SET SEARCH_PATH TO "public 2";

DROP TABLE IF EXISTS accounts;

CREATE TABLE accounts (
	account_id INT PRIMARY KEY,
	customer_name VARCHAR(50),
	balance DECIMAL(10,2)
);

INSERT INTO accounts VALUES
(1,'Alice',1000),
(2,'Bob',500),
(3,'Charlie',700);

SELECT * FROM accounts;

-- Dirty Read: A transaction reads data that another transaction has modified but not committed
-- postgresql has lowest isolation level as READ COMMITTED (default) and will not allow dirty reads
-- but if has READ UNCOMMITTED isolation level this could be possible

-- Non-repeatable READ: You read the same row twice in one transaction and get different values 
-- because another tranation committed changes
SET TRANSACTION ISOLATION LEVEL REPEATABLE READ; -- set to repeatable read to not allow non-repeatable read
BEGIN;
SELECT balance FROM accounts
WHERE account_id=1;

COMMIT;

-- phantom read you execute the same query twice, the second time, extra rows appear or disappear

BEGIN;
SELECT * FROM accounts
WHERE balance > 600;
COMMIT;

-- to prevent phantom read set transaction isolation level to SERIALIZABLE, performance cost, can 
-- cause tranactions to fail and require the query to be rerun, most isolated
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;
BEGIN;
SELECT * FROM accounts
WHERE balance > 600;

-- Isolation Levels (Least to Most Isolated): Read-uncommited, Read-commited, repeatable read, and serializable