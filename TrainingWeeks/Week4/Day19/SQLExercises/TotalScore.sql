WITH max_score_per_hacker_challenge AS (
SELECT s.hacker_id, s.challenge_id, MAX(s.score) as max_score, h.name FROM submissions s
    JOIN hackers h ON h.hacker_id = s.hacker_id
    GROUP BY s.hacker_id, s.challenge_id, h.name)

SELECT hacker_id, name, SUM(max_score) as total_score
    FROM max_score_per_hacker_challenge
    GROUP BY hacker_id, name
    HAVING total_score > 0 -- Having lets you filter off post-grouped data
    ORDER BY total_score DESC, hacker_id;