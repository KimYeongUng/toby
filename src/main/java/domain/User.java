package domain;

import lombok.Data;

@Data
public class User {
    String id;
    String password;
    String name;
    String email;

    Level level;
    int login;
    int recommend;

    public void upgradeLevel(){
        Level nextLevel = this.level.nextLevel();

        if(nextLevel == null)
            throw new IllegalStateException(this.level+" is not avaliable to upgrade");
        else
            this.level = nextLevel;
    }
}
