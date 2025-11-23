package data.scripts.campaign.intel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.utils.SettingsHelper;
import exerelin.campaign.AllianceManager;
import exerelin.campaign.PlayerFactionStore;
import exerelin.campaign.alliances.Alliance;

public class ForceOfPersonalityIntel extends BaseIntelPlugin {
    
    @Override
    public boolean isImportant() {
        return true;
    }
    
    @Override
    public String getIcon() {
        return "graphics/icons/intel/reputation.png";
    }
    
    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add("Personal");
        return tags;
    }
    
    @Override
    public IntelSortTier getSortTier() {
        return IntelSortTier.TIER_3;
    }
    
    @Override
    public String getName() {
        return "Force of Personality";
    }
    
    @Override
    public String getSmallDescriptionTitle() {
        return getName();
    }
    
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return Global.getSector().getPlayerFleet();
    }
    
    @Override
    public FactionAPI getFactionForUIColors() {
        return Global.getSector().getPlayerFaction();
    }
    
    @Override
    public String getCommMessageSound() {
        return getSoundMajorPosting();
    }
    
    @Override
    protected void addBulletPoints(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;
        
        float initPad = pad;
        if (mode == IntelInfoPlugin.ListInfoMode.IN_DESC) initPad = opad;
        
        bullet(info);
        
        SectorAPI sector = Global.getSector();
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        Alliance playerAlliance = null;
        
        if (playerFactionId != null) {
            playerAlliance = AllianceManager.getFactionAlliance(playerFactionId);
        }
        
        if (playerAlliance != null) {
            List<String> members = new ArrayList<String>(playerAlliance.getMembersCopy());
            members.remove(playerFactionId);
            
            if (!members.isEmpty()) {
                info.addPara("Alliance members gradually improve relations with each other", initPad, 
                           g, h, "Alliance members", "improve relations");
                info.addPara("Alliance is permanent and accepts any faction", initPad, 
                           g, h, "permanent", "any faction");
            }
        } else {
            info.addPara("Not currently in an alliance", initPad, g);
        }
        
        if (playerFactionId != null) {
            FactionAPI playerFaction = sector.getFaction(playerFactionId);
            if (playerFaction != null) {
                int preAllianceCount = 0;
                for (FactionAPI faction : sector.getAllFactions()) {
                    if (faction.isNeutralFaction()) continue;
                    if (faction.getId().equals(playerFactionId)) continue;
                    if (playerAlliance != null && playerAlliance.getMembersCopy().contains(faction.getId())) continue;
                    
                    float relation = playerFaction.getRelationship(faction.getId());
                    if (relation >= 0.5f) {
                        preAllianceCount++;
                    }
                }
                
                if (preAllianceCount > 0) {
                    info.addPara(preAllianceCount + " faction(s) with strong relations are improving relations with each other", 
                               initPad, g, h, preAllianceCount + "");
                }
            }
        }
        
        unindent(info);
    }
    
    @Override
    public void createIntelInfo(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.setParaSmallInsignia();
        info.addPara(getName(), c, 0f);
        info.setParaFontDefault();
        addBulletPoints(info, mode);
    }
    
    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;
        
        info.addPara("Your magnetic personality draws factions together, making them believe in a shared vision " +
                    "of the future. Those who align themselves with you find themselves putting aside their " +
                    "historical differences, discovering common ground they never knew existed.", opad);
        
        SectorAPI sector = Global.getSector();
        String playerFactionId = PlayerFactionStore.getPlayerFactionIdNGC();
        Alliance playerAlliance = null;
        
        if (playerFactionId != null) {
            playerAlliance = AllianceManager.getFactionAlliance(playerFactionId);
        }
        
        if (playerAlliance != null) {
            List<String> members = new ArrayList<String>(playerAlliance.getMembersCopy());
            members.remove(playerFactionId);
            
            if (!members.isEmpty()) {
                info.addSpacer(opad);
                info.addPara("Current Alliance Members:", opad, h, "Alliance Members");
                
                for (String memberId : members) {
                    FactionAPI faction = sector.getFaction(memberId);
                    if (faction != null) {
                        info.addPara("• " + faction.getDisplayName(), pad, tc, faction.getBaseUIColor(), 
                                   faction.getDisplayName());
                    }
                }
                
                info.addSpacer(opad);
                    int checkIntervalDays = SettingsHelper.getCheckIntervalDaysDisplay();
                    String daysText = checkIntervalDays == 1 ? "day" : "days";
                    String format = "Alliance members improve relations with each other by %s every %s %s. Your alliance is %s, and your " +
                               "magnetic personality allows %s to join regardless of their normal " +
                               "alignment compatibility.";
                    info.addPara(format, opad, tc, h, String.valueOf(SettingsHelper.getRelationImprovementAllianceDisplay()), String.valueOf(checkIntervalDays), daysText, "permanent", "any faction");
            }
        } else {
            info.addSpacer(opad);
            info.addPara("You are not currently in an alliance. When you form or join one, it will become " +
                        "permanent, and your magnetic personality will allow any faction to join regardless " +
                        "of alignment compatibility.", opad, tc, h, "permanent", "any faction");
        }
        
        if (playerFactionId != null) {
            FactionAPI playerFaction = sector.getFaction(playerFactionId);
            if (playerFaction != null) {
                List<String> preAllianceFactions = new ArrayList<String>();
                for (FactionAPI faction : sector.getAllFactions()) {
                    if (faction.isNeutralFaction()) continue;
                    if (faction.getId().equals(playerFactionId)) continue;
                    if (playerAlliance != null && playerAlliance.getMembersCopy().contains(faction.getId())) continue;
                    
                    float relation = playerFaction.getRelationship(faction.getId());
                    if (relation >= 0.5f) {
                        preAllianceFactions.add(faction.getId());
                    }
                }
                
                if (!preAllianceFactions.isEmpty()) {
                    info.addSpacer(opad);
                    info.addPara("Factions with strong relations to you (50+) are also being drawn together, " +
                               "improving relations with each other at a slower pace:", opad, tc, h, "strong relations", "50+");
                    
                    for (String factionId : preAllianceFactions) {
                        FactionAPI faction = sector.getFaction(factionId);
                        if (faction != null) {
                            float relation = playerFaction.getRelationship(factionId);
                            info.addPara("• " + faction.getDisplayName() + " (" + 
                                       (int)(relation * 100) + " relations)", pad, tc, 
                                       faction.getBaseUIColor(), faction.getDisplayName(), 
                                       (int)(relation * 100) + "");
                        }
                    }
                    
                    int checkIntervalDays = SettingsHelper.getCheckIntervalDaysDisplay();
                    String daysText = checkIntervalDays == 1 ? "day" : "days";
                    String format = "These factions improve relations with each other by %s every %s %s.";
                    info.addPara(format, pad, tc, h, String.valueOf(SettingsHelper.getRelationImprovementPreAllianceDisplay()), String.valueOf(checkIntervalDays), daysText);
                }
            }
        }
        
        addBulletPoints(info, IntelInfoPlugin.ListInfoMode.IN_DESC);
    }
}
