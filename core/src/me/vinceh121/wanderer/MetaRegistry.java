package me.vinceh121.wanderer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.type.TypeFactory;

import me.vinceh121.wanderer.building.IslandMeta;
import me.vinceh121.wanderer.building.LighthouseMeta;
import me.vinceh121.wanderer.character.CharacterMeta;
import me.vinceh121.wanderer.entity.PropMeta;
import me.vinceh121.wanderer.guntower.MachineGunGuntowerMeta;

public final class MetaRegistry {
	private static final Logger LOG = LogManager.getLogger(MetaRegistry.class);
	private static final MetaRegistry INSTANCE = new MetaRegistry();
	private final Map<String, IMeta> metaMap = new HashMap<>();

	private MetaRegistry() {
		try {
			this.loadDefaults();
		} catch (final IOException e) {
			MetaRegistry.LOG.error("Error while loading meta defaults", e);
			throw new RuntimeException(e);
		}
	}

	public <T extends IMeta> void readMetas(final FileHandle fh, final Class<T> clazz)
			throws StreamReadException, DatabindException, IOException {
		final Map<String, T> read = WandererConstants.MAPPER.readValue(fh.read(),
				TypeFactory.defaultInstance().constructMapType(Hashtable.class, String.class, clazz));
		this.putAll(read);
	}

	public void loadDefaults() throws StreamReadException, DatabindException, IOException {
		this.readMetas(Gdx.files.internal("lighthouses.json"), LighthouseMeta.class);
		this.readMetas(Gdx.files.internal("islands.json"), IslandMeta.class);
		this.readMetas(Gdx.files.internal("characters.json"), CharacterMeta.class);
		this.readMetas(Gdx.files.internal("props.json"), PropMeta.class);
		this.readMetas(Gdx.files.internal("machinegunGuntowers.json"), MachineGunGuntowerMeta.class);
	}

	public void clear() {
		this.metaMap.clear();
	}

	public boolean containsKey(final String key) {
		return this.metaMap.containsKey(key);
	}

	public boolean containsValue(final IMeta value) {
		return this.metaMap.containsValue(value);
	}

	public Set<Entry<String, IMeta>> entrySet() {
		return this.metaMap.entrySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends IMeta> T get(final String key) {
		return (T) this.metaMap.get(key);
	}

	public String getReverse(final IMeta meta) {
		for (final Entry<String, IMeta> e : this.metaMap.entrySet()) {
			if (e.getValue() == meta) {
				return e.getKey();
			}
		}
		return null;
	}

	public void putAll(final Map<? extends String, ? extends IMeta> m) {
		for (final String key : m.keySet()) {
			if (this.metaMap.containsKey(key)) {
				throw new IllegalStateException("Duplicate key: " + key);
			}
		}
		this.metaMap.putAll(m);
	}

	public void put(final String key, final IMeta value) {
		final IMeta previous = this.metaMap.put(key, value);
		if (previous != null) {
			throw new IllegalStateException("Duplicate key: " + key);
		}
	}

	public int size() {
		return this.metaMap.size();
	}

	public Collection<IMeta> values() {
		return this.metaMap.values();
	}

	public static MetaRegistry getInstance() {
		return MetaRegistry.INSTANCE;
	}
}
