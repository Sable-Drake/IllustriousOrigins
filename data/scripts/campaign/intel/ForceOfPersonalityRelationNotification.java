package data.scripts.campaign.intel;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.utils.SettingsHelper;

public class ForceOfPersonalityRelationNotification extends BaseIntelPlugin {

    private List<RelationChange> allChanges = new ArrayList<RelationChange>();
    private List<RelationChange> allianceChanges = new ArrayList<RelationChange>();
    private List<RelationChange> preAllianceChanges = new ArrayList<RelationChange>();

    public static class RelationChange {
        public String factionAId;
        public String factionBId;
        public float oldRelation;
        public float newRelation;
        public float delta;

        public RelationChange(String factionAId, String factionBId, float oldRelation, float newRelation) {
            this.factionAId = factionAId;
            this.factionBId = factionBId;
            this.oldRelation = oldRelation;
            this.newRelation = newRelation;
            this.delta = newRelation - oldRelation;
        }
    }

    public ForceOfPersonalityRelationNotification(List<RelationChange> allChanges, 
                                                  List<RelationChange> allianceChanges, 
                                                  List<RelationChange> preAllianceChanges) {
        this.allChanges = allChanges;
        this.allianceChanges = allianceChanges;
        this.preAllianceChanges = preAllianceChanges;
    }
    
    public ForceOfPersonalityRelationNotification(List<RelationChange> changes, boolean isAllianceMembers) {
        this.allChanges = changes;
        if (isAllianceMembers) {
            this.allianceChanges = changes;
        } else {
            this.preAllianceChanges = changes;
        }
    }

    @Override
    public boolean isImportant() {
        return false;
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
        boolean hasAllianceChanges = !allianceChanges.isEmpty();
        boolean hasPreAllianceChanges = !preAllianceChanges.isEmpty();

        if (hasAllianceChanges && hasPreAllianceChanges) {
            return "Alliance and Faction Relations Improved";
        } else if (hasAllianceChanges) {
            return "Alliance Relations Improved";
        } else if (hasPreAllianceChanges) {
            return "Faction Relations Improved";
        }
        return "Relations Improved";
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
        return getSoundStandardPosting();
    }

    @Override
    protected void addBulletPoints(TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == IntelInfoPlugin.ListInfoMode.IN_DESC) initPad = opad;

        bullet(info);

        SectorAPI sector = Global.getSector();
        for (RelationChange change : allChanges) {
            FactionAPI factionA = sector.getFaction(change.factionAId);
            FactionAPI factionB = sector.getFaction(change.factionBId);
            if (factionA == null || factionB == null) continue;

            RepLevel repLevel = RepLevel.getLevelFor(change.newRelation);
            String relationStr = repLevel.getDisplayName();
            
            int deltaValue = (int)(change.delta * 100f);
            String deltaStr = change.delta > 0 ? "+" + deltaValue : String.valueOf(deltaValue);
            
            String format = "%s and %s: %s (%s)";
            String[] highlights = new String[]{
                factionA.getDisplayName(),
                factionB.getDisplayName(),
                relationStr,
                deltaStr
            };
            
            info.addPara(format, initPad, g, h, highlights);
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

        int checkIntervalDays = SettingsHelper.getCheckIntervalDaysDisplay();
        String daysText = checkIntervalDays == 1 ? "day" : "days";
        
        if (!allianceChanges.isEmpty() && !preAllianceChanges.isEmpty()) {
            String format = "Your magnetic personality continues to draw factions together. " +
                           "Relations between alliance members and factions with strong relations to you " +
                           "have improved in the past %s %s.";
            info.addPara(format, opad, tc, h, String.valueOf(checkIntervalDays), daysText);
        } else if (!allianceChanges.isEmpty()) {
            String format = "Your magnetic personality continues to draw alliance members together. " +
                           "Relations between alliance members have improved in the past %s %s.";
            info.addPara(format, opad, tc, h, String.valueOf(checkIntervalDays), daysText);
        } else if (!preAllianceChanges.isEmpty()) {
            String format = "Factions with strong relations to you are being drawn together by your vision. " +
                           "Their relations with each other have improved in the past %s %s.";
            info.addPara(format, opad, tc, h, String.valueOf(checkIntervalDays), daysText);
        }

        SectorAPI sector = Global.getSector();
        
        if (!allianceChanges.isEmpty()) {
            info.addSpacer(opad);
            info.addPara("Alliance Members:", pad, h, "Alliance Members");
            for (RelationChange change : allianceChanges) {
                displayRelationChange(info, sector, change, pad, tc, h);
            }
        }
        
        if (!preAllianceChanges.isEmpty()) {
            info.addSpacer(opad);
            info.addPara("Factions with Strong Relations:", pad, h, "Factions with Strong Relations");
            for (RelationChange change : preAllianceChanges) {
                displayRelationChange(info, sector, change, pad, tc, h);
            }
        }
        
        addBulletPoints(info, IntelInfoPlugin.ListInfoMode.IN_DESC);
    }
    
    private void displayRelationChange(TooltipMakerAPI info, SectorAPI sector, RelationChange change, 
                                       float pad, Color tc, Color h) {
        FactionAPI factionA = sector.getFaction(change.factionAId);
        FactionAPI factionB = sector.getFaction(change.factionBId);
        if (factionA == null || factionB == null) return;

        RepLevel newRepLevel = RepLevel.getLevelFor(change.newRelation);
        RepLevel oldRepLevel = RepLevel.getLevelFor(change.oldRelation);
        String relationStr = newRepLevel.getDisplayName();
        String oldRelationStr = oldRepLevel.getDisplayName();
        boolean repLevelChanged = !newRepLevel.equals(oldRepLevel);

        int deltaValue = (int)(change.delta * 100f);
        String deltaStr = change.delta > 0 ? "+" + deltaValue : String.valueOf(deltaValue);

        String format;
        Color[] colors;
        String[] highlights;
        
        if (repLevelChanged) {
            format = "• %s and %s: %s to %s (%s)";
            colors = new Color[]{
                factionA.getBaseUIColor(),
                factionB.getBaseUIColor(),
                h,
                h,
                h
            };
            highlights = new String[]{
                factionA.getDisplayName(),
                factionB.getDisplayName(),
                oldRelationStr,
                relationStr,
                deltaStr
            };
        } else {
            format = "• %s and %s: %s (%s)";
            colors = new Color[]{
                factionA.getBaseUIColor(),
                factionB.getBaseUIColor(),
                h,
                h
            };
            highlights = new String[]{
                factionA.getDisplayName(),
                factionB.getDisplayName(),
                relationStr,
                deltaStr
            };
        }
        
        info.addPara(format, pad, colors, highlights);
    }
}
