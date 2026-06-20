package com.revature;

import java.util.function.ToDoubleFunction;

public class Triangle {
    public final int height;
    public final int base;
    private double area;

    public Triangle(int height, int base) {
        this.height = height;
        this.base = base;
    }

    public void setArea(double a) {
        area = a;
    }

    public double getArea() {
        return area;
    }

    public static ToDoubleFunction<Triangle> f = f -> {f.setArea((f.height * f.base) / 2.0);
                                                     return f.getArea();
                                                     };
}
