package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader;

public class WandererParticleShader extends ParticleShader {
	private static String defaultVertexShader = null;
	private static String defaultFragmentShader = null;
	public final static Uniform alphaTest = new Uniform("u_alphaTest");

	public final int u_alphaTest;

	public WandererParticleShader(Renderable renderable) {
		this(renderable, new Config(getDefaultVertexShader(), getDefaultFragmentShader()));
	}

	public WandererParticleShader(Renderable renderable, Config config) {
		// this really is an annoying way to do this, but I guess it's better to follow
		// the scheme they've already put in place
		super(renderable, config, createPrefix(renderable, config));

		u_alphaTest = register(alphaTest);

		this.setDefaultCullFace(0);
	}

	@Override
	protected void bindMaterial(Renderable renderable) {
		super.bindMaterial(renderable);
		for (Attribute att : renderable.material) {
			if ((att.type & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest) {
				set(u_alphaTest, ((FloatAttribute) att).value);
			}
		}
	}

	public static String createPrefix(final Renderable renderable, final Config config) {
		String prefix = ParticleShader.createPrefix(renderable, config);
		final long attributesMask = renderable.material.getMask();
		if ((attributesMask & BlendingAttribute.Type) == BlendingAttribute.Type)
			prefix += "#define " + BlendingAttribute.Alias + "Flag\n";
		if ((attributesMask & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
			prefix += "#define " + FloatAttribute.AlphaTestAlias + "Flag\n";
		return prefix;
	}

	public static String getDefaultVertexShader() {
		if (defaultVertexShader == null)
			defaultVertexShader = Gdx.files.internal("shaders/particles.vert").readString();
		return defaultVertexShader;
	}

	public static String getDefaultFragmentShader() {
		if (defaultFragmentShader == null)
			defaultFragmentShader = Gdx.files.internal("shaders/particles.frag").readString();
		return defaultFragmentShader;
	}

}
