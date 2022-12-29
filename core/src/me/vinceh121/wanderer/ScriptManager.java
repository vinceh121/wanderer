package me.vinceh121.wanderer;

import java.net.URI;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import me.vinceh121.wanderer.script.FileHandleModuleSourceProvider;
import me.vinceh121.wanderer.script.JsAudio;
import me.vinceh121.wanderer.script.JsConsole;
import me.vinceh121.wanderer.script.JsTimers;
import me.vinceh121.wanderer.story.Chapter;
import me.vinceh121.wanderer.story.Part;
import me.vinceh121.wanderer.story.StoryBook;

public class ScriptManager {
	private final Context ctx = Context.enter();

	public ScriptManager() {
		this.ctx.setLanguageVersion(Context.VERSION_ES6);
	}

	public Scriptable loadChapter(final FileHandle src, final FileHandle base) {
		return this.loadChapter(FileHandleModuleSourceProvider.fromFileHandle(src),
				FileHandleModuleSourceProvider.fromFileHandle(base));
	}

	public Scriptable loadChapter(final URI src, final URI base) {
		final RequireBuilder reqBuild = new RequireBuilder();
		reqBuild.setSandboxed(true);
		reqBuild.setModuleScriptProvider(new SoftCachingModuleScriptProvider(new FileHandleModuleSourceProvider(base)));
		final ScriptableObject scope = this.ctx.initSafeStandardObjects();
		ScriptManager.fillStoryScope(scope);
		final Require req = reqBuild.createRequire(this.ctx, scope);
		final Scriptable exports = req.requireMain(this.ctx, src.toString());
		return exports;
	}

	public void update() {
		JsTimers.getInstance().update(Gdx.graphics.getDeltaTime());
	}

	public void dispose() {
		Context.exit();
	}

	public static void fillStoryScope(final ScriptableObject scope) {
		ScriptManager.fillStoryPartScope(scope);
	}

	public static void fillStoryPartScope(final ScriptableObject scope) {
		new JsConsole().install(scope);
		// Use a global JsTimers to avoid id-collision as much as possible
		JsTimers.getInstance().install(scope);
		JsAudio.install(scope);
		final Class<?>[] classesToImport = { StoryBook.class, Chapter.class, Part.class };

		for (final Class<?> cls : classesToImport) {
			scope.put(cls.getSimpleName(), scope, new NativeJavaClass(scope, cls));
		}
	}
}
