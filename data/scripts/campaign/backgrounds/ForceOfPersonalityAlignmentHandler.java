package data.scripts.campaign.backgrounds;

import com.fs.starfarer.api.Global;
import exerelin.campaign.alliances.Alliance;
import exerelin.campaign.backgrounds.CharacterBackgroundUtils;

public class ForceOfPersonalityAlignmentHandler {
    
    private static String getMemoryKey(Alliance alliance) {
        if (alliance == null) return null;
        return "$force_of_personality_visionary_" + alliance.getName().replaceAll("\\s+", "_");
    }
    
    public static void setVisionaryAlliance(Alliance alliance) {
        if (alliance == null) return;
        
        try {
            if (!CharacterBackgroundUtils.isBackgroundActive("force_of_personality")) {
                return;
            }
        } catch (Exception e) {
            return;
        }
        
        String key = getMemoryKey(alliance);
        if (key != null) {
            Global.getSector().getMemoryWithoutUpdate().set(key, true);
        }
    }
}
