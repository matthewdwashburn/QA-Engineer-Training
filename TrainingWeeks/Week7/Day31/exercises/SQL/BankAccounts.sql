/*	
You are given 2 tables from a database, and it's your job to find the users that currently 
have both a checking and savings account with a balance of more than $1000 in at least one of the two accounts.
*/
SELECT u.first_name, u.last_name 
    FROM users u
    JOIN accounts a ON a.user_id = u.id
    WHERE a.account_type IN ('Checking', 'Savings')
    GROUP BY u.id, u.first_name, u.last_name -- You can group by things not in the select statement but you cannot select things not in the group by statement
    HAVING COUNT(DISTINCT a.account_type) = 2 -- Make sure there is at least one checking and one savings account
    AND MAX(a.balance) > 1000; -- Make sure that at least one account has a balance over 1000