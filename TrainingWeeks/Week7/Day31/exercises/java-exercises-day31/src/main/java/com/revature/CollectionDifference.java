package com.revature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.HashSet;

public class CollectionDifference {
    public static List<Character> diff(Collection<Character> a, Collection<Character> b) {
    HashSet<Character> diffSet = new HashSet<>();

    // Loop through a
    for(char c : a) {
        if (!b.contains(c)) {
            diffSet.add(c);
        }
    }
    // Loop through a
    for (char c : b) {
        if (!a.contains(c)) {
            diffSet.add(c);
        }
    }
    List<Character> diffList = new ArrayList<>(diffSet);
    diffList.sort(Comparator.naturalOrder());
    
    return diffList;
  }
}
