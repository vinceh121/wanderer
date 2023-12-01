package me.vinceh121.wanderer;

import org.mozilla.javascript.NativeJavaObject;
import org.mozilla.javascript.Scriptable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Align;

import me.vinceh121.wanderer.event.Event;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.story.Chapter;
import me.vinceh121.wanderer.story.Part;
import me.vinceh121.wanderer.story.StoryBook;
import me.vinceh121.wanderer.ui.ObjectivesView;

public class StoryWanderer extends Wanderer {
	private static FileHandle STORY_SCRIPTS_ROOT;
	private ObjectivesView objectivesView;
	private StoryBook storyBook;
	private Chapter chapter;
	private Part part;

	public StoryWanderer(final ApplicationMultiplexer applicationMultiplexer) {
		super(applicationMultiplexer);
	}

	@Override
	public void create() {
		super.create();
		StoryWanderer.STORY_SCRIPTS_ROOT = Gdx.files.internal("story"); // FIXME this could and should be purely static
		this.objectivesView = new ObjectivesView();
		this.objectivesView.setVisible(false);
		this.objectivesView.align(Align.center);
		this.getGraphicsManager().getStage().addActor(this.objectivesView);

		this.getInputManager().addListener(new InputListenerAdapter(0) {
			@Override
			public boolean inputDown(final Input in) {
				if (in == Input.SHOW_OBJECTIVES) {
					StoryWanderer.this.objectivesView.setVisible(true);
					return true;
				}
				return false;
			}

			@Override
			public boolean inputUp(final Input in) {
				if (in == Input.SHOW_OBJECTIVES) {
					StoryWanderer.this.objectivesView.setVisible(false);
					return true;
				}
				return false;
			}
		});

		// BY CALLER

		this.startStory("singleplayer", 0, 0);
	}

	@Override
	public void resize(final int width, final int height) {
		super.resize(width, height);
		this.objectivesView.setX(width / 2);
		this.objectivesView.setY(height - 60);
	}

	public void startStory(final String name, final int chapter, final int part) {
		final Scriptable exports = this.getScriptManager()
			.loadModule(StoryWanderer.STORY_SCRIPTS_ROOT.child(name + ".js"), StoryWanderer.STORY_SCRIPTS_ROOT);
		this.storyBook = (StoryBook) ((NativeJavaObject) exports.get("storyBook", exports)).unwrap();
		this.chapter = this.storyBook.getChapters().get(chapter);
		this.part = this.chapter.getParts().get(part);

		this.objectivesView.setTitle(this.part.getTitle());
		this.objectivesView.setObjectives(this.part.getObjectives());

		this.part.addEventListener("objectiveCompleted", this::onObjectiveCompleted);
		this.flushEntityQueue(); // FIXME shouldn't be here
		this.part.getPartStart().run();
	}

	private void onObjectiveCompleted(final Event e) {
		this.objectivesView.setObjectivesCompleted(this.part.getObjectivesCompleted());
	}

	public StoryBook getStoryBook() {
		return this.storyBook;
	}

	public Chapter getChapter() {
		return this.chapter;
	}

	public Part getPart() {
		return this.part;
	}
}
