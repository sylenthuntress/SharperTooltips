package sylenthuntress.enchanted_tooltips;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnchantedTooltips implements ModInitializer {
    public static final String MOD_ID = "enchanted-tooltips";
    public static final String MOD_NAME = "EnchantedTooltips";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitialize() {
        EnchantedTooltips.LOGGER.info(MOD_NAME + " successfully loaded!");
    }
}