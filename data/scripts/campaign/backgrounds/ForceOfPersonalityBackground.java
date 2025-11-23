package data.scripts.campaign.backgrounds;

import java.awt.Color;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.campaign.intel.ForceOfPersonalityIntel;
import exerelin.campaign.backgrounds.BaseCharacterBackground;
import exerelin.utilities.NexFactionConfig;

public class ForceOfPersonalityBackground extends BaseCharacterBackground {
    
    public static final String BACKGROUND_ID = "force_of_personality";

    @Override
    public float getOrder() {
        return 100f;
    }

    @Override
    public boolean shouldShowInSelection(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        return true;
    }

    @Override
    public String getTitle(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        return "Force of Personality";
    }

    @Override
    public String getShortDescription(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        return "Your magnetic personality draws factions together, making them believe in a shared vision of the future.";
    }

    @Override
    public String getLongDescription(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        return "Those who align themselves with you find themselves drawn together by a shared vision. " +
               "Factions that trust you gradually put aside their historical differences, finding common ground " +
               "they never knew existed. Alliances formed under your leadership become unbreakable bonds, " +
               "and even those not yet fully committed begin to see the wisdom in cooperation.";
    }

    @Override
    public void onNewGameAfterTimePass(FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        if (Global.getSector() == null) return;
        
        Global.getSector().addScript(new ForceOfPersonalityScript());
        
        if (!Global.getSector().hasScript(data.scripts.campaign.listeners.ForceOfPersonalityAllianceInterceptor.class)) {
            Global.getSector().addScript(new data.scripts.campaign.listeners.ForceOfPersonalityAllianceInterceptor());
        }
        
        if (Global.getSector().getListenerManager() != null && 
            !Global.getSector().getListenerManager().hasListenerOfClass(data.scripts.campaign.listeners.ForceOfPersonalityAllianceListener.class)) {
            Global.getSector().addListener(new data.scripts.campaign.listeners.ForceOfPersonalityAllianceListener());
        }
        
        if (Global.getSector().getIntelManager() != null) {
            ForceOfPersonalityIntel intel = new ForceOfPersonalityIntel();
            Global.getSector().getIntelManager().addIntel(intel, false);
        }
    }

    @Override
    public void addTooltipForSelection(TooltipMakerAPI tooltip, FactionSpecAPI factionSpec, NexFactionConfig factionConfig, Boolean expanded) {
        super.addTooltipForSelection(tooltip, factionSpec, factionConfig, expanded);
        
        if (expanded) {
            float pad = 10f;
            Color tc = Misc.getTextColor();
            Color h = Misc.getHighlightColor();
            
            tooltip.addSpacer(10f);
            tooltip.addPara("Factions in alliance with you gradually improve relations with each other, " +
                           "putting aside their differences as they embrace your vision.", pad, tc, h, 
                           "alliance", "improve relations");
            
            tooltip.addPara("Alliances you form become permanent, bound together by unwavering commitment " +
                           "to the shared future you envision.", pad, tc, h, "permanent");
            
            tooltip.addPara("Even factions with strong relations to you (50+) begin to trust each other more, " +
                           "though at a slower pace than those fully committed to your alliance.", pad, tc, h, 
                           "strong relations", "trust each other");
            
            tooltip.addPara("Your magnetic personality allows any faction to join your alliances " +
                           "regardless of their normal alignment compatibility.", pad, tc, h, 
                           "any faction", "alignment compatibility");
        }
    }

    @Override
    public void addTooltipForIntel(TooltipMakerAPI tooltip, FactionSpecAPI factionSpec, NexFactionConfig factionConfig) {
        super.addTooltipForIntel(tooltip, factionSpec, factionConfig);
        
        float pad = 10f;
        Color tc = Misc.getTextColor();
        Color h = Misc.getHighlightColor();
        
        tooltip.addSpacer(10f);
        tooltip.addPara("Your magnetic personality continues to draw factions together. See the Force of Personality " +
                       "intel entry for detailed information on current effects.", pad, tc, h, "Force of Personality");
    }
}
