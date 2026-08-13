package com.revature;

public class Life {
    public static void main(String[] args) {
        Jake j1 = new Jake(0);
        Jake j2 = new Jake(0);
        j1.increaseBallKnowledge();
        j1.increaseBallKnowledge();
        System.out.println(j1.getBallKnowledge());
        System.out.println(j2.getBallKnowledge());
        System.out.println(Washburn.getFamilyMemberCount());
    }
}
