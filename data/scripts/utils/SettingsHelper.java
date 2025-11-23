package data.scripts.utils;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public class SettingsHelper {
    
    private static final String MOD_ID = "illustriousorigins";
    
    public static float getCheckIntervalDays() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            try {
                Integer value = LunaSettings.getInt(MOD_ID, "checkIntervalDays");
                if (value != null) {
                    return value.floatValue();
                }
            } catch (Exception e) {
            }
        }
        return 30f;
    }
    
    public static float getRelationImprovementAlliance() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            try {
                Integer value = LunaSettings.getInt(MOD_ID, "relationImprovementAlliance");
                if (value != null) {
                    return value.intValue() / 100f;
                }
            } catch (Exception e) {
            }
        }
        return 0.02f;
    }
    
    public static float getRelationImprovementPreAlliance() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            try {
                Integer value = LunaSettings.getInt(MOD_ID, "relationImprovementPreAlliance");
                if (value != null) {
                    return value.intValue() / 100f;
                }
            } catch (Exception e) {
            }
        }
        return 0.01f;
    }
    
    public static int getRelationImprovementAllianceDisplay() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            try {
                Integer value = LunaSettings.getInt(MOD_ID, "relationImprovementAlliance");
                if (value != null) {
                    return value.intValue();
                }
            } catch (Exception e) {
            }
        }
        return 2;
    }
    
    public static int getRelationImprovementPreAllianceDisplay() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            try {
                Integer value = LunaSettings.getInt(MOD_ID, "relationImprovementPreAlliance");
                if (value != null) {
                    return value.intValue();
                }
            } catch (Exception e) {
            }
        }
        return 1;
    }
    
    public static int getCheckIntervalDaysDisplay() {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")) {
            try {
                Integer value = LunaSettings.getInt(MOD_ID, "checkIntervalDays");
                if (value != null) {
                    return value.intValue();
                }
            } catch (Exception e) {
            }
        }
        return 30;
    }
}
