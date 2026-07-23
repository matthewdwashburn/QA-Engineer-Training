package com.revature;

public class Jake extends Washburn implements CurryMentality {
    int ballKnowledge;
    public Jake(int ballKnowledge) {
        this.ballKnowledge = ballKnowledge;
    }

    public int getBallKnowledge() {
        return this.ballKnowledge;
    }

    public void increaseBallKnowledge() {
        this.ballKnowledge++;
    }
    
    @Override
    public void sleep() {
        System.out.println("Grinding teeth...");
    }

    @Override
    public void shootingStrategy() {
        System.out.println("Chuck threes");
    }
}
