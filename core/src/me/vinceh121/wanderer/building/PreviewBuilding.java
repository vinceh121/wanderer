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

	public PreviewBuilding(final Wanderer game, final AbstractBuildingPrototype prototype) {
		super(game, prototype);

		this.setCollideModel(null);

		for (final DisplayModel m : this.getModels()) {
			m.addTextureAttribute(ColorAttribute.createEmissive(PreviewBuilding.BLUE));
			m.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_DST_COLOR, 0.5f));
		}
	}

	@Override
	public void enterBtWorld(final btDiscreteDynamicsWorld world) {
		super.enterBtWorld(world);
		final ContactResultCallback cb = new ContactResultCallback() {
			@Override
			public float addSingleResult(final btManifoldPoint cp, final btCollisionObjectWrapper colObj0Wrap,
					final int partId0, final int index0, final btCollisionObjectWrapper colObj1Wrap, final int partId1,
					final int index1) {
				final int idx = colObj1Wrap.getCollisionObject().getUserIndex();
				if (PreviewBuilding.this.game.getEntity(idx) instanceof IControllableEntity) {
					PreviewBuilding.this.setBlocked(true);
				}
				return 0;
			}
		};
		this.game.getBtWorld().contactTest(this.getInteractZone(), cb);
		cb.dispose();
	}

	@Override
	protected void onInteractContact(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		this.setBlocked(true);
	}

	@Override
	protected void onInteractStop(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		this.setBlocked(false);
	}

	private void setBlocked(final boolean b) {
		this.blocked = b;
		if (b) {
			this.setColor(PreviewBuilding.RED);
		} else {
			this.setColor(PreviewBuilding.BLUE);
		}
	}

	private void setColor(final Color c) {
		for (final DisplayModel m : this.getModels()) {
			for (final Attribute att : m.getTextureAttributes()) {
				if (att instanceof ColorAttribute) {
					((ColorAttribute) att).color.set(c);
				}
			}
		}
	}

	public boolean isBlocked() {
		return this.blocked;
	}
}
