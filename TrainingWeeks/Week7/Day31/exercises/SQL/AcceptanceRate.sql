SELECT date, ((1.0 * COUNT(CASE WHEN action = 'accepted' THEN 1 ELSE NULL END)) / (1.0 * COUNT(CASE WHEN action = 'sent' THEN 1 ELSE NULL END))) AS acceptance_rate
    from fb_friend_requests
    GROUP BY date
    HAVING acceptance_rate > 0;