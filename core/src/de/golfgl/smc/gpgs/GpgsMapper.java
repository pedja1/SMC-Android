package de.golfgl.smc.gpgs;

import de.golfgl.gdxgamesvcs.IGameServiceIdMapper;
import rs.pedjaapps.smc.utility.GameSave;

/**
 * Created by Benjamin Schulte on 23.11.2017.
 */

public class GpgsMapper implements IGameServiceIdMapper<String> {
    public static final String CLIENT_ID = "247781040817-11joguefac2afnufld8mdegfirle5h2k.apps.googleusercontent.com";
    public static final String ACH_LEVEL_1_CLEARED = "CgkIscWah5sHEAIQBA";
    public static final String ACH_LEVEL_2_CLEARED = "CgkIscWah5sHEAIQBQ";
    public static final String ACH_LEVEL_3_CLEARED = "CgkIscWah5sHEAIQBg";
    public static final String ACH_LEVEL_4_CLEARED = "CgkIscWah5sHEAIQBw";
    public static final String ACH_LEVEL_5_CLEARED = "CgkIscWah5sHEAIQCA";
    public static final String ACH_LEVEL_6_CLEARED = "CgkIscWah5sHEAIQCQ";
    public static final String ACH_LEVEL_7_CLEARED = "CgkIscWah5sHEAIQCg";
    public static final String ACH_LEVEL_8_CLEARED = "CgkIscWah5sHEAIQCw";
    public static final String ACH_LEVEL_9_CLEARED = "CgkIscWah5sHEAIQDA";
    public static final String ACH_LEVEL_10_CLEARED = "CgkIscWah5sHEAIQDQ";
    public static final String ACH_LEVEL_11_CLEARED = "CgkIscWah5sHEAIQDg";
    public static final String ACH_WORLD_1_CLEARED = "CgkIscWah5sHEAIQDw";
    public static final String ACH_LEVEL_13_CLEARED = "CgkIscWah5sHEAIQEA";
    public static final String LEAD_TOTAL_SCORE = "CgkIscWah5sHEAIQAw";
    public static final String EVENT_EVENT_LEVEL_STARTED = "CgkIscWah5sHEAIQAQ";
    public static final String EVENT_EVENT_LEVEL_CLEARED = "CgkIscWah5sHEAIQAg";

    @Override
    public String mapToGsId(String independantId) {
        switch (independantId) {
            case GameSave.EVENT_LEVEL_CLEARED:
                return EVENT_EVENT_LEVEL_CLEARED;
            case GameSave.EVENT_LEVEL_STARTED:
                return EVENT_EVENT_LEVEL_STARTED;
            case GameSave.LEADERBOARD_TOTAL:
                return LEAD_TOTAL_SCORE;
            case "lvl_1_CLEAR":
                return ACH_LEVEL_1_CLEARED;
            case "lvl_2_CLEAR":
                return ACH_LEVEL_2_CLEARED;
            case "lvl_3_CLEAR":
                return ACH_LEVEL_3_CLEARED;
            case "lvl_4_CLEAR":
                return ACH_LEVEL_4_CLEARED;
            case "lvl_5_CLEAR":
                return ACH_LEVEL_5_CLEARED;
            case "lvl_6_CLEAR":
                return ACH_LEVEL_6_CLEARED;
            case "lvl_7_CLEAR":
                return ACH_LEVEL_7_CLEARED;
            case "lvl_8_CLEAR":
                return ACH_LEVEL_8_CLEARED;
            case "lvl_9_CLEAR":
                return ACH_LEVEL_9_CLEARED;
            case "pasol_3_CLEAR":
                return ACH_LEVEL_10_CLEARED;
            case "stephane_1_CLEAR":
                return ACH_LEVEL_11_CLEARED;
            case "tower_1_CLEAR":
                return ACH_WORLD_1_CLEARED;
            case "eatomania_CLEAR":
                return ACH_LEVEL_13_CLEARED;
            default:
                return null;
        }
    }
}
