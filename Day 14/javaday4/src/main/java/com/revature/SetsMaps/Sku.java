package com.revature.SetsMaps;

public class Sku implements Comparable<Sku>{
    private final String code;

    public Sku(String code) {
        this.code = code;
    }
    
    /*
    equals() defines LOGICAL equality
    USED BY:
    - Hashset (duplicate prevention)
    - HasMap (key matching)
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sku sku)) return false;
        // Case insensitive comparison
        return code != null && code.equalsIgnoreCase(sku.code);

    }

    // Make sure a and A are put into the same key, first wins
    @Override
    public int hashCode() {
        return code == null ? 0 : code.toLowerCase().hashCode();
    }

    // Will ignore case when comparing
    @Override
    public int compareTo(Sku o) {
        return code.compareToIgnoreCase(code);
    }

    @Override
    public String toString() {
        return code;
    }
}
