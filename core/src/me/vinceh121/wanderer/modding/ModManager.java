package me.vinceh121.wanderer.modding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import me.vinceh121.wanderer.ScriptManager;
import me.vinceh121.wanderer.WandererConstants;

public class ModManager {
	private static final Logger LOG = LogManager.getLogger(ModManager.class);
	private final List<Mod> mods = new ArrayList<>();

	public void loadMods() throws IOException {
		for (final FileHandle modFolder : Gdx.files.internal("mods").list()) {
			final ModManifest manifest =
					WandererConstants.MAPPER.readValue(modFolder.child("manifest.json").read(), ModManifest.class);

			final Mod mod = new Mod();
			mod.setManifest(manifest);
			mod.setFolder(modFolder);
			this.mods.add(mod);
		}

		ModManager.LOG.info("Loaded {} mods", this.mods.size());
	}

	public void executeModsEntryPoints(final ScriptManager scriptManager) {
		for (final Mod mod : this.mods) {
			final FileHandle entryPoint = mod.getFolder().child("index.js");

			if (!entryPoint.exists()) {
				continue;
			}

			scriptManager.loadModule(entryPoint, mod.getFolder());
		}
	}
}
