package me.vinceh121.wanderer.script;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.commonjs.module.provider.ModuleSource;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class FileHandleModuleSourceProvider implements ModuleSourceProvider {
	private final URI base;

	public FileHandleModuleSourceProvider(final URI base) {
		this.base = base;
	}

	public FileHandleModuleSourceProvider(final FileHandle base) {
		this.base = FileHandleModuleSourceProvider.fromFileHandle(base);
	}

	@Override
	public ModuleSource loadSource(final String moduleId, final Scriptable paths, final Object validator)
			throws IOException, URISyntaxException {
		return this.loadSource(new URI(moduleId), this.base, validator);
	}

	@Override
	public ModuleSource loadSource(final URI uri, final URI baseUri, final Object validator)
			throws IOException, URISyntaxException {
		FileHandle fh = FileHandleModuleSourceProvider.fromURI(uri);
		if (!fh.path().endsWith(".js") && !fh.exists()) {
			fh = fh.sibling(fh.name() + ".js");
		}
		final ModuleSource src = new ModuleSource(fh.reader(), null, uri, baseUri, validator);
		return src;
	}

	public static URI fromFileHandle(final FileHandle fh) {
		try {
			return new URI("fh", fh.type().toString(), fh.path().startsWith("/") ? fh.path() : "/" + fh.path(), null);
		} catch (final URISyntaxException e) {
			// shouldn't happen
			throw new RuntimeException(e);
		}
	}

	public static FileHandle fromURI(final URI uri) {
		if (!"fh".equals(uri.getScheme())) {
			throw new RuntimeException("URI doesn't have expected fh schema");
		}
		return Gdx.files.getFileHandle(uri.getPath().substring(1), FileType.valueOf(uri.getHost()));
	}
}
