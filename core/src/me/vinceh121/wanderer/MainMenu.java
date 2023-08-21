package me.vinceh121.wanderer;

import static me.vinceh121.wanderer.i18n.I18N.gettext;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;
import me.vinceh121.wanderer.ui.OptionsView;

public class MainMenu extends ApplicationDelegate {
	private Stage stage;
	private Actor tblMain, tblSingleplayer;
	private OptionsView tblOptions;
	private Sound3D music;
	private SoundEmitter3D musicEmitter;

	public MainMenu(final ApplicationMultiplexer applicationMultiplexer) {
		super(applicationMultiplexer);
	}

	@Override
	public void create() {
		this.stage = new Stage(new FitViewport(1024, 1024));
		Gdx.input.setInputProcessor(this.stage);

		Skin skin = WandererConstants.getUISkin();

		this.tblMain = new TblMain(skin);
		this.setView(this.tblMain);

		this.tblSingleplayer = new TblSingleplayer(skin);

		this.tblOptions = new OptionsView(skin);
		this.tblOptions.setOnApply(() -> this.setView(tblMain));
		this.tblOptions.setOnClose(() -> this.setView(tblMain));

		final String music = "orig/book/music/danger3.wav";
		WandererConstants.ASSET_MANAGER.load(music, Sound3D.class);
		WandererConstants.ASSET_MANAGER.finishLoadingAsset(music);
		this.music = WandererConstants.ASSET_MANAGER.get(music, Sound3D.class);
		this.musicEmitter = this.music.playGeneral();
	}

	public void setView(Actor newView) {
		this.stage.clear();
		newView.setBounds(0, 0, 1024, 1024);
		this.stage.addActor(newView);
	}

	@Override
	public void resize(int width, int height) {
		this.stage.getViewport().update(width, height);
	}

	@Override
	public void render() {
		this.stage.act(Gdx.graphics.getDeltaTime());

		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		this.stage.draw();
	}

	@Override
	public void dispose() {
		this.musicEmitter.dispose();
		this.music.dispose();
	}

	private class TblMain extends Table {
		public TblMain(Skin skin) {
			super(skin);

			final int buttonPadding = 12;

			this.add(new Image(WandererConstants.ASSET_MANAGER.get("orig/locale/if_main.bmp", Texture.class)))
				.padBottom(64);
			this.row();

			TextButton btnSingleplayer = new TextButton(gettext("Single player"), this.getSkin());
			btnSingleplayer.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					setView(tblSingleplayer);
				}
			});
			this.add(btnSingleplayer).padBottom(buttonPadding);
			this.row();

			this.add(new TextButton(gettext("Multiplayer"), this.getSkin())).padBottom(buttonPadding);
			this.row();

			TextButton btnOptions = new TextButton(gettext("Options"), this.getSkin());
			btnOptions.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					setView(tblOptions);
				}
			});
			this.add(btnOptions).padBottom(buttonPadding);
			this.row();

			TextButton btnQuit = new TextButton(gettext("Quit"), this.getSkin());
			btnQuit.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
				}
			});
			this.add(btnQuit).padBottom(buttonPadding);
			this.row();

			this.add(new TextButton(gettext("Credits"), this.getSkin())).padBottom(buttonPadding);
		}
	}

	private class TblSingleplayer extends Table {

		public TblSingleplayer(Skin skin) {
			super(skin);

			final int buttonPadding = 12;

			TextButton btnNewGame = new TextButton(gettext("New Game"), this.getSkin());
			btnNewGame.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					StoryWanderer story = new StoryWanderer(applicationMultiplexer);
					story.create();
					story.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					applicationMultiplexer.setDelegate(story);
					dispose();
				}
			});
			this.add(btnNewGame).padBottom(buttonPadding);
			this.row();

			this.add(new TextButton(gettext("Load Game"), this.getSkin())).padBottom(buttonPadding);
			this.row();

			TextButton btnCancel = new TextButton(gettext("Cancel"), this.getSkin());
			btnCancel.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					setView(tblMain);
				}
			});
			this.add(btnCancel).padBottom(buttonPadding);
		}
	}
}
