package com.revature.polymorphism;

public class Parent {
    String jobTitle;

    public void work(){
        System.out.println("Work hard to make money");
    }

    public Parent someMethod(){
        return new Parent();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }
    
}
