package com.revature.collections;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

public class CollectionsEx {
    public static void main(String[] args) {
        Person ryan = new Person("Ryan", 60, 100);
        Person jake = new Person("Jake", 60, 90);
        Person paul = new Person("Paul", 60, 80);

        List<Person> people = new ArrayList<>();
        people.add(ryan);
        people.add(jake);
        people.add(paul);
        System.out.println(people);

        people.add(2, ryan);
        System.out.println(people.get(2));
        System.out.println(people);
        System.out.println(people.size());

        Set<Person> personSet = new HashSet<>();
        personSet.addAll(people);
        System.out.println(personSet);

        //Map 
        Map<Integer, Person> personMap = new HashMap<>();
        personMap.put(1, ryan);
        personMap.put(12, paul);
        personMap.put(42, paul);

        System.out.println(personMap.get(42));

        //Enhanced for loop - loop over entire collection of elements
        for(Integer key : personMap.keySet()) {
            System.out.println(key + " = " + personMap.get(key));
        }

        //Innefficent way to do this
        Set<Integer> keySet = personMap.keySet();
        List<Integer> keyList = new ArrayList<>(keySet);
        for(int i = 0; i<keyList.size(); i++) {
            int key = keyList.get(i);
            System.out.println(key + " = " + personMap.get(key));
        }
    }

}
