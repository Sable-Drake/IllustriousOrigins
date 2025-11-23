package data.scripts.campaign.backgrounds;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.scripts.campaign.intel.ForceOfPersonalityRelationNotification;
import data.scripts.campaign.intel.ForceOfPersonalityRelationNotification.RelationChange;
import data.scripts.utils.SettingsHelper;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.alliances.Alliance;
import exerelin.campaign.backgrounds.CharacterBackgroundUtils;

public class ForceOfPersonalityScript implements EveryFrameScript {
    
    private static final float MIN_RELATION_FOR_PRE_ALLIANCE = 0.5f;
    
    private CampaignClockAPI clock;
    private long lastCheckTimestamp;
    
    public ForceOfPersonalityScript() {
        this.clock = Global.getSector().getClock();
        lastCheckTimestamp = clock.getTimestamp();
    }
    
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
        SectorAPI sector = Global.getSector();
        if (sector == null) return;
        
        if (!CharacterBackgroundUtils.isBackgroundActive(ForceOfPersonalityBackground.BACKGROUND_ID)) {
            return;
        }
        
        float checkInterval = SettingsHelper.getCheckIntervalDays();
        float daysSinceLastCheck = clock.getElapsedDaysSince(lastCheckTimestamp);
        if (daysSinceLastCheck >= checkInterval) {
            lastCheckTimestamp = clock.getTimestamp();
            processPeriodicEffects(sector);
        }
    }
    
    private void processPeriodicEffects(SectorAPI sector) {
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        if (playerFactionId == null) return;
        
        FactionAPI playerFaction = sector.getFaction(playerFactionId);
        if (playerFaction == null) return;
        
        Alliance playerAlliance = AllianceManager.getFactionAlliance(playerFactionId);
        
        List<RelationChange> allChanges = new ArrayList<RelationChange>();
        List<RelationChange> allianceChanges = new ArrayList<RelationChange>();
        List<RelationChange> preAllianceChanges = new ArrayList<RelationChange>();
        
        if (playerAlliance != null) {
            allianceChanges = processAllianceMembers(sector, playerAlliance, playerFactionId);
            allChanges.addAll(allianceChanges);
        }
        
        preAllianceChanges = processPreAllianceFactions(sector, playerFactionId, playerAlliance);
        allChanges.addAll(preAllianceChanges);
        
        if (!allChanges.isEmpty()) {
            ForceOfPersonalityRelationNotification notification = 
                new ForceOfPersonalityRelationNotification(allChanges, allianceChanges, preAllianceChanges);
            Global.getSector().getIntelManager().addIntel(notification, false);
        }
    }
    
    private List<RelationChange> processAllianceMembers(SectorAPI sector, Alliance alliance, String playerFactionId) {
        List<String> members = new ArrayList<String>(alliance.getMembersCopy());
        Set<String> permaMembers = alliance.getPermaMembersCopy();
        
        for (String memberId : members) {
            if (!permaMembers.contains(memberId)) {
                alliance.addPermaMember(memberId);
            }
        }
        
        ForceOfPersonalityAlignmentHandler.setVisionaryAlliance(alliance);
        
        List<RelationChange> changes = new ArrayList<RelationChange>();
        
        for (int i = 0; i < members.size(); i++) {
            String factionIdA = (String) members.get(i);
            FactionAPI factionA = sector.getFaction(factionIdA);
            if (factionA == null || factionA.isNeutralFaction() || !hasMarkets(factionA) || shouldSkipFaction(factionA)) continue;
            
            for (int j = i + 1; j < members.size(); j++) {
                String factionIdB = (String) members.get(j);
                FactionAPI factionB = sector.getFaction(factionIdB);
                if (factionB == null || factionB.isNeutralFaction() || !hasMarkets(factionB) || shouldSkipFaction(factionB)) continue;
                
                RelationChange change = improveRelations(factionA, factionIdB, SettingsHelper.getRelationImprovementAlliance());
                if (change != null) {
                    changes.add(change);
                }
            }
        }
        
        return changes;
    }
    
    private List<RelationChange> processPreAllianceFactions(SectorAPI sector, String playerFactionId, Alliance playerAlliance) {
        FactionAPI playerFaction = sector.getFaction(playerFactionId);
        if (playerFaction == null) return new ArrayList<RelationChange>();
        
        List<String> preAllianceFactions = new ArrayList<String>();
        
        for (FactionAPI faction : sector.getAllFactions()) {
            if (faction.isNeutralFaction()) continue;
            if (faction.getId().equals(playerFactionId)) continue;
            if (playerAlliance != null && playerAlliance.getMembersCopy().contains(faction.getId())) continue;
            if (!hasMarkets(faction)) continue;
            if (shouldSkipFaction(faction)) continue;
            
            float relation = playerFaction.getRelationship(faction.getId());
            if (relation >= MIN_RELATION_FOR_PRE_ALLIANCE) {
                preAllianceFactions.add(faction.getId());
            }
        }
        
        List<RelationChange> changes = new ArrayList<RelationChange>();
        
        for (String factionId : preAllianceFactions) {
            FactionAPI faction = sector.getFaction(factionId);
            if (faction == null || !hasMarkets(faction) || shouldSkipFaction(faction)) continue;
            
            RelationChange change = improveRelations(playerFaction, factionId, SettingsHelper.getRelationImprovementPreAlliance());
            if (change != null) {
                changes.add(change);
            }
        }
        
        for (int i = 0; i < preAllianceFactions.size(); i++) {
            String factionIdA = (String) preAllianceFactions.get(i);
            FactionAPI factionA = sector.getFaction(factionIdA);
            if (factionA == null || !hasMarkets(factionA) || shouldSkipFaction(factionA)) continue;
            
            for (int j = i + 1; j < preAllianceFactions.size(); j++) {
                String factionIdB = (String) preAllianceFactions.get(j);
                FactionAPI factionB = sector.getFaction(factionIdB);
                if (factionB == null || !hasMarkets(factionB) || shouldSkipFaction(factionB)) continue;
                
                RelationChange change = improveRelations(factionA, factionIdB, SettingsHelper.getRelationImprovementPreAlliance());
                if (change != null) {
                    changes.add(change);
                }
            }
        }
        
        return changes;
    }
    
    private RelationChange improveRelations(FactionAPI faction, String targetFactionId, float amount) {
        float currentRel = faction.getRelationship(targetFactionId);
        float newRel = Math.min(1.0f, currentRel + amount);
        faction.setRelationship(targetFactionId, newRel);
        
        if (newRel > currentRel) {
            return new RelationChange(faction.getId(), targetFactionId, currentRel, newRel);
        }
        return null;
    }
    
    private boolean hasMarkets(FactionAPI faction) {
        if (faction == null) return false;
        
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        if (playerFactionId != null && faction.getId().equals(playerFactionId)) {
            return true;
        }
        
        for (MarketAPI market : Global.getSector().getEconomy().getMarketsCopy()) {
            if (market.getFactionId().equals(faction.getId()) && !market.isHidden()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean shouldSkipFaction(FactionAPI faction) {
        if (faction == null) return true;
        
        String factionId = faction.getId();
        
        if ("ix_ninth".equals(factionId)) {
            return true;
        }
        
        if ("ix_trinity".equals(factionId) || "ix_trinity_asm".equals(factionId)) {
            return true;
        }
        
        return false;
    }
}
