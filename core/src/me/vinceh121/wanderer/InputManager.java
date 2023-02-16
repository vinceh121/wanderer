package me.vinceh121.wanderer;

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import me.vinceh121.wanderer.input.Binding;
import me.vinceh121.wanderer.input.Binding.DeviceType;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.MouseWheelScroll;

public class InputManager extends ApplicationAdapter {
	private static final String PREF_NAME = "me.vinceh121.wanderer.inputs";
	private final HashSetValuedHashMap<Input, Binding> bindings = new HashSetValuedHashMap<>();
	private final PriorityQueue<InputListener> listeners = new PriorityQueue<>(
			(o1, o2) -> Integer.compare(o2.getPriority(), o1.getPriority())); // reverse sort order
	private boolean controllersReady;

	public void loadOrDefaults() throws JsonProcessingException {
		this.load();
		if (this.bindings.size() == 0) {
			this.setDefaultBinds();
			this.save();
		}
	}

	public void load() throws JsonProcessingException {
		this.bindings.clear();
		final Preferences pref = Gdx.app.getPreferences(InputManager.PREF_NAME);
		for (final Input i : Input.values()) {
			if (pref.contains(i.toString())) {
				this.bindings.putAll(i,
						WandererConstants.MAPPER.readValue(pref.getString(i.toString()),
								new TypeReference<List<Binding>>() {
								}));
			}
		}
	}

	public void save() throws JsonProcessingException {
		final Preferences pref = Gdx.app.getPreferences(InputManager.PREF_NAME);
		for (final Input i : this.bindings.keySet()) {
			pref.putString(i.toString(), WandererConstants.MAPPER.writeValueAsString(this.bindings.get(i)));
		}
		pref.flush();
	}

	public void setDefaultBinds() {
		this.bindings.clear();

		this.bindings.put(Input.WALK_RIGHT, new Binding(Keys.RIGHT, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_RIGHT, new Binding(Keys.D, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_LEFT, new Binding(Keys.LEFT, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_LEFT, new Binding(Keys.A, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_FORWARDS, new Binding(Keys.UP, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_FORWARDS, new Binding(Keys.W, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_BACKWARDS, new Binding(Keys.DOWN, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_BACKWARDS, new Binding(Keys.S, DeviceType.KEYBOARD));
		this.bindings.put(Input.JUMP, new Binding(Keys.SPACE, DeviceType.KEYBOARD));
		this.bindings.put(Input.JUMP, new Binding(Buttons.RIGHT, DeviceType.MOUSE));

		this.bindings.put(Input.SCROLL_BELT_LEFT, new Binding(Keys.LEFT, DeviceType.KEYBOARD));
		this.bindings.put(Input.SCROLL_BELT_LEFT, new Binding(MouseWheelScroll.UP.ordinal(), DeviceType.MOUSE_WHEEL));
		this.bindings.put(Input.SCROLL_BELT_LEFT, new Binding(MouseWheelScroll.LEFT.ordinal(), DeviceType.MOUSE_WHEEL));
		this.bindings.put(Input.SCROLL_BELT_RIGHT, new Binding(Keys.RIGHT, DeviceType.KEYBOARD));
		this.bindings.put(Input.SCROLL_BELT_RIGHT,
				new Binding(MouseWheelScroll.DOWN.ordinal(), DeviceType.MOUSE_WHEEL));
		this.bindings.put(Input.SCROLL_BELT_RIGHT,
				new Binding(MouseWheelScroll.RIGHT.ordinal(), DeviceType.MOUSE_WHEEL));

		this.bindings.put(Input.OPEN_BELT, new Binding(Keys.ENTER, DeviceType.KEYBOARD));
		this.bindings.put(Input.OPEN_BELT, new Binding(Buttons.LEFT, DeviceType.MOUSE));

		this.bindings.put(Input.PAUSE_MENU, new Binding(Keys.ESCAPE, DeviceType.KEYBOARD));
		this.bindings.put(Input.PAUSE_MENU, new Binding(Keys.F1, DeviceType.KEYBOARD));

		this.bindings.put(Input.UI_VALIDATE, new Binding(Keys.ENTER, DeviceType.KEYBOARD));

		this.bindings.put(Input.DEBUG_BULLET, new Binding(Keys.F7, DeviceType.KEYBOARD));
		this.bindings.put(Input.DEBUG_GLX, new Binding(Keys.F3, DeviceType.KEYBOARD));
		this.bindings.put(Input.DEBUG_TIMESCALE, new Binding(Keys.F2, DeviceType.KEYBOARD));
		this.bindings.put(Input.CURSOR_CAPTURE, new Binding(Keys.F4, DeviceType.KEYBOARD));

		this.bindings.put(Input.SWITCH_CONTROLLED_VEHICLE, new Binding(Keys.TAB, DeviceType.KEYBOARD));

		this.bindings.put(Input.QUICK_SAVE, new Binding(Keys.F5, DeviceType.KEYBOARD));
		this.bindings.put(Input.QUICK_LOAD, new Binding(Keys.F8, DeviceType.KEYBOARD));
	}

	@Override
	public void create() {
		new Thread(this::controllersInit, "ControllersInit").start();

		Gdx.input.setCursorCatched(true);

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(final int keycode) {
				final Binding b = new Binding(keycode, DeviceType.KEYBOARD);
				final Collection<Input> in = InputManager.this.inputsForBinding(b);
				if (!in.isEmpty()) {
					InputManager.this.fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean keyUp(final int keycode) {
				final Binding b = new Binding(keycode, DeviceType.KEYBOARD);
				final Collection<Input> in = InputManager.this.inputsForBinding(b);
				if (!in.isEmpty()) {
					InputManager.this.fireInputUp(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean touchDown(final int screenX, final int screenY, final int pointer, final int button) {
				final Binding b = new Binding(button, DeviceType.MOUSE);
				final Collection<Input> in = InputManager.this.inputsForBinding(b);
				if (!in.isEmpty()) {
					InputManager.this.fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean touchUp(final int screenX, final int screenY, final int pointer, final int button) {
				final Binding b = new Binding(button, DeviceType.MOUSE);
				final Collection<Input> in = InputManager.this.inputsForBinding(b);
				if (!in.isEmpty()) {
					InputManager.this.fireInputUp(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean scrolled(final float amountX, final float amountY) {
				final MouseWheelScroll scroll;
				if (amountY > 0) {
					scroll = MouseWheelScroll.DOWN;
				} else if (amountY < 0) {
					scroll = MouseWheelScroll.UP;
				} else if (amountX > 0) {
					scroll = MouseWheelScroll.RIGHT;
				} else if (amountX < 0) {
					scroll = MouseWheelScroll.LEFT;
				} else {
					Gdx.app.log(this.getClass().getName(), "Couldn't figure out a mouse scroll value");
					return false;
				}

				final Collection<Input> in = InputManager.this
					.inputsForBinding(new Binding(scroll.ordinal(), DeviceType.MOUSE_WHEEL));
				if (!in.isEmpty()) {
					InputManager.this.fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean mouseMoved(final int screenX, final int screenY) {
				final int centerX = Gdx.graphics.getWidth() / 2;
				final int centerY = Gdx.graphics.getHeight() / 2;

				if (Gdx.input.isCursorCatched()) {
					Gdx.input.setCursorPosition(centerX, centerY);
					final boolean b = InputManager.this.fireMouseMoved(screenX - centerX, screenY - centerY);
					return b;
				} else {
					return false;
				}
			}
		});
	}

	private void controllersInit() {
		// to call Controllers#initialize
		Controllers.getControllers();

		Controllers.addListener(new ControllerAdapter() {
			@Override
			public boolean buttonDown(final Controller controller, final int buttonIndex) {
				final Binding b = new Binding(buttonIndex, DeviceType.CONTROLLER);
				final Collection<Input> in = InputManager.this.inputsForBinding(b);
				if (in != null) {
					InputManager.this.fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean buttonUp(final Controller controller, final int buttonIndex) {
				final Binding b = new Binding(buttonIndex, DeviceType.CONTROLLER);
				final Collection<Input> in = InputManager.this.inputsForBinding(b);
				if (in != null) {
					InputManager.this.fireInputUp(in);
					return true;
				}
				return false;
			}

			@Override
			public void connected(final Controller controller) {
				System.out.println(controller.getName() + " connected");
			}

			@Override
			public void disconnected(final Controller controller) {
				System.out.println(controller.getName() + " disconnected");
			}
		});

		this.controllersReady = true;
	}

	public Collection<Input> inputsForBinding(final Binding b) {
		final Vector<Input> vec = new Vector<>();
		for (final Entry<Input, Binding> e : this.bindings.entries()) {
			if (b.equals(e.getValue())) {
				vec.add(e.getKey());
			}
		}
		return vec;
	}

	public Input inputForBinding(final Binding b) {
		for (final Entry<Input, Binding> e : this.bindings.entries()) {
			if (b.equals(e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}

	public boolean isPressed(final Input in) {
		final Set<Binding> binds = this.bindings.get(in);
		if (binds.isEmpty()) {
			return false;
		}
		for (final Binding b : binds) {
			if (b.getDeviceType() == DeviceType.KEYBOARD) {
				if (Gdx.input.isKeyPressed(b.getKey())) {
					return true;
				}
			} else if (b.getDeviceType() == DeviceType.MOUSE) {
				if (Gdx.input.isButtonPressed(b.getKey())) {
					return true;
				}
			} else if (b.getDeviceType() == DeviceType.CONTROLLER && this.controllersReady) {
				for (final Controller c : Controllers.getControllers()) {
					if (c.getButton(b.getKey())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean fireInputDown(final Collection<Input> c) {
		for (final Input i : c) {
			if (this.fireInputDown(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fireInputUp(final Collection<Input> c) {
		for (final Input i : c) {
			if (this.fireInputUp(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fireInputDown(final Input i) {
		for (final InputListener l : this.listeners) {
			if (l.inputDown(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fireInputUp(final Input i) {
		for (final InputListener l : this.listeners) {
			if (l.inputUp(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fireMouseMoved(final int x, final int y) {
		for (final InputListener l : this.listeners) {
			if (l.mouseMoved(x, y)) {
				return true;
			}
		}
		return false;
	}

	public void addListener(final InputListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(final InputListener listener) {
		this.listeners.remove(listener);
	}

	public static String nameForControllerButton(final ControllerMapping m, final int b) {
		if (m.axisLeftX == b) {
			return "Left X";
		} else if (m.axisLeftY == b) {
			return "Left Y";
		} else if (m.axisRightX == b) {
			return "Right X";
		} else if (m.axisRightY == b) {
			return "Right Y";
		} else if (m.buttonA == b) {
			return "A";
		} else if (m.buttonB == b) {
			return "B";
		} else if (m.buttonX == b) {
			return "X";
		} else if (m.buttonY == b) {
			return "Y";
		} else if (m.buttonBack == b) {
			return "Back";
		} else if (m.buttonStart == b) {
			return "Start";
		} else if (m.buttonL1 == b) {
			return "L1";
		} else if (m.buttonL2 == b) {
			return "L2";
		} else if (m.buttonR1 == b) {
			return "R1";
		} else if (m.buttonR2 == b) {
			return "R2";
		} else if (m.buttonDpadUp == b) {
			return "Dpad UP";
		} else if (m.buttonDpadDown == b) {
			return "Dpad DOWN";
		} else if (m.buttonDpadLeft == b) {
			return "Dpad LEFT";
		} else if (m.buttonDpadRight == b) {
			return "Dpad RIGHT";
		} else if (m.buttonLeftStick == b) {
			return "Left Stick";
		} else if (m.buttonRightStick == b) {
			return "Right Stick";
		}
		return null;
	}
}
