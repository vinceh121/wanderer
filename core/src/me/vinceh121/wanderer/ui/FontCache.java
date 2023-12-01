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

	public static BitmapFont get(final FileHandle path, final FontParameter parameters) {
		final FontCacheKey key = new FontCacheKey(path, parameters);
		BitmapFont font = FontCache.CACHE.get(key);

		if (font != null) {
			return font;
		}

		final FreeTypeFontGenerator gen = new FreeTypeFontGenerator(path);
		font = gen.generateFont(parameters);
		gen.dispose();

		FontCache.CACHE.put(key, font);

		return font;
	}

	private static class FontCacheKey {
		private final FileHandle path;
		private final FontParameter parameters;

		public FontCacheKey(final FileHandle path, final FontParameter parameters) {
			this.path = path;
			this.parameters = parameters;
		}

		public FileHandle getPath() {
			return this.path;
		}

		public FontParameter getParameters() {
			return this.parameters;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.parameters, this.path);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final FontCacheKey other = (FontCacheKey) obj;
			return Objects.equals(this.parameters, other.parameters) && Objects.equals(this.path, other.path);
		}

		@Override
		public String toString() {
			return "FontCacheKey [path=" + this.path + ", parameters=" + this.parameters + "]";
		}
	}

	public static class FontParameter extends FreeTypeFontParameter {
		public FontParameter() {
			this.characters += "АаБбВвГгДд†ЕеЁёЖжЗзИиЙйКкЛл‡МмНнОоПпРрСсТтУуФфХхЦцЧчШшЩщЪъЫыЬьЭэЮюЯя";
			this.hinting = Hinting.AutoFull;
		}

		@Override
		public int hashCode() {
			return Objects.hash(this.borderColor,
					this.borderGamma,
					this.borderStraight,
					this.borderWidth,
					this.characters,
					this.color,
					this.flip,
					this.gamma,
					this.genMipMaps,
					this.hinting,
					this.incremental,
					this.kerning,
					this.magFilter,
					this.minFilter,
					this.mono,
					this.packer,
					this.padBottom,
					this.padLeft,
					this.padRight,
					this.padTop,
					this.renderCount,
					this.shadowColor,
					this.shadowOffsetX,
					this.shadowOffsetY,
					this.size,
					this.spaceX,
					this.spaceY);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (this.getClass() != obj.getClass()) {
				return false;
			}
			final FontParameter other = (FontParameter) obj;
			return Objects.equals(this.borderColor, other.borderColor)
					&& Float.floatToIntBits(this.borderGamma) == Float.floatToIntBits(other.borderGamma)
					&& this.borderStraight == other.borderStraight
					&& Float.floatToIntBits(this.borderWidth) == Float.floatToIntBits(other.borderWidth)
					&& Objects.equals(this.characters, other.characters) && Objects.equals(this.color, other.color)
					&& this.flip == other.flip && Float.floatToIntBits(this.gamma) == Float.floatToIntBits(other.gamma)
					&& this.genMipMaps == other.genMipMaps && this.hinting == other.hinting && this.incremental == other.incremental
					&& this.kerning == other.kerning && this.magFilter == other.magFilter && this.minFilter == other.minFilter
					&& this.mono == other.mono && Objects.equals(this.packer, other.packer) && this.padBottom == other.padBottom
					&& this.padLeft == other.padLeft && this.padRight == other.padRight && this.padTop == other.padTop
					&& this.renderCount == other.renderCount && Objects.equals(this.shadowColor, other.shadowColor)
					&& this.shadowOffsetX == other.shadowOffsetX && this.shadowOffsetY == other.shadowOffsetY
					&& this.size == other.size && this.spaceX == other.spaceX && this.spaceY == other.spaceY;
		}
	}
}
