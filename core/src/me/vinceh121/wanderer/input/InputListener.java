package me.vinceh121.wanderer.input;

public interface InputListener {
	boolean inputDown(Input in);

	boolean inputUp(Input in);

	boolean mouseMoved(int x, int y);

	int getPriority();
}
