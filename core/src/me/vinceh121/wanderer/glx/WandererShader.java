package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Attribute;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;

public class WandererShader extends DefaultShader {
	public static final Uniform tiledMaterialUniform = new Uniform("u_tiledMaterialTexture");
	public static final Setter tiledMaterialTextureSetter = new LocalSetter() {
		@Override
		public void set(BaseShader shader, int inputID, Renderable renderable, Attributes combinedAttributes) {
			final int unit = shader.context.textureBinder
					.bind(((TiledMaterialAttribute) (combinedAttributes.get(TiledMaterialAttribute.TiledMaterial)))
							.getTextureDescriptor());
			shader.set(inputID, unit);
		}
	};
	public static final Uniform tiledMaterialOpacityUniform = new Uniform("u_tiledMaterialOpacity");
	public static final Uniform tiledMaterialRatioUniform = new Uniform("u_tiledMaterialRatio");

	public final int u_tiledMaterialTexture, u_tiledMaterialOpacity, u_tiledMaterialRatio;

	public WandererShader(Renderable renderable, Config config) {
		super(renderable, config, WandererShader.createPrefix(renderable, config));

		this.u_tiledMaterialTexture = register(tiledMaterialUniform, tiledMaterialTextureSetter);
		this.u_tiledMaterialOpacity = register(tiledMaterialOpacityUniform);
		this.u_tiledMaterialRatio = register(tiledMaterialRatioUniform);
	}

	@Override
	protected void bindMaterial(Attributes attributes) {
		super.bindMaterial(attributes);
		for (Attribute a : attributes) {
			if (a.type == TiledMaterialAttribute.TiledMaterial) {
				set(this.u_tiledMaterialOpacity, ((TiledMaterialAttribute) a).getOpacity());
				set(this.u_tiledMaterialRatio, ((TiledMaterialAttribute) a).getRatio());
				break;
			}
		}
	}

	public static String createPrefix(final Renderable renderable, final Config config) {
		String prefix = DefaultShader.createPrefix(renderable, config); // sigh should've been local
		for (Attribute a : renderable.material) {
			if (a.type == TiledMaterialAttribute.TiledMaterial) {
				prefix += "#define tiledMaterialFlag\n";
				break;
			}
		}
		return prefix;
	}

}
