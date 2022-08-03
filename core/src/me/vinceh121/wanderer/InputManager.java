package me.vinceh121.wanderer;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerAdapter;
import com.badlogic.gdx.controllers.ControllerMapping;
import com.badlogic.gdx.controllers.Controllers;

import me.vinceh121.wanderer.input.Binding;
import me.vinceh121.wanderer.input.Binding.DeviceType;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.MouseWheelScroll;

public class InputManager extends ApplicationAdapter {
	private final HashSetValuedHashMap<Input, Binding> bindings = new HashSetValuedHashMap<>();
	private final Vector<InputListener> listeners = new Vector<>();
	private boolean controllersReady;

	public void setDefaultBinds() {
		this.bindings.clear();

		this.bindings.put(Input.WALK_RIGHT, new Binding(Keys.RIGHT, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_LEFT, new Binding(Keys.LEFT, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_FORWARDS, new Binding(Keys.UP, DeviceType.KEYBOARD));
		this.bindings.put(Input.WALK_BACKWARDS, new Binding(Keys.BACK, DeviceType.KEYBOARD));
		this.bindings.put(Input.JUMP, new Binding(Keys.SPACE, DeviceType.KEYBOARD));

		this.bindings.put(Input.SCROLL_BELT_LEFT, new Binding(Keys.LEFT, DeviceType.KEYBOARD));
		this.bindings.put(Input.SCROLL_BELT_LEFT, new Binding(MouseWheelScroll.UP.ordinal(), DeviceType.MOUSE_WHEEL));
		this.bindings.put(Input.SCROLL_BELT_LEFT, new Binding(MouseWheelScroll.LEFT.ordinal(), DeviceType.MOUSE_WHEEL));
		this.bindings.put(Input.SCROLL_BELT_RIGHT, new Binding(Keys.RIGHT, DeviceType.KEYBOARD));
		this.bindings.put(Input.SCROLL_BELT_RIGHT,
				new Binding(MouseWheelScroll.DOWN.ordinal(), DeviceType.MOUSE_WHEEL));
		this.bindings.put(Input.SCROLL_BELT_RIGHT,
				new Binding(MouseWheelScroll.RIGHT.ordinal(), DeviceType.MOUSE_WHEEL));

		this.bindings.put(Input.OPEN_BELT, new Binding(Keys.ENTER, DeviceType.KEYBOARD));

		this.bindings.put(Input.PAUSE_MENU, new Binding(Keys.ESCAPE, DeviceType.KEYBOARD));
		this.bindings.put(Input.PAUSE_MENU, new Binding(Keys.F1, DeviceType.KEYBOARD));

		this.bindings.put(Input.UI_VALIDATE, new Binding(Keys.ENTER, DeviceType.KEYBOARD));

		this.bindings.put(Input.DEBUG_BULLET, new Binding(Keys.F7, DeviceType.KEYBOARD));
		this.bindings.put(Input.DEBUG_GLX, new Binding(Keys.F3, DeviceType.KEYBOARD));
		this.bindings.put(Input.SWITCH_CONTROLLED_VEHICLE, new Binding(Keys.TAB, DeviceType.KEYBOARD));
	}

	@Override
	public void create() {
		setDefaultBinds();
		new Thread(this::controllersInit, "ControllersInit").start();

		Gdx.input.setInputProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				Binding b = new Binding(keycode, DeviceType.KEYBOARD);
				Collection<Input> in = inputsForBinding(b);
				if (!in.isEmpty()) {
					fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean keyUp(int keycode) {
				Binding b = new Binding(keycode, DeviceType.KEYBOARD);
				Collection<Input> in = inputsForBinding(b);
				if (!in.isEmpty()) {
					fireInputUp(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) {
				Binding b = new Binding(button, DeviceType.MOUSE);
				Collection<Input> in = inputsForBinding(b);
				if (!in.isEmpty()) {
					fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) {
				Binding b = new Binding(button, DeviceType.MOUSE);
				Collection<Input> in = inputsForBinding(b);
				if (!in.isEmpty()) {
					fireInputUp(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean scrolled(float amountX, float amountY) {
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
					Gdx.app.log(getClass().getName(), "Couldn't figure out a mouse scroll value");
					return false;
				}

				Collection<Input> in = inputsForBinding(new Binding(scroll.ordinal(), DeviceType.MOUSE_WHEEL));
				if (!in.isEmpty()) {
					fireInputDown(in);
					return true;
				}
				return false;
			}
		});
	}

	private void controllersInit() {
		// to call Controllers#initialize
		Controllers.getControllers();

		Controllers.addListener(new ControllerAdapter() {
			@Override
			public boolean buttonDown(Controller controller, int buttonIndex) {
				Binding b = new Binding(buttonIndex, DeviceType.CONTROLLER);
				Collection<Input> in = inputsForBinding(b);
				if (in != null) {
					fireInputDown(in);
					return true;
				}
				return false;
			}

			@Override
			public boolean buttonUp(Controller controller, int buttonIndex) {
				Binding b = new Binding(buttonIndex, DeviceType.CONTROLLER);
				Collection<Input> in = inputsForBinding(b);
				if (in != null) {
					fireInputUp(in);
					return true;
				}
				return false;
			}

			@Override
			public void connected(Controller controller) {
				System.out.println(controller.getName() + " connected");
			}

			@Override
			public void disconnected(Controller controller) {
				System.out.println(controller.getName() + " disconnected");
			}
		});

		this.controllersReady = true;
	}

	public Collection<Input> inputsForBinding(Binding b) {
		final Vector<Input> vec = new Vector<>();
		for (Entry<Input, Binding> e : this.bindings.entries()) {
			if (b.equals(e.getValue())) {
				vec.add(e.getKey());
			}
		}
		return vec;
	}

	public Input inputForBinding(Binding b) {
		for (Entry<Input, Binding> e : this.bindings.entries()) {
			if (b.equals(e.getValue())) {
				return e.getKey();
			}
		}
		return null;
	}

	public boolean isPressed(final Input in) {
		final Set<Binding> binds = bindings.get(in);
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
				for (Controller c : Controllers.getControllers()) {
					if (c.getButton(b.getKey())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean fireInputDown(Collection<Input> c) {
		for (Input i : c) {
			if (this.fireInputDown(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fireInputUp(Collection<Input> c) {
		for (Input i : c) {
			if (this.fireInputUp(i)) {
				return true;
			}
		}
		return false;
	}

	private boolean fireInputDown(Input i) {
		for (InputListener l : this.listeners) {
			if (l.inputDown(i))
				return true;
		}
		return false;
	}

	private boolean fireInputUp(Input i) {
		for (InputListener l : this.listeners) {
			if (l.inputUp(i))
				return true;
		}
		return false;
	}

	public void addListener(InputListener listener) {
		this.listeners.add(listener);
	}

	public void removeListener(InputListener listener) {
		this.listeners.remove(listener);
	}

	public void removeListener(int index) {
		this.listeners.remove(index);
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
