package com.revature.collections;

import java.util.*;

public class CollectionsEx {

    public static void main(String[] args) {
        Person ryan = new Person("Ryan", 60, 100);
        Person richard = new Person("Richard",65,90);
        Person sierra = new Person("Sierra",61,80);

        List<Person> people = new ArrayList<>();
        people.add(ryan);
        people.add(richard);
        people.add(sierra);

        people.add(2,ryan);

        System.out.println(people);
        System.out.println(people.get(2));
        System.out.println(people.size());

        Set<Person> personSet = new HashSet<>();
        personSet.addAll(people);
        System.out.println(personSet);

        //Map
        Map<Integer, Person> personMap = new HashMap<>();
        personMap.put(1,ryan);
        personMap.put(12,richard);
        personMap.put(45,sierra);

        System.out.println(personMap.get(45));

        //Enhanced For Loop - designed to loop over an entire collection of elements
        for(Integer key : personMap.keySet()){
            System.out.println(key + " = " + personMap.get(key));
        }

        //inefficient way to do this:
        Set<Integer> keySet = personMap.keySet();

        List<Integer> keyList = new ArrayList<>(keySet);
        for(int i=0; i<keyList.size();i++){
            int key = keyList.get(i);
            System.out.println(key + " = " + personMap.get(key));

        }


    }
}
