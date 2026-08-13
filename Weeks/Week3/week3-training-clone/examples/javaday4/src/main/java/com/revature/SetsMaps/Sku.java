package com.revature.SetsMaps;

import java.util.Objects;

public class Sku implements Comparable<Sku>{
    private final String code;

    public Sku(String code) {
        this.code = code;
    }

    /* equals() defines LOGICAL equality.
    * USED BY:
    * - HashSet (duplicate prevention)
    * - HashMap (key matching)
    * Two SKUs are equal if their "code" matches exactly*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sku sku)) return false;

        //CASE-INSENSITIVE comparison
        return code != null && code.equalsIgnoreCase(sku.code);
    };


    @Override
    public int hashCode() {
        return code == null ? 0 : code.toLowerCase().hashCode();
    }

    //will ignore case when comparing
    public int compareTo(Sku o) {
        return code.compareToIgnoreCase(o.code);
    }

    @Override
    public String toString() {
        return code;
    }
}
