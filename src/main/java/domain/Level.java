package domain;

public enum Level {
    GOLD(3,null),SILVER(2,GOLD),BASIC(1,SILVER);

    private final int value;
    private final Level next;

    Level(int value,Level next){
        this.value = value;
        this.next = next;
    }

    public Level nextLevel(){
        return this.next;
    }

    public int intValue(){
        return this.value;
    }

    public static Level getValueOf(int value){
        return switch (value) {
            case 1 -> BASIC;
            case 2 -> SILVER;
            case 3 -> GOLD;
            default -> throw new AssertionError("unknown value: " + value);
        };
    }
}
