## DEMOS
- Went through postman collections, environments
- Jmeter stress test demo, create thread group, set thread count, rand up period, loop count
- Create sampler http request, listener view results tree, listener summary report
- run jmeter in terminal to start the GUI
- Dont use GUI for load testing, use CLI for that, use GUI only for test creation and test debugging
- Jmeter is a load testing software

## EXERCISES

### SQL
- LIMIT 1 OFFSET 2, third in the list
- You can group by something that's not in the SELECT statement
- Remember you can AND in Having
- CASE WHEN THEN  ELSE END
- YEAR(YYYY-MM-DD) (Month and Day as well)
- SUBSTRING(string_expression, start_position, length)
- COUNT (CASE WHEN balance... THEN 1 ELSE NULL END) (Must be null not 0 or count will count it)
- WITH union_table AS (SELECT UNION SELECT)
### JAVA
- multiple ways to declare array, notable being String[] arr = {'bob', 'bill'}, int[] arr = new int[5], int[] arr; (Init later)
- import java.util.Arrays, print an array with Arrays.toString(arr)
- watch for casting numbers as ints instead of floats
- java collections methods extensive review needed
	- list.sort(Comparator.naturalOrder())
	- sets can be passed when creating a list no problem
	- Arrays.asList(arr);
- Arrays.sort(b) or Arrays.sort(b, Collections.reverseOrder())
- ```
Map<String, Integer> freq = new HashMap<>();
for (String w : text.toLowerCase().split("\\s+")) {
    freq.put(w, freq.getOrDefault(w, 0) + 1);
}
return freq;
```
- indexOf(value)
### PYTHON
- alpha string then list comprehension to get alpha list
- arrays have .remove and .add
- Find index of element in python arr.index(value)
- Python passes primitives like types like int str by value, and non primitive collections like list, dict, set by reference
- join a list
- split a list
- for key, value in enumerate(my_dict):
- .index(value)
- When you get stuck, don't panic print, first read the requirements, and only when you are sure youthen go back and walk your data through print statements
- list.discard(discard) if you don't know if the value exists or not
- python zip function
- string[start:end:step]
- float('-inf')
- import pandas as pd
	- df1 = pd.DataFrame({'user_id: [1,2,3], 'name': ['Alice', 'Bob', 'Joe']})
	- df1 = pd.DataFrame({'user_id: [1,2,3], 'age': ['35', '45', '55']})
	- merged = pd.merge(df1, df2, on='user_id', how='inner')
	- merged.fillna(0, inplace=True)
- __ enter  __ (self) and __ exit__ (self, exc_type, exc_value, traceback):