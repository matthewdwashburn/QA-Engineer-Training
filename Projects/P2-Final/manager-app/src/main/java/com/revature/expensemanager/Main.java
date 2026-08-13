package com.revature.expensemanager;

import com.revature.expensemanager.config.DatabaseSeeder;

public class Main {

    private static final int PORT = 7001;

    public static void main(String[] args) {
        // No-op unless DATABASE_ENV=testing selects the load-test database.
        DatabaseSeeder.seedIfTesting();

        AppFactory.build().start(PORT);
    }
}
