-- https://platform.stratascratch.com/coding/10156-number-of-units-per-nationality?code_type=1
SELECT h.nationality, COUNT(DISTINCT u.unit_id) AS apart_count FROM airbnb_hosts h
    JOIN airbnb_units u ON h.host_id = u.host_id
    WHERE h.age < 30 AND u.unit_type = 'Apartment'
    GROUP BY h.nationality
    ORDER BY apart_count DESC;