package bluestonecarrot.itemframeprotector;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * The main class of the ItemFrameProtector plugin.
 * This plugin protects boats from destroying item frames.
 * @author bluestonecarrot
 */
public class Main extends JavaPlugin {

    /**
     * Calls when the function is enabled.
     * The only thing that needs to be done when enabling is registering events
     */
    @Override
    public void onEnable() {
        // register the entity destroy listener
        getServer().getPluginManager().registerEvents(new DestroyListener(), this);
    }

    @Override
    public void onDisable() {}
}
