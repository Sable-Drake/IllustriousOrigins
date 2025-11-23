package data.scripts.campaign.listeners;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.BaseCampaignEventListener;
import com.fs.starfarer.api.campaign.SectorAPI;
import data.scripts.campaign.backgrounds.ForceOfPersonalityBackground;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.alliances.Alliance;
import exerelin.campaign.backgrounds.CharacterBackgroundUtils;

public class ForceOfPersonalityAllianceListener extends BaseCampaignEventListener {
    
    public ForceOfPersonalityAllianceListener() {
        super(false);
    }
    
    @Override
    public void reportEconomyTick(int iterIndex) {
        SectorAPI sector = Global.getSector();
        if (sector == null) return;
        
        if (!CharacterBackgroundUtils.isBackgroundActive(ForceOfPersonalityBackground.BACKGROUND_ID)) {
            return;
        }
        
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        if (playerFactionId == null) return;
        
        Alliance playerAlliance = AllianceManager.getFactionAlliance(playerFactionId);
        if (playerAlliance != null) {
            data.scripts.campaign.backgrounds.ForceOfPersonalityAlignmentHandler.setVisionaryAlliance(playerAlliance);
        }
    }
}
