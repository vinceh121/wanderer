package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class WandererShader extends DefaultShader {
	public static final Uniform tiledMaterialUniform = new Uniform("u_tiledMaterialTexture");
	public static final Setter tiledMaterialTextureSetter = new LocalSetter() {
		@Override
		public void set(final BaseShader shader, final int inputID, final Renderable renderable,
				final Attributes combinedAttributes) {
			final int unit = shader.context.textureBinder
				.bind(((TiledMaterialAttribute) combinedAttributes.get(TiledMaterialAttribute.TiledMaterial))
					.getTextureDescriptor());
			shader.set(inputID, unit);
		}
	};
	public static final Uniform tiledMaterialOpacityUniform = new Uniform("u_tiledMaterialOpacity");
	public static final Uniform tiledMaterialRatioUniform = new Uniform("u_tiledMaterialRatio");

	public final int u_tiledMaterialTexture, u_tiledMaterialOpacity, u_tiledMaterialRatio;

	public WandererShader(final Renderable renderable, final Config config) {
		super(renderable, config, WandererShader.createPrefix(renderable, config));

		this.u_tiledMaterialTexture = this.register(WandererShader.tiledMaterialUniform,
				WandererShader.tiledMaterialTextureSetter);
		this.u_tiledMaterialOpacity = this.register(WandererShader.tiledMaterialOpacityUniform);
		this.u_tiledMaterialRatio = this.register(WandererShader.tiledMaterialRatioUniform);
	}

	@Override
	protected void bindMaterial(final Attributes attributes) {
		// start inline super.bindMaterial(attributes);
		int cullFace = config.defaultCullFace == -1 ? defaultCullFace : config.defaultCullFace;
		int depthFunc = config.defaultDepthFunc == -1 ? defaultDepthFunc : config.defaultDepthFunc;
		float depthRangeNear = 0f;
		float depthRangeFar = 1f;
		boolean depthMask = true;

		for (final Attribute attr : attributes) {
			final long t = attr.type; // BAD BEHAVIOUR
				// In the following, libgdx seems to consider t is a bitfield for the attributes array, and not a single type
			if (BlendingAttribute.is(t)) {
				context.setBlending(true,
						((BlendingAttribute) attr).sourceFunction,
						((BlendingAttribute) attr).destFunction);
				set(u_opacity, ((BlendingAttribute) attr).opacity);
			} else if ((t & IntAttribute.CullFace) == IntAttribute.CullFace)
				cullFace = ((IntAttribute) attr).value;
			else if ((t & FloatAttribute.AlphaTest) == FloatAttribute.AlphaTest)
				set(u_alphaTest, ((FloatAttribute) attr).value);
			else if ((t & DepthTestAttribute.Type) == DepthTestAttribute.Type) {
				DepthTestAttribute dta = (DepthTestAttribute) attr;
				depthFunc = dta.depthFunc;
				depthRangeNear = dta.depthRangeNear;
				depthRangeFar = dta.depthRangeFar;
				depthMask = dta.depthMask;
			} else if (!config.ignoreUnimplemented)
				throw new GdxRuntimeException("Unknown material attribute: " + attr.toString());
		}

		context.setCullFace(cullFace);
		context.setDepthTest(depthFunc, depthRangeNear, depthRangeFar);
		context.setDepthMask(depthMask);
		// end inline super.bindMaterial
		for (final Attribute a : attributes) {
			if (a.type == TiledMaterialAttribute.TiledMaterial) {
				this.set(this.u_tiledMaterialOpacity, ((TiledMaterialAttribute) a).getOpacity());
				this.set(this.u_tiledMaterialRatio, ((TiledMaterialAttribute) a).getRatio());
				break;
			}
		}
	}

	public static String createPrefix(final Renderable renderable, final Config config) {
		String prefix = "#version 330\n" + DefaultShader.createPrefix(renderable, config); // sigh should've been local
		for (final Attribute a : renderable.material) {
			if (a.type == TiledMaterialAttribute.TiledMaterial) {
				prefix += "#define tiledMaterialFlag\n";
			} else if (a.type == NoLightningAttribute.NO_LIGHTNING) {
				prefix += "#define noLightningFlag\n";
			}
		}
		return prefix;
	}

}
