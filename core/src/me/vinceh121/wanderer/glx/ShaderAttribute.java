package me.vinceh121.wanderer.glx;

import java.util.Objects;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class ShaderAttribute extends Attribute {
	public static final String ALIAS_SHADER = "shader";
	public static final long TYPE_SHADER = register(ALIAS_SHADER);
	private final IShaderBuilder shaderBuilder;

	public ShaderAttribute(IShaderBuilder shaderBuilder) {
		super(TYPE_SHADER);
		Objects.requireNonNull(shaderBuilder);
		this.shaderBuilder = shaderBuilder;
	}

	@Override
	public int compareTo(Attribute o) {
		throw new UnsupportedOperationException("ShaderAttribute#compareTo(o) is not implemented");
	}

	@Override
	public Attribute copy() {
		return new ShaderAttribute(this.shaderBuilder);
	}

	public IShaderBuilder getShaderBuilder() {
		return shaderBuilder;
	}
}
