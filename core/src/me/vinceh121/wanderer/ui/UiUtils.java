package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;

public final class UiUtils {
	
	private static int srcFuncColor;
	private static int dstFuncColor;
	private static int srcFuncAlpha;
	private static int dstFuncAlpha;

	/**
	 * Utility to draw a texture with 1:1 scale and vertical flip
	 */
	public static void drawFlip(Batch batch, Texture tex, float x, float y) {
		batch.draw(tex, x, y, tex.getWidth(), tex.getHeight(), 0, 0, tex.getWidth(), tex.getHeight(), false, true);
	}
	
	public static void beginBlend(Batch batch) {
		srcFuncColor = batch.getBlendSrcFunc();
		dstFuncColor = batch.getBlendDstFunc();
		srcFuncAlpha = batch.getBlendSrcFuncAlpha();
		dstFuncAlpha = batch.getBlendDstFuncAlpha();
	}
	
	public static void endBlend(Batch batch) {
		batch.setBlendFunctionSeparate(srcFuncColor, dstFuncColor, srcFuncAlpha, dstFuncAlpha);
	}
}
