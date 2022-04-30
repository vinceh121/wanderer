package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public class ArtifactEntity extends AbstractEntity {
	private final ArtifactMeta artifact;
	private final btGhostObject interactZone;
	private final IContactListener interactListener;

	public ArtifactEntity(Wanderer game, ArtifactMeta artifact) {
		super(game);
		this.artifact = artifact;
		this.setDisplayTexture(artifact.getArtifactTexture());
		this.setDisplayModel(artifact.getArtifactModel());
		if (this.artifact.isRed()) {
			this.addTextureAttribute(ColorAttribute.createEmissive(0f, 0.7f, 1f, 0f));// TODO find red color
		} else {
			this.addTextureAttribute(ColorAttribute.createEmissive(0f, 0.7f, 1f, 0f));
		}
		this.addTextureAttribute(new BlendingAttribute(GL20.GL_ALPHA, GL20.GL_ONE, 0.5f));

		this.interactZone = new btGhostObject();
		this.interactZone.setCollisionFlags(CollisionFlags.CF_NO_CONTACT_RESPONSE);
		this.interactZone.setCollisionShape(new btSphereShape(2f));
		this.interactListener = new ContactListenerAdapter() {
			@Override
			public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
				// do not interact if we aren't controlling a character
				// TODO if vehicle, fetch controlling character
				if (!(game.getControlledEntity() instanceof CharacterW)) {
					return;
				}
				CharacterW chara = (CharacterW) game.getControlledEntity();

				// if collided objects are interaction zone and player character
				if ((colObj0.getCPointer() == interactZone.getCPointer()
						&& colObj1.getCPointer() == chara.getGhostObject().getCPointer())
						|| (colObj1.getCPointer() == interactZone.getCPointer()
								&& colObj0.getCPointer() == chara.getGhostObject().getCPointer())) {
					if (chara.canPickUpArtifact()) {
						WandererConstants.ASSET_MANAGER.get("orig/feedback/artefactcollected.wav", Sound.class).play();
						chara.pickUpArtifact(artifact);
						game.removeEntity(ArtifactEntity.this);
						dispose();
					} else {
						WandererConstants.ASSET_MANAGER.get("orig/feedback/beltfull.wav", Sound.class).play();
						game.showMessage("Belt full!");
					}
				}
			}
		};
		this.game.getPhysicsManager().addContactListener(interactListener);

		this.scale(0.05f, 0.05f, 0.05f);
	}

	public ArtifactMeta getArtifact() {
		return artifact;
	}

	@Override
	public void enterBtWorld(btDiscreteDynamicsWorld world) {
		super.enterBtWorld(world);
		world.addCollisionObject(this.interactZone, CollisionFilterGroups.SensorTrigger,
				CollisionFilterGroups.CharacterFilter);
	}

	@Override
	public void leaveBtWorld(btDiscreteDynamicsWorld world) {
		super.leaveBtWorld(world);
		world.removeCollisionObject(this.interactZone);
	}

	@Override
	public void render(ModelBatch batch, Environment env) {
		super.render(batch, env);
		rotate(Vector3.Y, 3);
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		Matrix4 noScaleRotation = new Matrix4(getTransform().getTranslation(new Vector3()).add(0, 1, 0),
				new Quaternion(), new Vector3(1, 1, 1));
		this.interactZone.setWorldTransform(noScaleRotation);
	}

	@Override
	public void dispose() {
		this.game.getPhysicsManager().removeContactListener(interactListener);
		this.game.getBtWorld().removeCollisionObject(this.interactZone);
		// hack to dispose of the zone next tick as it is still referenced in the
		// current one
		Gdx.app.postRunnable(() -> this.interactZone.dispose());
		super.dispose();
	}
}
