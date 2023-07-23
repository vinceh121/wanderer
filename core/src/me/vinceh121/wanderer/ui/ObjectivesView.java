package me.vinceh121.wanderer.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import me.vinceh121.wanderer.WandererConstants;

public class ObjectivesView extends Table {
	private final Label title = new Label("", this.getSkin());
	private final List<Cell<Label>> objectives = new ArrayList<>();
	private Set<Integer> objectivesComplete;

	public ObjectivesView() {
		super(WandererConstants.getDevSkin());
		this.title.setFontScale(1.5f);
		this.add(this.title).align(Align.center);
		this.row();
	}

	public void setTitle(final String title) {
		this.title.setText(title);
	}

	public void clearObjectives() {
		for (final Cell<Label> obj : this.objectives) {
			this.removeActor(obj.getActor());
		}
	}

	public void setObjectives(final List<String> newObjectives) {
		this.clearObjectives();
		int i = 0;

		for (; i < this.objectives.size(); i++) {
			this.objectives.get(i).getActor().setText(newObjectives.get(i));
		}

		for (; i < newObjectives.size(); i++) {
			this.objectives.add(this.add(newObjectives.get(i)).align(Align.left));
			this.row();
		}
	}

	public Set<Integer> getObjectivesCompleted() {
		return this.objectivesComplete;
	}

	public void setObjectivesCompleted(final Set<Integer> completedObjectives) {
		this.objectivesComplete = completedObjectives;
		this.updateObjectivesFormatting();
	}

	private void updateObjectivesFormatting() {
		for (final Cell<Label> element : this.objectives) {
			element.getActor().setColor(Color.WHITE);
		}

		for (final int obj : this.objectivesComplete) {
			this.objectives.get(obj).getActor().setColor(Color.GREEN);
		}
	}
}
