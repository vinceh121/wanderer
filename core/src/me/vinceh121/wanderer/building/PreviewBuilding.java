package me.vinceh121.wanderer.building;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.entity.IControllableEntity;

public class PreviewBuilding extends AbstractBuilding {
	private static final Color BLUE = new Color(0f, 0.8f, 1f, 0f), RED = new Color(1f, 0.1f, 0f, 0f);
	private boolean blocked;

	public PreviewBuilding(final Wanderer game, final AbstractBuildingMeta meta) {
		super(game, meta);

		this.setCollideModel(null);

		for (final DisplayModel m : this.getModels()) {
			m.addTextureAttribute(ColorAttribute.createEmissive(BLUE));
			m.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_DST_COLOR, 0.5f));
		}
	}

	@Override
	public void enterBtWorld(btDiscreteDynamicsWorld world) {
		super.enterBtWorld(world);
		ContactResultCallback cb = new ContactResultCallback() {
			@Override
			public float addSingleResult(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0,
					int index0, btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
				int idx = colObj1Wrap.getCollisionObject().getUserIndex();
				if (game.getEntity(idx) instanceof IControllableEntity) {
					setBlocked(true);
				}
				return 0;
			}
		};
		this.game.getBtWorld().contactTest(getInteractZone(), cb);
		cb.dispose();
	}

	@Override
	protected void onInteractContact(btCollisionObject colObj0, btCollisionObject colObj1) {
		this.setBlocked(true);
	}

	@Override
	protected void onInteractStop(btCollisionObject colObj0, btCollisionObject colObj1) {
		this.setBlocked(false);
	}

	private void setBlocked(boolean b) {
		this.blocked = b;
		if (b) {
			this.setColor(RED);
		} else {
			this.setColor(BLUE);
		}
	}

	private void setColor(Color c) {
		for (final DisplayModel m : this.getModels()) {
			for (final Attribute att : m.getTextureAttributes()) {
				if (att instanceof ColorAttribute) {
					((ColorAttribute) att).color.set(c);
				}
			}
		}
	}

	public boolean isBlocked() {
		return blocked;
	}
}
