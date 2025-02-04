package net.ilexiconn.llibrary.client.lang;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.ilexiconn.llibrary.LLibrary;
import net.minecraft.util.StringTranslate;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author gegy1000
 * @since 1.1.0
 */
@SideOnly(Side.CLIENT)
public enum LanguageHandler {
    INSTANCE;

    private Map<String, Map<String, String>> localizations = new HashMap<>();

    public RemoteLanguageContainer loadRemoteLocalization(String modId) throws Exception {
        InputStream in = LanguageHandler.class.getResourceAsStream("/assets/" + modId.toLowerCase() + "/lang.json");
        if (in != null) {
            return new Gson().fromJson(new InputStreamReader(in), RemoteLanguageContainer.class);
        }
        return null;
    }

    public void load() {
        File cacheDir = new File("llibrary/lang");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        for (File child : cacheDir.listFiles()) {
            if (child.isFile()) {
                try {
                    Map<String, String> lang = StringTranslate.parseLangFile(new FileInputStream(child));
                    this.localizations.put(child.getName().split("\\.")[0], lang);
                } catch (Exception e) {
                    LLibrary.LOGGER.error("An exception occurred while loading " + child.getName() + " from cache.", e);
                }
            }
        }
        for (ModContainer mod : Loader.instance().getModList()) {
            String modId = mod.getModId();
            try {
                RemoteLanguageContainer container = loadRemoteLocalization(modId);
                if (container != null) {
                    for (RemoteLanguageContainer.LangContainer language : container.languages) {
                        Map<String, String> lang = StringTranslate.parseLangFile(new URL(language.downloadURL).openStream());
                        String locale = language.locale;
                        if (this.localizations.containsKey(locale)) {
                            lang.putAll(this.localizations.get(locale));
                        }
                        this.localizations.put(locale, lang);
                    }
                }
            } catch (Exception e) {
                LLibrary.LOGGER.error("An exception occurred while loading remote lang container for " + modId, e);
            }
        }
        for (Map.Entry<String, Map<String, String>> entry : this.localizations.entrySet()) {
            String language = entry.getKey();
            File cache = new File(cacheDir, language + ".lang");
            try {
                if (!cache.exists()) {
                    cache.createNewFile();
                }
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(cache), Charsets.UTF_8));
                for (Map.Entry<String, String> langEntry : entry.getValue().entrySet()) {
                    out.append(langEntry.getKey()).append("=").append(langEntry.getValue()).append("\n");
                }
                out.close();
            } catch (Exception e) {
                LLibrary.LOGGER.error("An exception occurred while saving cache for " + language);
            }
        }
    }

    public void addRemoteLocalizations(String language, Map<String, String> properties) {
        Map<String, String> localizationsForLang = this.localizations.get(language);
        if (localizationsForLang != null) {
            properties.putAll(localizationsForLang);
        }
    }
}
