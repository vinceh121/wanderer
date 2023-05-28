package me.vinceh121.wanderer.launcher;

public class DataNpkSum {
	private boolean valid, demo;

	public boolean isValid() {
		return this.valid;
	}

	public void setValid(final boolean valid) {
		this.valid = valid;
	}

	public boolean isDemo() {
		return this.demo;
	}

	public void setDemo(final boolean demo) {
		this.demo = demo;
	}

	@Override
	public String toString() {
		return "DataNpkSum [valid=" + this.valid + ", demo=" + this.demo + "]";
	}
}
