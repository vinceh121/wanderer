package me.vinceh121.wanderer.animation;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.BaseAnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/**
 * An animation controller that simultaneously plays all animations such that
 * for each bone, [rot, trans] the animation with ID
 * "[characterState]_[rot|trans]_[bone]" is played
 */
public class MultiplexedSkinAnimationController extends BaseAnimationController {
	private static final String[] ANIM_TYPES = new String[] { "rot", "trans" };
	private final Array<String> boneNames = new Array<>(false, 16);
	private AnimationTrack current, previous;
	private boolean doTransitions = true;
	private float transitionTime = 0.1f;
	/**
	 * Time left for the smooth loop last frame to first frame transition
	 */
	private float smoothLoopTrans;
	/**
	 * Time left for the smooth transition between the current and previous
	 * animation
	 */
	private float previousTrans;

	public MultiplexedSkinAnimationController(final ModelInstance target) {
		super(target);
		for (final Node node : MultiplexedSkinAnimationController.flattenNodes(target.nodes)) {
			// bone nodes do not have NodeParts, only meshes do
			if (node.parts.size == 0) {
				this.boneNames.add(node.id);
			}
		}
	}

	public void update(float delta) {
		if (this.current == null) {
			return;
		}

		delta *= this.current.speed;

		// increment play time
		this.current.currentTime = MathUtils.clamp(this.current.currentTime + delta, 0, this.current.totalTime);

		if (this.current.totalTime == this.current.currentTime) {
			if (this.current.playbackType == PlaybackType.LOOP) {
				// should loop, reached the end of the track, reset cursor
				this.current.currentTime = 0;

			} else if (this.current.playbackType == PlaybackType.LOOP_SMOOTH) {
				// should loop, reached the end of the track, reset cursor
				this.current.currentTime = 0;
				this.smoothLoopTrans = this.transitionTime;

			} else if (this.current.playbackType == PlaybackType.NORMAL) {
				// shouldn't loop, reached the end of the track, stop playback
				// let's not set to null as to keep the proper behaviour of
				// #playAnimationOptional
				// this.current = null;
				return;
			} else if (this.current.playbackType == PlaybackType.BOOMERANG && this.current.speed > 0) {
				// should boomerang, reached end of track, reverse order
				this.current.speed *= -1;
			}
		}

		if (this.current.playbackType == PlaybackType.LOOP_SMOOTH && this.smoothLoopTrans != 0) {
			final float alpha = this.smoothLoopTrans / this.transitionTime;
			for (final Animation anim : this.current.animations) {
				this.applyAnimations(anim, this.current.currentTime, anim, this.current.totalTime, alpha);
			}
			this.smoothLoopTrans = Math.max(0, this.smoothLoopTrans - delta);
			return;
		}

		// should boomerang, reached start of track, reverse order
		if (this.current.playbackType == PlaybackType.BOOMERANG && this.current.currentTime == 0) {
			this.current.speed *= -1;
		}

		if (this.previous != null && this.previousTrans != 0) {
			final float alpha = this.previousTrans / this.transitionTime;

			for (int i = 0; i < Math.max(this.current.animations.size, this.current.animations.size); i++) {
				this.begin();
				if (i < this.current.animations.size) {
					this.apply(this.current.animations.get(i), this.current.currentTime, 1);
				}
				if (i < this.previous.animations.size) {
					this.apply(this.previous.animations.get(i), this.previous.currentTime, alpha);
				}
				this.end();
			}
			this.previousTrans = Math.max(0, this.previousTrans - delta);
			return;
		}

		for (final Animation anim : this.current.animations) {
			this.applyAnimation(anim, this.current.currentTime);
		}
	}

	public void playAnimationOptional(final String state, final PlaybackType playbackType, final float speed) {
		if (this.current == null || !this.current.name.equals(state)) {
			this.playAnimation(state, playbackType, speed);
		}
	}

	public void playAnimation(final String state, final PlaybackType playbackType, final float speed) {
		final Array<Animation> anims = new Array<>(this.boneNames.size);

		for (final String bone : this.boneNames) {
			for (final String type : MultiplexedSkinAnimationController.ANIM_TYPES) {
				// FIXME PERF ISSUE getAnimation() is an iterative search
				final Animation a = this.target.getAnimation(state + "_" + type + "_" + bone);
				if (a != null) {
					anims.add(a);
				}
			}
		}

		if (anims.size == 0) {
			throw new IllegalStateException("No animation track & bone for state `" + state + "'");
		}

		if (this.doTransitions) {
			this.previous = this.current;
			this.previousTrans = this.transitionTime;
		}

		this.current = new AnimationTrack(state, anims);
		this.current.playbackType = playbackType;
		this.current.speed = speed;
	}

	public AnimationTrack getCurrent() {
		return this.current;
	}

	public AnimationTrack getPrevious() {
		return this.previous;
	}

	public boolean isDoTransitions() {
		return this.doTransitions;
	}

	public void setDoTransitions(final boolean doTransitions) {
		this.doTransitions = doTransitions;
	}

	public float getTransitionTime() {
		return this.transitionTime;
	}

	public void setTransitionTime(final float transitionTime) {
		this.transitionTime = transitionTime;
	}

	public static Array<Node> flattenNodes(final Iterable<Node> nodes) {
		final Array<Node> list = new Array<>();
		for (final Node n : nodes) {
			list.add(n);
			list.addAll(MultiplexedSkinAnimationController.flattenNodes(n.getChildren()));
		}
		return list;
	}

	public enum PlaybackType {
		NORMAL, LOOP, LOOP_SMOOTH, BOOMERANG;
	}

	public static class AnimationTrack {
		private final String name;
		private final Array<Animation> animations;
		private float totalTime, currentTime, speed = 1f;
		private PlaybackType playbackType;

		public AnimationTrack(final String name, final Array<Animation> animations) {
			this.name = name;
			this.animations = animations;

			for (final Animation a : animations) {
				this.totalTime = Math.max(a.duration, this.totalTime);
			}
		}

		public String getName() {
			return this.name;
		}

		public float getTotalTime() {
			return this.totalTime;
		}

		public void setTotalTime(final float totalTime) {
			this.totalTime = totalTime;
		}

		public float getCurrentTime() {
			return this.currentTime;
		}

		public void setCurrentTime(final float current) {
			this.currentTime = current;
		}

		public float getSpeed() {
			return this.speed;
		}

		public void setSpeed(final float speed) {
			this.speed = speed;
		}

		public PlaybackType getPlaybackType() {
			return this.playbackType;
		}

		public void setPlaybackType(final PlaybackType playbackType) {
			this.playbackType = playbackType;
		}

		public Array<Animation> getAnimations() {
			return this.animations;
		}
	}
}
