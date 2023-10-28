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

import me.vinceh121.wanderer.building.IslandPrototype;
import me.vinceh121.wanderer.building.LighthousePrototype;
import me.vinceh121.wanderer.character.CharacterPrototype;
import me.vinceh121.wanderer.entity.PropPrototype;
import me.vinceh121.wanderer.entity.guntower.MachineGunGuntowerPrototype;
import me.vinceh121.wanderer.entity.plane.MachineGunPlanePrototype;

public final class PrototypeRegistry {
	private static final Logger LOG = LogManager.getLogger(PrototypeRegistry.class);
	private static final PrototypeRegistry INSTANCE = new PrototypeRegistry();
	private final Map<String, IPrototype> prototypeMap = new HashMap<>();

	private PrototypeRegistry() {
		try {
			this.loadDefaults();
		} catch (final IOException e) {
			PrototypeRegistry.LOG.error("Error while loading prototype defaults", e);
			throw new RuntimeException(e);
		}
	}

	public <T extends IPrototype> void readPrototypes(final FileHandle fh, final Class<T> clazz)
			throws StreamReadException, DatabindException, IOException {
		final Map<String, T> read = WandererConstants.MAPPER.readValue(fh.read(),
				TypeFactory.defaultInstance().constructMapType(Hashtable.class, String.class, clazz));
		this.putAll(read);
	}

	public void loadDefaults() throws StreamReadException, DatabindException, IOException {
		this.readPrototypes(Gdx.files.internal("prototypes/lighthouses.json"), LighthousePrototype.class);
		this.readPrototypes(Gdx.files.internal("prototypes/islands.json"), IslandPrototype.class);
		this.readPrototypes(Gdx.files.internal("prototypes/characters.json"), CharacterPrototype.class);
		this.readPrototypes(Gdx.files.internal("prototypes/props.json"), PropPrototype.class);
		this.readPrototypes(Gdx.files.internal("prototypes/machinegunGuntowers.json"), MachineGunGuntowerPrototype.class);
		this.readPrototypes(Gdx.files.internal("prototypes/machinegunPlanes.json"), MachineGunPlanePrototype.class);
	}

	public void clear() {
		this.prototypeMap.clear();
	}

	public boolean containsKey(final String key) {
		return this.prototypeMap.containsKey(key);
	}

	public boolean containsValue(final IPrototype value) {
		return this.prototypeMap.containsValue(value);
	}

	public Set<Entry<String, IPrototype>> entrySet() {
		return this.prototypeMap.entrySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends IPrototype> T get(final String key) {
		return (T) this.prototypeMap.get(key);
	}

	public String getReverse(final IPrototype prototype) {
		for (final Entry<String, IPrototype> e : this.prototypeMap.entrySet()) {
			if (e.getValue() == prototype) {
				return e.getKey();
			}
		}
		return null;
	}

	public void putAll(final Map<? extends String, ? extends IPrototype> m) {
		for (final String key : m.keySet()) {
			if (this.prototypeMap.containsKey(key)) {
				throw new IllegalStateException("Duplicate key: " + key);
			}
		}
		this.prototypeMap.putAll(m);
	}

	public void put(final String key, final IPrototype value) {
		final IPrototype previous = this.prototypeMap.put(key, value);
		if (previous != null) {
			throw new IllegalStateException("Duplicate key: " + key);
		}
	}

	public int size() {
		return this.prototypeMap.size();
	}

	public Collection<IPrototype> values() {
		return this.prototypeMap.values();
	}

	public static PrototypeRegistry getInstance() {
		return PrototypeRegistry.INSTANCE;
	}
}
