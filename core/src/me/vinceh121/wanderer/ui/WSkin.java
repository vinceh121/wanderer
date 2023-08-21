package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.ReadOnlySerializer;
import com.badlogic.gdx.utils.Json.Serializer;

import me.vinceh121.wanderer.ui.FontCache.FontParameter;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.SerializationException;

/**
 * To add TTF font deserializer
 */
public class WSkin extends Skin {

	public WSkin() {
	}

	public WSkin(FileHandle skinFile, TextureAtlas atlas) {
		super(skinFile, atlas);
	}

	public WSkin(FileHandle skinFile) {
		super(skinFile);
	}

	public WSkin(TextureAtlas atlas) {
		super(atlas);
	}

	@Override
	protected Json getJsonLoader(final FileHandle skinFile) {
		final Json json = super.getJsonLoader(skinFile);

		Serializer<BitmapFont> fontSer = json.getSerializer(BitmapFont.class);

		json.setSerializer(BitmapFont.class, new ReadOnlySerializer<BitmapFont>() {
			@Override
			public BitmapFont read(Json json, JsonValue jsonData, @SuppressWarnings("rawtypes") Class type) {
				String path = json.readValue("file", String.class, jsonData);
				if (path.endsWith(".ttf")) {
					FileHandle fontFile = skinFile.parent().child(path);
					if (!fontFile.exists())
						fontFile = Gdx.files.internal(path);
					if (!fontFile.exists())
						throw new SerializationException("Font file not found: " + fontFile);

					FontParameter parameters = new FontParameter();
					parameters.size = json.readValue("size", int.class, 16, jsonData);
					parameters.color = Color.valueOf(json.readValue("color", String.class, "#ffffff", jsonData));
					return FontCache.get(fontFile, parameters);
				} else {
					return fontSer.read(json, jsonData, type);
				}
			}
		});

		return json;
	}
}
