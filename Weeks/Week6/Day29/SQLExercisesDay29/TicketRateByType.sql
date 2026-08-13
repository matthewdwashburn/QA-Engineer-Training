with true_type_count as (select type, count(*) AS cnt FROM facebook_complaints
    WHERE processed = TRUE
    GROUP BY type
)
-- When you know there is only one value for each row, you can aggregate with max so sql won't complain (MAX(t.cnt))
-- You can also CAST t.cnt AS REAL to eliminate int division
SELECT f.type, (CAST(MAX(t.cnt) AS REAL) / count(*)) AS processed_rate FROM facebook_complaints f
JOIN true_type_count t ON f.type = t.type
GROUP BY f.type;