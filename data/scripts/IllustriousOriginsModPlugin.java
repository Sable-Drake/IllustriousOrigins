package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;

public class IllustriousOriginsModPlugin extends BaseModPlugin {
    
    @Override
    public void onApplicationLoad() {
        if (!Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            Global.getLogger(this.getClass()).error("Illustrious Origins requires Nexerelin to be enabled!");
            return;
        }
    }
}
