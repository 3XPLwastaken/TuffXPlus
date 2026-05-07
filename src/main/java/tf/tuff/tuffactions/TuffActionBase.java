package tf.tuff.tuffactions;

import java.util.logging.Level;

public abstract class TuffActionBase {

    private boolean enabled = false;
    private boolean debugMode = false;

    private final String name;
    private final String configPath;

    protected final TuffActions plugin;

    public TuffActionBase(TuffActions plugin, String name, String configPath, boolean defaultEnabled) {
        this.plugin = plugin;
        this.name = name;
        this.configPath = configPath;
        plugin.plugin.getConfig().addDefault(configPath+".enabled", defaultEnabled);
        plugin.plugin.getConfig().addDefault(configPath+".debug", false);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void onConfigLoad() {
        boolean wasEnabled = this.enabled;
        this.enabled = plugin.plugin.getConfig().getBoolean(this.configPath+".enabled");
        if (enabled) {
            this.enable(wasEnabled);
        } else if (wasEnabled) {
            this.disable();
        }
        this.debugMode = plugin.plugin.getConfig().getBoolean(this.configPath+".debug");
    }
    protected void enable(boolean wasEnabled) {
        plugin.info(name+" enabled.");
    }
    protected void disable() {
        plugin.info(name+" is now disabled.");
    }

    protected boolean isDebug() {
        return debugMode;
    }
    protected void debug(String msg) {
        if (debugMode) plugin.log(Level.INFO, msg);
    }
    protected void debug(String msg, Exception e) {
        if (debugMode) plugin.log(Level.INFO, msg, e);
    }
}
