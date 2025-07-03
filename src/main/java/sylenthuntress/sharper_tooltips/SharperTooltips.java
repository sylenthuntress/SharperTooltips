package sylenthuntress.sharper_tooltips;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharperTooltips implements ModInitializer {
    public static final String MOD_ID = "sharper-tooltips";
    public static final String MOD_NAME = "SharperTooltips";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitialize() {
        SharperTooltips.LOGGER.info(MOD_NAME + " successfully loaded!");
    }
}