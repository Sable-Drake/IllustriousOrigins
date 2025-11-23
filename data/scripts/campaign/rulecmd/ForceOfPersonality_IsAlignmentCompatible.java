package data.scripts.campaign.rulecmd;

import java.util.List;
import java.util.Map;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.backgrounds.ForceOfPersonalityBackground;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.alliances.Alliance;
import exerelin.campaign.backgrounds.CharacterBackgroundUtils;

public class ForceOfPersonality_IsAlignmentCompatible extends BaseCommandPlugin {
    
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (!CharacterBackgroundUtils.isBackgroundActive(ForceOfPersonalityBackground.BACKGROUND_ID)) {
            return false;
        }
        
        if (params.size() < 2) return false;
        
        String factionId = params.get(0).getString(memoryMap);
        String allianceId = params.get(1).getString(memoryMap);
        
        if (factionId == null || allianceId == null) return false;
        
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        if (playerFactionId == null) return false;
        
        Alliance alliance = AllianceManager.getAllianceByName(allianceId);
        if (alliance == null) {
            alliance = AllianceManager.getFactionAlliance(playerFactionId);
        }
        
        if (alliance != null && alliance.getMembersCopy().contains(playerFactionId)) {
            return true;
        }
        
        return false;
    }
}
