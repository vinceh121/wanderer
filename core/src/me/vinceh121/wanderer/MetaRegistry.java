package me.vinceh121.wanderer;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.files.FileHandle;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;

public class MetaRegistry {
	private static final MetaRegistry INSTANCE = new MetaRegistry();
	private final Map<String, IMeta> metaMap = new HashMap<>();

	public <T extends IMeta> void readMetas(FileHandle fh) throws StreamReadException, DatabindException, IOException {
		final Map<String, T> read = WandererConstants.MAPPER.readValue(fh.read(), new TypeReference<Map<String, T>>() {
		});
		this.putAll(read);
	}

	public void clear() {
		metaMap.clear();
	}

	public boolean containsKey(String key) {
		return metaMap.containsKey(key);
	}

	public boolean containsValue(IMeta value) {
		return metaMap.containsValue(value);
	}

	public Set<Entry<String, IMeta>> entrySet() {
		return metaMap.entrySet();
	}

	@SuppressWarnings("unchecked")
	public <T extends IMeta> T get(String key) {
		return (T) metaMap.get(key);
	}

	public void putAll(Map<? extends String, ? extends IMeta> m) {
		for (String key : m.keySet()) {
			if (this.metaMap.containsKey(key)) {
				throw new IllegalStateException("Duplicate key: " + key);
			}
		}
		metaMap.putAll(m);
	}

	public void put(String key, IMeta value) {
		final IMeta previous = metaMap.put(key, value);
		if (previous != null) {
			throw new IllegalStateException("Duplicate key: " + key);
		}
	}

	public int size() {
		return metaMap.size();
	}

	public Collection<IMeta> values() {
		return metaMap.values();
	}

	public static MetaRegistry getInstance() {
		return INSTANCE;
	}
}
