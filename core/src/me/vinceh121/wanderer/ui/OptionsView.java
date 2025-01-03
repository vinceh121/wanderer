package me.vinceh121.wanderer.ui;

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
import me.vinceh121.wanderer.i18n.I18N;
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

	public OptionsView(final Skin skin) {
		super(skin);

		final Label lblOptions = new Label(I18N.gettext("Options"), skin);
		lblOptions.setFontScale(2);
		this.add(lblOptions).padBottom(64).colspan(2);
		this.row();

		////// GENERAL
		final Label lblGeneral = new Label(I18N.gettext("General"), skin);
		lblGeneral.setFontScale(1.5f);
		this.add(lblGeneral).padTop(32);
		this.row();

		this.add(new Label(I18N.gettext("Interface language"), skin));
		this.add(new LangSelect(skin, "en", "de", "fr", "it", "ru"));
		this.row();

		this.add(new Label(I18N.gettext("Audio language"), skin));
		this.speechSelect = new LangSelect(skin, "en", "de", "ru");
		this.add(this.speechSelect);
		this.row();

		///// GRAPHICS
		this.add(new Label(I18N.gettext("Graphics"), skin)).padTop(32);
		this.row();

		this.chkVSync = new CheckBox(I18N.gettext("V-Sync"), skin);
		this.chkVSync.setChecked(Preferences.getPreferences().<Boolean>getOrElse("graphics.vsync", false));
		this.add(this.chkVSync);
		this.row();

		this.add(new Label(I18N.gettext("Window mode"), skin));
		this.selMon = new MonitorSelect(skin);
		if (Preferences.getPreferences().get("graphics.monitor") instanceof Integer) {
			this.selMon.setSelected(Windowed.WINDOWED);
		} else if (Preferences.getPreferences().contains("graphics.monitor")) {
			this.selMon.setSelected(GraphicsUtilities.getMonitor(this.selMon.getItems().toArray(Monitor.class),
					Preferences.getPreferences().<String>get("graphics.monitor")));
		}
		this.add(this.selMon);
		this.row();

		this.add(new Label(I18N.gettext("Resolution"), skin));
		this.selRes = new ResolutionSelect(skin);
		if (this.selMon.getSelected() == Windowed.WINDOWED) {
			this.selRes.setSelected(null);
		} else {
			this.selRes.setSelected(GraphicsUtilities.getDisplayMode(this.selRes.getItems().toArray(DisplayMode.class),
					Preferences.getPreferences().<String>get("graphics.resolution")));
		}
		this.add(this.selRes);
		this.row();

		final TextButton btnCancel = new TextButton(I18N.gettext("Cancel"), skin);
		btnCancel.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				OptionsView.this.onClose.run();
			}
		});
		this.add(btnCancel);

		final TextButton btnApply = new TextButton(I18N.gettext("Apply"), skin);
		btnApply.addListener(new ClickListener() {
			@Override
			public void clicked(final InputEvent event, final float x, final float y) {
				OptionsView.this.apply();
				OptionsView.this.onApply.run();
			}
		});
		this.add(btnApply);

		this.selMon.addListener(new ChangeListener() {
			@Override
			public void changed(final ChangeEvent event, final Actor actor) {
				final Monitor mon = OptionsView.this.selMon.getSelected();
				if (mon != Windowed.WINDOWED) {
					OptionsView.this.selRes.setDisabled(false);
					OptionsView.this.selRes.setItems(Gdx.graphics.getDisplayModes(mon));
				} else {
					OptionsView.this.selRes.setDisabled(true);
					OptionsView.this.selRes.setSelected(None.NONE);
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
		return this.onClose;
	}

	public void setOnClose(final Runnable onClose) {
		this.onClose = onClose;
	}

	public Runnable getOnApply() {
		return this.onApply;
	}

	public void setOnApply(final Runnable onApply) {
		this.onApply = onApply;
	}

	private static class ResolutionSelect extends SelectBox<DisplayMode> {
		public ResolutionSelect(final Skin skin) {
			super(skin);
		}

		@Override
		protected String toString(final DisplayMode item) {
			if (item == None.NONE) {
				return I18N.gettext("<select fullscreen>");
			} else {
				return item.width + "x" + item.height + " " + item.refreshRate + /* Hertz unit */I18N.gettext("Hz");
			}
		}
	}

	private static class MonitorSelect extends SelectBox<Monitor> {
		public MonitorSelect(final Skin skin) {
			super(skin);

			final Monitor[] mons = new Monitor[Gdx.graphics.getMonitors().length + 1];
			mons[0] = Windowed.WINDOWED;
			System.arraycopy(Gdx.graphics.getMonitors(), 0, mons, 1, Gdx.graphics.getMonitors().length);

			this.setItems(mons);
		}

		@Override
		protected String toString(final Monitor item) {
			if (item.name.equals(Gdx.graphics.getPrimaryMonitor().name)) {
				return I18N.gettext("(Primary)") + " " + item.name;
			} else {
				return item.name;
			}
		}
	}

	private static class LangSelect extends SelectBox<String> {
		public LangSelect(final Skin skin, final String... langs) {
			super(skin);

			this.setItems(langs);
		}

		@Override
		protected String toString(final String item) {
			final Locale loc = Locale.forLanguageTag(item);
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
			super(-1, -1, I18N.gettext("Windowed"));
		}
	}
}
