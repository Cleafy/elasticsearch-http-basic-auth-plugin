package com.cleafy.elasticsearch6.plugins.http;

import com.cleafy.elasticsearch6.plugins.http.utils.Globals;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Setting;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.util.concurrent.ThreadContext;
import org.elasticsearch.plugins.ActionPlugin;
import org.elasticsearch.plugins.Plugin;
import org.elasticsearch.rest.RestHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;


/**
 * @author Andrea Sessa (andrea.sessa@cleafy.com)
 */
public class HttpBasicServerPlugin extends Plugin implements ActionPlugin {

    private boolean enabledByDefault = false;
    private final Settings settings;
    BasicRestFilter basicFilter;

    @Inject
    public HttpBasicServerPlugin(Settings settings) {
        this.settings = settings;
        this.basicFilter = new BasicRestFilter(this.settings);
    }

    public String name() {
        return "http-basic-server-plugin";
    }

    public String description() {
        return "HTTP Basic Server Plugin";
    }

    @Override
    public UnaryOperator<RestHandler> getRestHandlerWrapper(final ThreadContext threadContext) {
        if (this.settings.getAsBoolean(Globals.SETTINGS_ENABLED, enabledByDefault)) {
            return (rh) -> basicFilter.wrap(rh);
        }
        return (rh) -> rh;
    }

    @Override
    public Settings additionalSettings() {
        if (this.settings.getAsBoolean(Globals.SETTINGS_ENABLED, enabledByDefault)) {
            final Settings.Builder builder = Settings.builder();
            builder.put(super.additionalSettings());
            return builder.build();
        } else {
            return Settings.EMPTY;
        }
    }

    @Override
    public List<Setting<?>> getSettings() {
        List<Setting<?>> settings = new ArrayList<Setting<?>>();

        settings.addAll(super.getSettings());

        settings.add(Setting.boolSetting(Globals.SETTINGS_ENABLED, enabledByDefault, Setting.Property.NodeScope, Setting.Property.Filtered));
        settings.add(Setting.simpleString(Globals.SETTINGS_USERNAME, Setting.Property.NodeScope, Setting.Property.Filtered));
        settings.add(Setting.simpleString(Globals.SETTINGS_PASSWORD, Setting.Property.NodeScope, Setting.Property.Filtered));
        settings.add(Setting.boolSetting(Globals.SETTINGS_LOG, false, Setting.Property.NodeScope, Setting.Property.Filtered));

        return settings;
    }
}