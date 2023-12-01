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

import me.vinceh121.wanderer.i18n.I18N;
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

		final Skin skin = WandererConstants.getUISkin();

		this.tblMain = new TblMain(skin);
		this.setView(this.tblMain);

		this.tblSingleplayer = new TblSingleplayer(skin);

		this.tblOptions = new OptionsView(skin);
		this.tblOptions.setOnApply(() -> this.setView(this.tblMain));
		this.tblOptions.setOnClose(() -> this.setView(this.tblMain));

		final String music = "orig/book/music/danger3.wav";
		WandererConstants.ASSET_MANAGER.load(music, Sound3D.class);
		WandererConstants.ASSET_MANAGER.finishLoadingAsset(music);
		this.music = WandererConstants.ASSET_MANAGER.get(music, Sound3D.class);
		this.musicEmitter = this.music.playGeneral();
	}

	public void setView(final Actor newView) {
		this.stage.clear();
		newView.setBounds(0, 0, 1024, 1024);
		this.stage.addActor(newView);
	}

	@Override
	public void resize(final int width, final int height) {
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
		public TblMain(final Skin skin) {
			super(skin);

			final int buttonPadding = 12;

			this.add(new Image(WandererConstants.ASSET_MANAGER.get("orig/locale/if_main.bmp", Texture.class)))
				.padBottom(64);
			this.row();

			final TextButton btnSingleplayer = new TextButton(I18N.gettext("Single player"), this.getSkin());
			btnSingleplayer.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					MainMenu.this.setView(MainMenu.this.tblSingleplayer);
				}
			});
			this.add(btnSingleplayer).padBottom(buttonPadding);
			this.row();

			this.add(new TextButton(I18N.gettext("Multiplayer"), this.getSkin())).padBottom(buttonPadding);
			this.row();

			final TextButton btnOptions = new TextButton(I18N.gettext("Options"), this.getSkin());
			btnOptions.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					MainMenu.this.setView(MainMenu.this.tblOptions);
				}
			});
			this.add(btnOptions).padBottom(buttonPadding);
			this.row();

			final TextButton btnQuit = new TextButton(I18N.gettext("Quit"), this.getSkin());
			btnQuit.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					Gdx.app.exit();
				}
			});
			this.add(btnQuit).padBottom(buttonPadding);
			this.row();

			this.add(new TextButton(I18N.gettext("Credits"), this.getSkin())).padBottom(buttonPadding);
		}
	}

	private class TblSingleplayer extends Table {

		public TblSingleplayer(final Skin skin) {
			super(skin);

			final int buttonPadding = 12;

			final TextButton btnNewGame = new TextButton(I18N.gettext("New Game"), this.getSkin());
			btnNewGame.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					final StoryWanderer story = new StoryWanderer(MainMenu.this.applicationMultiplexer);
					story.create();
					story.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
					MainMenu.this.applicationMultiplexer.setDelegate(story);
					MainMenu.this.dispose();
				}
			});
			this.add(btnNewGame).padBottom(buttonPadding);
			this.row();

			this.add(new TextButton(I18N.gettext("Load Game"), this.getSkin())).padBottom(buttonPadding);
			this.row();

			final TextButton btnCancel = new TextButton(I18N.gettext("Cancel"), this.getSkin());
			btnCancel.addListener(new ClickListener() {
				@Override
				public void clicked(final InputEvent event, final float x, final float y) {
					MainMenu.this.setView(MainMenu.this.tblMain);
				}
			});
			this.add(btnCancel).padBottom(buttonPadding);
		}
	}
}
