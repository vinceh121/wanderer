package me.vinceh121.wanderer.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.DisplayModel;

public class PreviewBuilding extends AbstractBuilding {

	public PreviewBuilding(final Wanderer game, final AbstractBuildingMeta meta) {
		super(game, meta);

		this.setCollideModel(null);

		for (final DisplayModel m : this.getModels()) {
			m.addTextureAttribute(ColorAttribute.createEmissive(new Color(0f, 0.8f, 1f, 0f)));
			m.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_DST_COLOR, 0.5f));
		}
	}

	// to not add interaction zone
	@Override
	public void enterBtWorld(final btDiscreteDynamicsWorld world) {
	}

	@Override
	public void leaveBtWorld(final btDiscreteDynamicsWorld world) {
	}
}
