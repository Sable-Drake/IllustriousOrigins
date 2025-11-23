package data.scripts.campaign.listeners;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorAPI;
import data.scripts.campaign.backgrounds.ForceOfPersonalityAlignmentHandler;
import data.scripts.campaign.backgrounds.ForceOfPersonalityBackground;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.alliances.Alliance;
import exerelin.campaign.backgrounds.CharacterBackgroundUtils;

public class ForceOfPersonalityAllianceInterceptor implements EveryFrameScript {
    
    private float checkInterval = 1f;
    private float timeSinceLastCheck = 0f;
    
    @Override
    public boolean isDone() {
        return false;
    }
    
    @Override
    public boolean runWhilePaused() {
        return false;
    }
    
    @Override
    public void advance(float amount) {
        if (!CharacterBackgroundUtils.isBackgroundActive(ForceOfPersonalityBackground.BACKGROUND_ID)) {
            return;
        }
        
        timeSinceLastCheck += amount;
        if (timeSinceLastCheck < checkInterval) {
            return;
        }
        timeSinceLastCheck = 0f;
        
        SectorAPI sector = Global.getSector();
        if (sector == null) return;
        
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        if (playerFactionId == null) return;
        
        Alliance playerAlliance = AllianceManager.getFactionAlliance(playerFactionId);
        if (playerAlliance != null) {
            ForceOfPersonalityAlignmentHandler.setVisionaryAlliance(playerAlliance);
        }
    }
}
