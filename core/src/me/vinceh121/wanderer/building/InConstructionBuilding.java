package me.vinceh121.wanderer.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.entity.DisplayModel;

public class InConstructionBuilding extends AbstractBuilding {
	private final AbstractBuildingMeta meta;
	private final Array<Bezier<Vector3>> curves = new Array<>();
	private boolean constructionDone;
	private Sound sound;
	private long soundId;
	private float aliveTime;

	public InConstructionBuilding(Wanderer game, AbstractBuildingMeta meta) {
		super(game, meta);
		this.meta = meta;

		for (final DisplayModel m : this.getModels()) {
			m.addTextureAttribute(ColorAttribute.createEmissive(new Color(0f, 0.8f, 1f, 0f)));
			m.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_DST_COLOR, 0.5f));

			// FIXME use fixed curves
			final Array<Vector3> points = new Array<>();
			points.add(new Vector3(MathUtils.random(-20, 20), MathUtils.random(-5, 5), MathUtils.random(-20, 20)));
			points.add(points.first().cpy().scl(0.5f).add(0, 10, 0));
			points.add(new Vector3());
			this.curves.add(new Bezier<>(points, 0, points.size));
		}
	}

	@Override
	protected void onInteractContact(btCollisionObject colObj0, btCollisionObject colObj1) {
		// disable interactivity
	}

	@Override
	public void render(ModelBatch batch, Environment env) {
		super.render(batch, env);

		for (int i = 0; i < this.curves.size; i++) {
			Bezier<Vector3> bezier = this.curves.get(i);
			DisplayModel model = this.getModels().get(i);

			Vector3 v = bezier.valueAt(new Vector3(), (this.aliveTime / 5) % 1);
			v.add(model.getRelativeTransform().getTranslation(new Vector3()));
			v.add(this.getTransform().getTranslation(new Vector3()));
			model.getAbsoluteTransform().setTranslation(v);
		}

		this.aliveTime += Gdx.graphics.getDeltaTime();
		if (this.aliveTime > this.meta.getBuildTime()) {
			this.build();
		}
	}

	public void build() {
		if (this.isConstructionDone()) {
			return;
		}
		this.constructionDone = true;
		if (this.getIsland() != null) {
			this.getIsland().removeBuilding(this);
			AbstractBuilding newBuilding = this.meta.createBuilding(this.game);
			this.game.addEntity(newBuilding);
			this.getIsland().addBuilding(newBuilding, getSlot());
		}

		this.sound.stop(soundId);
		this.game.removeEntity(this);
		this.dispose();
	}

	public boolean isConstructionDone() {
		return constructionDone;
	}

	@Override
	public void enterBtWorld(btDiscreteDynamicsWorld world) {
		super.enterBtWorld(world);
		this.sound = WandererConstants.ASSET_MANAGER.get("orig/lib/sound/healingspell.wav", Sound.class);
		this.soundId = this.sound.play();
		this.sound.setLooping(soundId, true);
	}

	@Override
	public void dispose() {
		this.sound.stop(this.soundId);
		super.dispose();
	}
}
