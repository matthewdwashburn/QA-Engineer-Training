l = [1,2,2,4] #List
s = {1,2,3,4} #Set
t = (1,2,3,4,5,5,5,6) #tuple
d = {"a":1, "b":2,"c":3}
#list.append(x) x is the value to be added to the end of the list
appended_list = ["I","declare","a","thumb"]
print(appended_list)
appended_list.append("war")
print(appended_list)

#list.extend(iterable) iterable is an iterable object, like another list
final_numbers = [5,6,7,8]
appended_list.extend(final_numbers)
print(appended_list)

#list.insert(i,x) index position that the object will be interted in front of
#So index 0 means front of the list, x is the value to be added
appended_list.insert(1,"one, two, three, four")
print(appended_list)

#list.remove(x) removes the first element to match x. Raises a ValueError if the item does not exist
appended_list.remove("thumb")
print(appended_list)

#list.pop([i]) i represents the OPTIONAL index position of the item you wish to return and remove from the list
#if no index is given then the last element in the list will both be returned and removed
appended_list.pop()
print(appended_list)

appended_list.clear()
print(appended_list)

#list.index(x[,start[,end]]) returns index of value x, where start and end are the optional start/end points
new_list=[1,2,3,4,5,3,6,"seven"]
print(new_list.index(4,1,5))
print("counter below")
print(new_list.count(3))

to_sort_numbers = [1,3,2,4,6,5,7,9,8,10]
print(to_sort_numbers)
to_sort_numbers.sort()
print(to_sort_numbers)
sort_using_key = [[1,"c"],[2,"a"],[3,"b"]]
def my_key(element):
    return element[1] # takes the second element in the nested list and uses it to sort the list
sort_using_key.sort(key=my_key)
# sort_using_key.sort(key= lambda x: x[1])
print(sort_using_key)

to_sort_numbers.reverse()
print(to_sort_numbers)
#or use below, thank you Landon
print(to_sort_numbers[::-1])

#list.copy() returns a "shallow" copy of the list

copied_list= sort_using_key.copy()

print(copied_list)

# Sets 

well = "well"
lets = "let's"
see = "See"
how = "how"
this = "this"
works = "works"

my_set = {1,1,1,1,1,1,1,1} 
print(my_set)
my_set.add(well)
my_set.add(well)
my_set.add(well)
my_set.add(lets)
my_set.add(see)
my_set.add(4)
my_set.add(how)
my_set.add(this)
my_set.add(works)
print(my_set)

#set.pop() removes an element and returns it from the set. Can't be sure which it is
print(my_set.pop())
print(my_set)

#set.discard(x) and set.remove(x) both try to remove the specified item, discard will not raise an error if the element does not 
#does not exist

my_set.remove("works")
print(my_set)

this_is_fixed = ("these","are","stuck","are")

print(this_is_fixed.count("are"))

print(this_is_fixed.index("these"))

def inside_the_dictionary():
    return "nice"

my_dictionary = {
    "key": "value",
    100: 1000,
    "string key": 5,
    10: "string value",
    None: "this still works",
    "can also do this": None,
    "function": inside_the_dictionary(),
    inside_the_dictionary(): "does this work"
}

print(my_dictionary[inside_the_dictionary()])

my_dictionary["new key"] = 23 #this adds a new key value pair to the dictionary, same as dic.update({key:value})
#my_dictionary.update({"new key":23})
print(my_dictionary)

print(my_dictionary.items()) # returns a list with the key value pairs stored as tuples
print(type(my_dictionary.items()))

print(list(my_dictionary.items()))
print(type(list(my_dictionary.items())))

#aside remove duplicates from a list
l2= [1,3,3,5,5,2,1,0]
l3=list(set(l2))
print(l3)

#dic.setdefault(key,[value]) retusn the value of the provided key, if it does not exist it creates it with the provided value

my_dictionary.setdefault("key3","new value?")
print(my_dictionary)

#dic.values() returns all values dic.keys() returns all keys





