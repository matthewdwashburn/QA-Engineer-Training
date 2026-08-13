SELECT 
    CASE 
        WHEN g.grade >= 8 THEN s.name
        ELSE NULL
    END AS name,
    g.grade, 
    s.marks
 FROM students s
    JOIN grades g ON s.marks BETWEEN g.min_mark AND g.max_mark
    ORDER BY -- Separate each case in order by
        g.grade DESC, 
        CASE WHEN g.grade >= 8 THEN s.name END,
        CASE WHEN g.grade < 8 THEN s.marks END