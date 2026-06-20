package com.EqualsHashCodeExercise;

import java.util.Objects;


public final class Money {
    String currency;
    long amountMinor;

    public Money(String currency, long amountMinor) {
        this.currency = currency;
        this.amountMinor = amountMinor;
    }

    public String getCurrency() {
        return currency;
    }

    public long getAmountMinor() {
        return amountMinor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Money m = (Money) o;
        return currency == m.currency && amountMinor == m.amountMinor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(currency, amountMinor);
    }

    @Override
    public String toString() {
        return "Money [currency=" + currency + ", amountMinor=" + amountMinor + "]";
    }
}
