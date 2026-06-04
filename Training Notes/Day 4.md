- Python does not have compile time, only run time

How to find any text in an entire directory, make sure to remove irrelevant files
- grep -rn "text" . --exclude-dir="venv" --exclude-dir=".git" 
