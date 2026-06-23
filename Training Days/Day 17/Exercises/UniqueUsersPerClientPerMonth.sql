-- https://platform.stratascratch.com/coding/2024-unique-users-per-client-per-month?code_type=1

SELECT client_id, Count(DISTINCT user_id), EXTRACT(MONTH FROM time_id) AS month FROM fact_events
    GROUP BY month, client_id;