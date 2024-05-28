package me.vinceh121.wanderer.building;

import java.util.List;
import java.util.stream.Collectors;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.NavigationWaypoint;
import me.vinceh121.wanderer.i18n.I18N;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;

public class NavigationTower extends AbstractControllableBuilding {
	private final NavigationTowerPrototype prototype;
	private NavigationWaypoint currentWaypoint;

	public NavigationTower(final Wanderer game, final NavigationTowerPrototype prototype) {
		super(game, prototype);
		this.prototype = prototype;

		this.setControlMessage(/* Popup when getting close to navtower */ I18N.gettext("Navigation Tower"));
	}

	public List<NavigationWaypoint> getAvailableWaypoints() {
		final Vector3 islandTrans = this.getIsland().getTranslation();

		return this.game.findEntitiesByClass(NavigationWaypoint.class)
			.filter(w -> w.isEnabled() && w.getTranslation().dst(islandTrans) > 2)
			.sorted((w1, w2) -> Double.compare(w1.getTranslation().dst(islandTrans),
					w2.getTranslation().dst(islandTrans)))
			.collect(Collectors.toUnmodifiableList());
	}

	private void nextWaypoint(int offset) {
		final List<NavigationWaypoint> waypoints = this.getAvailableWaypoints();

		if (waypoints.isEmpty()) {
			return;
		}

		int next = this.currentWaypoint == null ? 0 : (waypoints.indexOf(this.currentWaypoint) + offset) % waypoints.size();

		if (next < 0) {
			next = waypoints.size() - next;
		}

		this.currentWaypoint = waypoints.get(next);
	}

	private void start() {
		if (this.currentWaypoint != null) {
			this.getIsland().getAiController().setCurrentTask(new Island.NavigationGoto(currentWaypoint));
		} else {
			this.game.showMessage(I18N.gettext("No waypoint available"));
		}
	}

	@Override
	public void tick(float delta) {
		super.tick(delta);

		if (this.isControlled()) {
			this.moveCamera();
		}
	}

	private void moveCamera() {
		final PerspectiveCamera cam = this.game.getCamera();
		final float offY = this.prototype.getCameraOffset().y;
		final float offZ = this.prototype.getCameraOffset().z;

		final Vector3 direction;

		if (this.currentWaypoint == null) {
			direction = new Vector3(1, 0, 0);
		} else {
			direction = this.currentWaypoint.getTranslation().sub(this.getTranslation());
		}

		final Vector3 position = cam.position;
		position.set(direction);
		position.scl(offZ);
		position.add(0, offY, 0);
		position.add(this.getTranslation());

		cam.direction.set(direction);

		cam.update();
	}

	@Override
	public void onTakeControl() {
		super.onTakeControl();

		if (this.currentWaypoint != null) {
			this.nextWaypoint(1);
		}
	}

	@Override
	public InputListener createInputProcessor() {
		return new InputListenerAdapter(0) {
			@Override
			public boolean inputDown(Input in) {
				if (in == Input.SCROLL_BELT_RIGHT) {
					nextWaypoint(1);
				} else if (in == Input.SCROLL_BELT_LEFT) {
					nextWaypoint(-1);
				} else if (in == Input.FIRE) {
					start();
				}

				return false;
			}
		};
	}
}
