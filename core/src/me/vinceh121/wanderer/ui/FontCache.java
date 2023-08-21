package me.vinceh121.wanderer.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.Hinting;

public final class FontCache {
	private static final Map<FontCacheKey, BitmapFont> CACHE = new HashMap<>();

	public static BitmapFont get(FileHandle path, FontParameter parameters) {
		FontCacheKey key = new FontCacheKey(path, parameters);
		BitmapFont font = CACHE.get(key);

		if (font != null) {
			return font;
		}

		FreeTypeFontGenerator gen = new FreeTypeFontGenerator(path);
		font = gen.generateFont(parameters);
		gen.dispose();

		CACHE.put(key, font);

		return font;
	}

	private static class FontCacheKey {
		private final FileHandle path;
		private final FontParameter parameters;

		public FontCacheKey(FileHandle path, FontParameter parameters) {
			this.path = path;
			this.parameters = parameters;
		}

		public FileHandle getPath() {
			return path;
		}

		public FontParameter getParameters() {
			return parameters;
		}

		@Override
		public int hashCode() {
			return Objects.hash(parameters, path);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FontCacheKey other = (FontCacheKey) obj;
			return Objects.equals(parameters, other.parameters) && Objects.equals(path, other.path);
		}

		@Override
		public String toString() {
			return "FontCacheKey [path=" + path + ", parameters=" + parameters + "]";
		}
	}

	public static class FontParameter extends FreeTypeFontParameter {
		public FontParameter() {
			this.characters += "АаБбВвГгДд†ЕеЁёЖжЗзИиЙйКкЛл‡МмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя";
			this.hinting = Hinting.AutoFull;
		}

		@Override
		public int hashCode() {
			return Objects.hash(borderColor,
					borderGamma,
					borderStraight,
					borderWidth,
					characters,
					color,
					flip,
					gamma,
					genMipMaps,
					hinting,
					incremental,
					kerning,
					magFilter,
					minFilter,
					mono,
					packer,
					padBottom,
					padLeft,
					padRight,
					padTop,
					renderCount,
					shadowColor,
					shadowOffsetX,
					shadowOffsetY,
					size,
					spaceX,
					spaceY);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FontParameter other = (FontParameter) obj;
			return Objects.equals(borderColor, other.borderColor)
					&& Float.floatToIntBits(borderGamma) == Float.floatToIntBits(other.borderGamma)
					&& borderStraight == other.borderStraight
					&& Float.floatToIntBits(borderWidth) == Float.floatToIntBits(other.borderWidth)
					&& Objects.equals(characters, other.characters) && Objects.equals(color, other.color)
					&& flip == other.flip && Float.floatToIntBits(gamma) == Float.floatToIntBits(other.gamma)
					&& genMipMaps == other.genMipMaps && hinting == other.hinting && incremental == other.incremental
					&& kerning == other.kerning && magFilter == other.magFilter && minFilter == other.minFilter
					&& mono == other.mono && Objects.equals(packer, other.packer) && padBottom == other.padBottom
					&& padLeft == other.padLeft && padRight == other.padRight && padTop == other.padTop
					&& renderCount == other.renderCount && Objects.equals(shadowColor, other.shadowColor)
					&& shadowOffsetX == other.shadowOffsetX && shadowOffsetY == other.shadowOffsetY
					&& size == other.size && spaceX == other.spaceX && spaceY == other.spaceY;
		}
	}
}
