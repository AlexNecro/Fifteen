package hollowness.necro.fifteen;

import android.content.Context;

/**
 * Created by kurdyukov_ae on 07.02.2017.
 */

public enum GameRules {
    NONE(0),
    CLASSIC2(12),
    CLASSIC3(13),
    CLASSIC4(14),
    CLASSIC5(15),
    CLASSIC6(16),
    CLASSIC7(17),
    SWAP2(22),//3x3
    SWAP3(23),//3x3
    SWAP4(24),
    SWAP5(25),
    SWAP6(26),
    SWAP7(27),
    REVOLVE2(32),
    REVOLVE3(33),
    REVOLVE4(34),
    REVOLVE5(35),
    REVOLVE6(36),
    REVOLVE7(37),
    PUZZLE2(42),//3x3
    PUZZLE3(43),//3x3
    PUZZLE4(44),
    PUZZLE5(45),
    PUZZLE6(46),
    PUZZLE7(47);
    private int ruleCode;

    GameRules(int code) {
        ruleCode = code;
    }

    public static GameRules fromString(String rule) {
        return GameRules.valueOf(rule);
    }

    public String getName(Context context) {
        if (isClassic()) return context.getString(R.string.game_15);
        if (isSwap()) return context.getString(R.string.game_swap);
        if (isPuzzle()) return context.getString(R.string.game_puzzle);
        if (isRevolve()) return context.getString(R.string.game_revolve);
        return "?";
    }

    public String getDesc(Context context) {
        if (isClassic()) return context.getString(R.string.gamedesc_15);
        if (isSwap()) return context.getString(R.string.gamedesc_swap);
        if (isPuzzle()) return context.getString(R.string.gamedesc_puzzle);
        if (isRevolve()) return context.getString(R.string.gamedesc_revolve);
        return "";
    }

    public String getFullName(Context context) {
        return getName(context)+" "+getSizeX()+"x"+getSizeY();
    }

    public Boolean isPuzzle() {
        if (this == PUZZLE2 || this == PUZZLE3 || this == PUZZLE4 || this == PUZZLE5 || this == PUZZLE6 || this == PUZZLE7) return true;
        return false;
    }

    public Boolean isSwap() {
        if (this == SWAP2 || this == SWAP3 || this == SWAP4 || this == SWAP5 || this == SWAP6 || this == SWAP7) return true;
        return false;
    }

    public Boolean isRevolve() {
        if (this == REVOLVE2 || this == REVOLVE3 || this == REVOLVE4 || this == REVOLVE5 || this == REVOLVE6 || this == REVOLVE7) return true;
        return false;
    }

    public Boolean isClassic() {
        if (this == CLASSIC3 || this == CLASSIC3 || this == CLASSIC4 || this == CLASSIC5 || this == CLASSIC6 || this == CLASSIC7) return true;
        return false;
    }

    public int getSizeX() {
        switch(this) {
            case SWAP2:
            case REVOLVE2:
            case CLASSIC2:
            case PUZZLE2: return 2;
            case SWAP3:
            case REVOLVE3:
            case CLASSIC3:
            case PUZZLE3: return 3;
            case CLASSIC4:
            case SWAP4:
            case PUZZLE4:
            case REVOLVE4: return 4;
            case CLASSIC5:
            case SWAP5:
            case PUZZLE5:
            case REVOLVE5: return 5;
            case SWAP6:
            case PUZZLE6:
            case CLASSIC6:
            case REVOLVE6: return 6;
            case SWAP7:
            case PUZZLE7:
            case CLASSIC7:
            case REVOLVE7: return 7;
        }
        return 1;
    }

    public int getSizeY() {
        return getSizeX();
    }

    public String getDefaultImagePath() {
        if (isSwap()) return "random.png";
        if (isRevolve()) return "random.png";
        if (isPuzzle()) return "random.png";
        /*if (isSwap()) return "swap.png";
        if (isRevolve()) return "revolve.png";
        if (isPuzzle()) return "puzzle.png";*/
        return "classic.png";
    }
}
