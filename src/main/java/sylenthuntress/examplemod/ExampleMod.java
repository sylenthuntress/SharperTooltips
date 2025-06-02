package sylenthuntress.examplemod;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleMod implements ModInitializer {
    public static final String MOD_ID = "examplemod";
    public static final String MOD_NAME = "ExampleMod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static Identifier modIdentifier(String id) {
        return Identifier.of(MOD_ID, id);
    }

    public void onInitialize() {
        ExampleMod.LOGGER.info(MOD_NAME + " successfully loaded!");
    }
}