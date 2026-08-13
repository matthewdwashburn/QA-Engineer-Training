import sqlite3


conn = sqlite3.connect('students.db')
c= conn.cursor()

c.execute("""CREATE TABLE IF NOT EXISTS students (name TEXT, age INTEGER)""")

c.execute("""INSERT INTO students VALUES ('Mark',24),('John',20)""")

c.execute("""SELECT * FROM students""")

rows = c.fetchall()

for row in rows:
    print(row)

conn.commit()

conn.close()