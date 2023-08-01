package me.vinceh121.wanderer.ui;

import static me.vinceh121.wanderer.i18n.I18N.gettext;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class OptionsView extends Table {

	public OptionsView(Skin skin) {
		super(skin);

		this.add(new Label(gettext("Options"), skin)).padBottom(64).colspan(2);
		this.row();

		////// GENERAL
		this.add(new Label(gettext("General"), skin)).padTop(32);
		this.row();

		this.add(new Label(gettext("Interface language"), skin));
		this.add(new LangSelect(skin, "en", "de", "fr", "it", "ru"));
		this.row();

		this.add(new Label(gettext("Audio language"), skin));
		this.add(new LangSelect(skin, "en", "de", "ru"));
		this.row();

		///// GRAPHICS
		this.add(new Label(gettext("Graphics"), skin)).padTop(32);
		this.row();

		this.add(new CheckBox(gettext("V-Sync"), skin));
		this.row();

		this.add(new Label(gettext("Window mode"), skin));
		this.add(new WindowModeSelect(skin));
		this.row();

		this.add(new Label(gettext("Resolution"), skin));
		this.add(new ResolutionSelect(skin));
	}

	private static class ResolutionSelect extends SelectBox<DisplayMode> {
		public ResolutionSelect(Skin skin) {
			super(skin);
		}

		@Override
		protected String toString(DisplayMode item) {
			return item.width + "x" + item.height + " " + item.refreshRate + "Hz";
		}
	}

	private static class WindowModeSelect extends SelectBox<Monitor> {
		public WindowModeSelect(Skin skin) {
			super(skin);

			Monitor[] mons = new Monitor[Gdx.graphics.getMonitors().length + 1];
			mons[0] = Windowed.WINDOWED;
			System.arraycopy(Gdx.graphics.getMonitors(), 0, mons, 1, Gdx.graphics.getMonitors().length);

			setItems(mons);
		}

		@Override
		protected String toString(Monitor item) {
			if (item == null) {
				return gettext("Windowed");
			} else if (item.name.equals(Gdx.graphics.getPrimaryMonitor().name)) {
				return gettext("(Primary)") + " " + item.name;
			} else {
				return item.name;
			}
		}
	}

	private static class LangSelect extends SelectBox<String> {
		public LangSelect(Skin skin, String... langs) {
			super(skin);

			setItems(langs);
		}

		@Override
		protected String toString(String item) {
			Locale loc = Locale.forLanguageTag(item);
			return loc.getDisplayLanguage(loc);
		}
	}

	public static class Windowed extends Monitor {
		public static final Monitor WINDOWED = new Windowed();

		protected Windowed() {
			super(-1, -1, gettext("Windowed"));
		}
	}
}
