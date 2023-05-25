package me.vinceh121.wanderer.guntower;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.building.AbstractControllableBuilding;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;

public abstract class AbstractGuntower extends AbstractControllableBuilding {
	private static final Logger LOG = LogManager.getLogger(AbstractGuntower.class);
	private final AbstractGuntowerMeta meta;
	protected SoundEmitter3D fireSoundEmitter;
	private float azimuth = 0.75f, polarAngle = 0.5f;

	public AbstractGuntower(Wanderer game, AbstractGuntowerMeta meta) {
		super(game, meta);
		this.meta = meta;
		this.setName("Gun tower");

		if (this.meta.getFireSound() != null) {
			if (!WandererConstants.ASSET_MANAGER.isLoaded(this.meta.getFireSound(), Sound3D.class)) {
				LOG.warn("Hot-loading fire sound {}", this.meta.getFireSound());
				WandererConstants.ASSET_MANAGER.load(this.meta.getFireSound(), Sound3D.class);
				WandererConstants.ASSET_MANAGER.finishLoadingAsset(this.meta.getFireSound());
			}
			this.fireSoundEmitter =
					WandererConstants.ASSET_MANAGER.get(this.meta.getFireSound(), Sound3D.class).playGeneral();
			this.addSoundEmitter(this.fireSoundEmitter);
		}
	}

	public abstract void fire();

	@Override
	public InputListener createInputProcessor() {
		return new InputListenerAdapter(50) {
			@Override
			public boolean mouseMoved(int x, int y) {
				final float lookSensX =
						Preferences.getPreferences().<Double>getOrElse("input.lookSensitivityX", 0.2).floatValue();
				azimuth += x * lookSensX * 0.01f;
				azimuth %= 1;

				final float lookSensY =
						Preferences.getPreferences().<Double>getOrElse("input.lookSensitivityY", 0.005).floatValue();
				polarAngle += y * lookSensY * -1f;
				polarAngle = MathUtils.clamp(polarAngle, meta.getPolarMin(), meta.getPolarMax());
				return true;
			}
		};
	}

	public Vector3 getLookDirection() {
		Vector3 direction = new Vector3();
		direction.setFromSpherical(MathUtils.PI2 * (azimuth % 1), MathUtils.PI * (polarAngle % 1));
		direction.rotateRad(Vector3.X, MathUtils.HALF_PI);
		return direction;
	}

	public Quaternion getLookRotation() {
		Quaternion rot = new Quaternion();
		rot.setFromCross(Vector3.Y, this.getLookDirection());
		return rot;
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		if (this.isControlled()) {
			this.moveCamera();
		}

		Vector3 dir = getLookDirection();
		Quaternion rot = new Quaternion();
		rot.setFromMatrix(new Matrix4().setToLookAt(dir, Vector3.Y));
		rot.conjugate();

		this.animateParts("setLookRot", t -> t.set(t.getTranslation(new Vector3()), rot));

		if (this.isControlled() && this.game.getInputManager().isPressed(Input.FIRE)) {
			fire();
		}
	}

	protected void moveCamera() {
		PerspectiveCamera cam = this.game.getCamera();
		float offY = this.meta.getCameraOffset().y;
		float offZ = this.meta.getCameraOffset().z;

		Vector3 direction = this.getLookDirection();

		Vector3 position = cam.position;
		position.set(direction);
		position.scl(offZ);
		position.add(0, offY, 0);
		position.add(this.getTranslation());

		cam.direction.set(direction);

		cam.update();
	}

	public float getAzimuth() {
		return azimuth;
	}

	public void setAzimuth(float azimuth) {
		this.azimuth = azimuth;
	}

	public float getPolarAngle() {
		return polarAngle;
	}

	public void setPolarAngle(float polarAngle) {
		this.polarAngle = polarAngle;
	}

	@Override
	public AbstractGuntowerMeta getMeta() {
		return meta;
	}
}
