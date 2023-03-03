package me.vinceh121.wanderer.ui;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

import me.vinceh121.wanderer.WandererConstants;

public class ObjectivesView extends Table {
	private final Label title = new Label("", this.getSkin());
	private final List<Cell<Label>> objectives = new ArrayList<>();

	public ObjectivesView() {
		super(WandererConstants.getDevSkin());
		this.add(this.title).align(Align.center);
		this.row();
	}

	public void setTitle(String title) {
		this.title.setText(title);
	}

	public void clearObjectives() {
		for (Cell<Label> obj : this.objectives) {
			this.removeActor(obj.getActor());
		}
	}

	public void setObjectives(List<String> newObjectives) {
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
}
