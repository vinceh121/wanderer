package me.vinceh121.wanderer.ui;

import static me.vinceh121.wanderer.i18n.I18N.gettext;

import java.util.Locale;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics.DisplayMode;
import com.badlogic.gdx.Graphics.Monitor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.util.GraphicsUtilities;

public class OptionsView extends Table {
	private Runnable onClose = () -> {
	};
	private Runnable onApply = () -> {
	};
	private MonitorSelect selMon;
	private ResolutionSelect selRes;
	private CheckBox chkVSync;
	private LangSelect speechSelect;

	public OptionsView(Skin skin) {
		super(skin);

		Label lblOptions = new Label(gettext("Options"), skin);
		lblOptions.setFontScale(2);
		this.add(lblOptions).padBottom(64).colspan(2);
		this.row();

		////// GENERAL
		Label lblGeneral = new Label(gettext("General"), skin);
		lblGeneral.setFontScale(1.5f);
		this.add(lblGeneral).padTop(32);
		this.row();

		this.add(new Label(gettext("Interface language"), skin));
		this.add(new LangSelect(skin, "en", "de", "fr", "it", "ru"));
		this.row();

		this.add(new Label(gettext("Audio language"), skin));
		this.speechSelect = new LangSelect(skin, "en", "de", "ru");
		this.add(this.speechSelect);
		this.row();

		///// GRAPHICS
		this.add(new Label(gettext("Graphics"), skin)).padTop(32);
		this.row();

		chkVSync = new CheckBox(gettext("V-Sync"), skin);
		chkVSync.setChecked(Preferences.getPreferences().<Boolean>getOrElse("graphics.vsync", false));
		this.add(chkVSync);
		this.row();

		this.add(new Label(gettext("Window mode"), skin));
		this.selMon = new MonitorSelect(skin);
		if (Preferences.getPreferences().get("graphics.monitor") instanceof Integer) {
			this.selMon.setSelected(Windowed.WINDOWED);
		} else if (Preferences.getPreferences().contains("graphics.monitor")) {
			this.selMon.setSelected(GraphicsUtilities.getMonitor(this.selMon.getItems().toArray(Monitor.class),
					Preferences.getPreferences().<String>get("graphics.monitor")));
		}
		this.add(selMon);
		this.row();

		this.add(new Label(gettext("Resolution"), skin));
		this.selRes = new ResolutionSelect(skin);
		if (this.selMon.getSelected() == Windowed.WINDOWED) {
			this.selRes.setSelected(null);
		} else {
			this.selRes.setSelected(GraphicsUtilities.getDisplayMode(this.selRes.getItems().toArray(DisplayMode.class),
					Preferences.getPreferences().<String>get("graphics.resolution")));
		}
		this.add(selRes);
		this.row();

		TextButton btnCancel = new TextButton(gettext("Cancel"), skin);
		btnCancel.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				onClose.run();
			}
		});
		this.add(btnCancel);

		TextButton btnApply = new TextButton(gettext("Apply"), skin);
		btnApply.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				apply();
				onApply.run();
			}
		});
		this.add(btnApply);

		this.selMon.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Monitor mon = selMon.getSelected();
				if (mon != Windowed.WINDOWED) {
					selRes.setDisabled(false);
					selRes.setItems(Gdx.graphics.getDisplayModes(mon));
				} else {
					selRes.setDisabled(true);
					selRes.setSelected(None.NONE);
				}
			}
		});
	}

	private void apply() {
		if (this.selMon.getSelected() == Windowed.WINDOWED) {
			Preferences.getPreferences().set("graphics.monitor", -1);
		} else {
			Preferences.getPreferences().set("graphics.monitor", this.selMon.getSelected().name);
			Preferences.getPreferences().set("graphics.resolution", this.selRes.getSelected().toString());
		}

		Preferences.getPreferences().set("graphics.vsync", this.chkVSync.isChecked());

		Preferences.getPreferences().set("i18n.speech", this.speechSelect.getSelected());

		GraphicsUtilities.setFromPreferences();
	}

	public Runnable getOnClose() {
		return onClose;
	}

	public void setOnClose(Runnable onClose) {
		this.onClose = onClose;
	}

	public Runnable getOnApply() {
		return onApply;
	}

	public void setOnApply(Runnable onApply) {
		this.onApply = onApply;
	}

	private static class ResolutionSelect extends SelectBox<DisplayMode> {
		public ResolutionSelect(Skin skin) {
			super(skin);
		}

		@Override
		protected String toString(DisplayMode item) {
			if (item == None.NONE) {
				return gettext("<select fullscreen>");
			} else {
				return item.width + "x" + item.height + " " + item.refreshRate + /* Hertz unit */gettext("Hz");
			}
		}
	}

	private static class MonitorSelect extends SelectBox<Monitor> {
		public MonitorSelect(Skin skin) {
			super(skin);

			Monitor[] mons = new Monitor[Gdx.graphics.getMonitors().length + 1];
			mons[0] = Windowed.WINDOWED;
			System.arraycopy(Gdx.graphics.getMonitors(), 0, mons, 1, Gdx.graphics.getMonitors().length);

			setItems(mons);
		}

		@Override
		protected String toString(Monitor item) {
			if (item.name.equals(Gdx.graphics.getPrimaryMonitor().name)) {
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

	private static class None extends DisplayMode {
		public static final None NONE = new None();

		public None() {
			super(-1, -1, -1, -1);
		}
	}

	public static class Windowed extends Monitor {
		public static final Monitor WINDOWED = new Windowed();

		protected Windowed() {
			super(-1, -1, gettext("Windowed"));
		}
	}
}
