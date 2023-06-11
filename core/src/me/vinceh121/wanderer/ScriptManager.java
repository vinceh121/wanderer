package me.vinceh121.wanderer;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.script.FileHandleModuleSourceProvider;
import me.vinceh121.wanderer.script.JsAudio;
import me.vinceh121.wanderer.script.JsConsole;
import me.vinceh121.wanderer.script.JsTimers;
import me.vinceh121.wanderer.script.WandererContextFactory;
import me.vinceh121.wanderer.story.Chapter;
import me.vinceh121.wanderer.story.Part;
import me.vinceh121.wanderer.story.StoryBook;

public class ScriptManager {
	private static final List<Class<?>> PART_SCOPE_CLASSES = new ArrayList<>();
	private final Context ctx;
	private final ScriptableObject baseScope = new NativeObject();

	public ScriptManager() {
		this.ctx = Context.enter();
		this.ctx.setLanguageVersion(Context.VERSION_ES6);
	}

	public Scriptable loadModule(final FileHandle src, final FileHandle base) {
		return this.loadModule(FileHandleModuleSourceProvider.fromFileHandle(src),
				FileHandleModuleSourceProvider.fromFileHandle(base));
	}

	public Scriptable loadModule(final URI src, final URI base) {
		final RequireBuilder reqBuild = new RequireBuilder();
		reqBuild.setSandboxed(true);
		reqBuild.setModuleScriptProvider(new SoftCachingModuleScriptProvider(new FileHandleModuleSourceProvider(base)));
		final ScriptableObject scope = this.ctx.initSafeStandardObjects();
		ScriptManager.copyObject(this.baseScope, scope);
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

	public ScriptableObject getBaseScope() {
		return this.baseScope;
	}

	public Context getContext() {
		return this.ctx;
	}

	public static void copyObject(final Scriptable from, final Scriptable to) {
		for (Object id : from.getIds()) {
			if (id instanceof Integer) {
				to.put((int) id, to, from.get((int) id, from));
			} else if (id instanceof String) {
				to.put((String) id, to, from.get((String) id, from));
			}
		}
	}

	public static void fillStoryScope(final ScriptableObject scope) {
		ScriptManager.fillStoryPartScope(scope);
	}

	public static void fillStoryPartScope(final ScriptableObject scope) {
		new JsConsole().install(scope);
		// Use a global JsTimers to avoid id-collision as much as possible
		JsTimers.getInstance().install(scope);
		JsAudio.install(scope);

		for (final Class<?> cls : PART_SCOPE_CLASSES) {
			scope.put(cls.getSimpleName(), scope, new NativeJavaClass(scope, cls));
		}
	}

	static {
		ContextFactory.initGlobal(new WandererContextFactory());
		PART_SCOPE_CLASSES.addAll(Arrays.asList(
				// .story
				StoryBook.class,
				Chapter.class,
				Part.class));

		Reflections entityRef = new Reflections("me.vinceh121.wanderer");
		PART_SCOPE_CLASSES.addAll(entityRef.get(Scanners.SubTypes.of(AbstractEntity.class).asClass()));
	}
}
