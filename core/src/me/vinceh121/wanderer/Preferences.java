package me.vinceh121.wanderer;

import java.nio.file.Files;
import java.nio.file.Path;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

public final class Preferences {
	private static CommentedConfig PREFERENCES;

	public static void loadPreferences(Path path) {
		PREFERENCES = CommentedFileConfig.builder(path, TomlFormat.instance()).onFileNotFound((f, c) -> {
			Files.createDirectories(f.getParent());
			Files.createFile(f);
			c.initEmptyFile(f);
			return false;
		}).autosave().build();

		((CommentedFileConfig) PREFERENCES).load();
	}

	public static void loadInMemory() {
		PREFERENCES = CommentedConfig.inMemoryConcurrent();
	}

	public static CommentedConfig getPreferences() {
		return PREFERENCES;
	}
}
