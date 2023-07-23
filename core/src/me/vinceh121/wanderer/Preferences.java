package me.vinceh121.wanderer;

import java.nio.file.Files;
import java.nio.file.Path;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

public final class Preferences {
	private static CommentedConfig PREFERENCES;

	public static void loadPreferences(final Path path) {
		Preferences.PREFERENCES = CommentedFileConfig.builder(path, TomlFormat.instance()).onFileNotFound((f, c) -> {
			Files.createDirectories(f.getParent());
			Files.createFile(f);
			c.initEmptyFile(f);
			return false;
		}).autosave().build();

		((CommentedFileConfig) Preferences.PREFERENCES).load();
	}

	public static void loadInMemory() {
		Preferences.PREFERENCES = CommentedConfig.inMemoryConcurrent();
	}

	public static CommentedConfig getPreferences() {
		return Preferences.PREFERENCES;
	}
}
