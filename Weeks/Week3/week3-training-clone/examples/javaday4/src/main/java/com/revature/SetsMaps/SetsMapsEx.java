package com.revature.SetsMaps;

import java.util.*;

public class SetsMapsEx {
    public static void main(String[] args) {
        Set<Sku> hash = new HashSet<>();
        hash.add(new Sku("A"));
        hash.add(new Sku("A"));
        hash.add(new Sku("a"));
        System.out.println(hash.size());
        System.out.println(hash);

        //TreeSet automatically sorts elements using compareTo()
        TreeSet<Sku> tree = new TreeSet<>();
        tree.add(new Sku("B"));
        tree.add(new Sku("C"));
        tree.add(new Sku("A"));
        tree.add(new Sku("A"));
        tree.add(new Sku("a"));
        System.out.println(tree.size());
        System.out.println(tree);

        //HASHMAP
        Map<Sku, Integer> stock = new HashMap<>();
        stock.put(new Sku("A"),10);
        stock.put(new Sku("B"),4);
        //merge(key, value, function)
        //if key exists -> apply function (oldValue+newValue)
        //If kye does not exist -> insert value
        stock.merge(new Sku("A"),2,Integer::sum);
        stock.merge(new Sku("a"),4,Integer::sum);
        System.out.println(stock);


    }
}
