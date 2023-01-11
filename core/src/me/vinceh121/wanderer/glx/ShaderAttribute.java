package me.vinceh121.wanderer.glx;

import java.util.Objects;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.utils.ShaderProvider;

public class ShaderAttribute extends Attribute {
	public static final String ALIAS_SHADER = "shader";
	public static final long TYPE_SHADER = register(ALIAS_SHADER);
	private final ShaderProvider shaderProvider;

	public ShaderAttribute(ShaderProvider shaderProvider) {
		super(TYPE_SHADER);
		Objects.requireNonNull(shaderProvider);
		this.shaderProvider = shaderProvider;
	}

	@Override
	public int compareTo(Attribute o) {
		throw new UnsupportedOperationException("ShaderAttribute#compareTo(o) is not implemented");
	}

	@Override
	public Attribute copy() {
		return new ShaderAttribute(this.shaderProvider);
	}

	public ShaderProvider getShaderProvider() {
		return shaderProvider;
	}
}
