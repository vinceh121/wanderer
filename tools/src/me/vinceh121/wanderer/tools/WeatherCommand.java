package me.vinceh121.wanderer.tools;

import static java.util.Map.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.concurrent.Callable;

import org.apache.commons.collections4.ListValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.badlogic.gdx.graphics.Color;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.n2ae.model.NvxFileReader;
import me.vinceh121.n2ae.model.Vertex;
import me.vinceh121.n2ae.script.ClassCommandCall;
import me.vinceh121.n2ae.script.ICommandCall;
import me.vinceh121.n2ae.script.NOBClazz;
import me.vinceh121.n2ae.script.NewCommandCall;
import me.vinceh121.n2ae.script.tcl.TCLParser;
import me.vinceh121.wanderer.glx.SkyboxRenderer;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "weather", description = { "Converts a weather object to a sky configuration" })
public class WeatherCommand implements Callable<Integer> {
	private static final Map<String, String> IPOL_NAMES = Map.ofEntries(entry("amb_color", "ambLightColor"),
			entry("sun1_em", "sunColor"),
			entry("fog_color", "fogColor"),
			entry("stars_em", "starsColor"),
			entry("galaxy_diff", "galaxyColor"),
			entry("licht1_color", "sunLightColor"),
			entry("licht2_color", "moonLightColor"));
	private static final float DAY_LENGTH = 86400f;

	@Option(names = { "-i", "--input" })
	private File input;

	@Option(names = { "-m", "--model" })
	private File model;

	@Option(names = { "--skies" })
	private boolean skies;

	@Override
	public Integer call() throws Exception {
		final ObjectMapper mapper = new ObjectMapper();

		final Map<String, NOBClazz> model = mapper.readValue(this.model, new TypeReference<Map<String, NOBClazz>>() {
		});

		final TCLParser parser = new TCLParser();

		parser.setClassModel(model);

		try (final InputStream in = new FileInputStream(this.input)) {
			parser.read(in);
		}

		final ObjectNode doc = mapper.createObjectNode();

		for (int i = 0; i < parser.getCalls().size(); i++) {
			ICommandCall call = parser.getCalls().get(i);

			if (call instanceof NewCommandCall && ((NewCommandCall) call).getClazz().getName().equals("nipol")) {
				final String pnIpolName = ((NewCommandCall) call).getVarName();
				final String ipolName = IPOL_NAMES.get(pnIpolName);

				if (ipolName == null) {
					System.err.println("Unknown ipol " + pnIpolName);
					continue;
				}

				final ObjectNode ipol = mapper.createObjectNode();
				doc.set(ipolName, ipol);

				call = parser.getCalls().get(i++);

				while (!(call instanceof ClassCommandCall)
						|| !((ClassCommandCall) call).getPrototype().getName().equals("beginkeys")) {
					call = parser.getCalls().get(i++);
				}

				int keys = (int) ((ClassCommandCall) call).getArguments()[0];

				for (int j = 0; j < keys; j++) {
					call = parser.getCalls().get(i + j);

					final String cmd = ((ClassCommandCall) call).getPrototype().getName();
					final Object[] arguments = ((ClassCommandCall) call).getArguments();

					final float time = SkyboxRenderer.toDayProgress(((float) arguments[1]) / 60f);

					JsonNode keyFrame;

					if (cmd.equals("setkey4f")) {
						keyFrame = mapper.createObjectNode()
							.put("r", (float) arguments[2])
							.put("g", (float) arguments[3])
							.put("b", (float) arguments[4])
							.put("a", (float) arguments[5]);
					} else if (cmd.equals("setkey1f")) {
						keyFrame = new FloatNode((float) arguments[2]);
					} else if (cmd.equals("setkey2f")) {
						keyFrame = mapper.createArrayNode().add((float) arguments[2]).add((float) arguments[3]);
					} else if (cmd.equals("setkey3f")) {
						keyFrame = mapper.createArrayNode()
							.add((float) arguments[2])
							.add((float) arguments[3])
							.add((float) arguments[4]);
					} else {
						throw new UnsupportedOperationException(cmd);
					}

					ipol.set(Float.toString(time), keyFrame);
				}

				i += keys;
			}
		}

		if (this.skies) {
			ObjectNode skyTopColor = doc.putObject("skyTopColor");
			ObjectNode skyMiddleColor = doc.putObject("skyMiddleColor");
			ObjectNode skyBottomColor = doc.putObject("skyBottomColor");
			
			NavigableMap<Float, SkyPoint> points = new TreeMap<>();

			for (ICommandCall call : parser.getCalls()) {
				if (!(call instanceof ClassCommandCall)) {
					continue;
				}

				final ClassCommandCall clsCall = (ClassCommandCall) call;

				if (!clsCall.getPrototype().getName().equals("setkey")
						|| !((String) clsCall.getArguments()[2]).startsWith("vb")) {
					continue;
				}

				final int i = Integer.parseInt(((String) clsCall.getArguments()[2]).substring(2)) + 1;
				final float time = SkyboxRenderer.toDayProgress(((float) clsCall.getArguments()[1]) / 60f);

				try (FileInputStream in =
						new FileInputStream(this.input.toPath().resolveSibling("himmel" + i + ".nvx").toFile())) {
					NvxFileReader r = new NvxFileReader(in);
					r.readAll();

					points.put(time, this.recognizeColors(r.getVertices()));
				}
			}

			for (Entry<Float, SkyPoint> e : points.entrySet()) {
				skyTopColor.putPOJO(e.getKey().toString(), e.getValue().top);
				skyMiddleColor.putPOJO(e.getKey().toString(), e.getValue().middle);
				skyBottomColor.putPOJO(e.getKey().toString(), e.getValue().bottom);
			}

		}

		System.out.println(doc.toPrettyString());

		return 0;
	}

	private SkyPoint recognizeColors(List<Vertex> vertices) {
		ListValuedMap<Float, Color> grouped = new ArrayListValuedHashMap<>();

		for (Vertex v : vertices) {
			Color color = new Color();
			Color.rgba8888ToColor(color, bgra8888Torgba8888(v.getColor()));
			grouped.put(v.getCoord()[1], color);
		}

		float min = Collections.min(grouped.keys());
		float max = Collections.max(grouped.keys());

		float middle = (max + min) / 2;

		assert grouped.get(min).size() == 1;
		assert grouped.get(max).size() == 1;
		assert grouped.get(middle).size() != 0;

		SkyPoint point = new SkyPoint();

		point.getTop().set(grouped.get(max).get(0));
		point.getBottom().set(grouped.get(min).get(0));
		point.getMiddle().set(average(grouped.get(middle)));

		return point;
	}

	public static int bgra8888Torgba8888(int bgra) {
		final int normB = (bgra & 0xFF000000) >>> 24;
		final int normG = (bgra & 0x00FF0000) >>> 16;
		final int normR = (bgra & 0x0000FF00) >>> 8;
		final int normA = (bgra & 0x000000FF);

		return (normR << 24) | (normG << 16) | (normB << 8) | normA;
	}

	public static Color average(Collection<Color> colors) {
		assert colors.size() != 0;

		float r = 0;
		float g = 0;
		float b = 0;
		float a = 0;

		for (Color c : colors) {
			r += c.r;
			g += c.g;
			b += c.b;
			a += c.a;
		}

		r /= colors.size();
		g /= colors.size();
		b /= colors.size();
		a /= colors.size();

		return new Color(r, g, b, a);
	}

	private static class SkyPoint {
		private final Color top = new Color(), middle = new Color(), bottom = new Color();

		public Color getTop() {
			return top;
		}

		public Color getMiddle() {
			return middle;
		}

		public Color getBottom() {
			return bottom;
		}

		@Override
		public String toString() {
			return "SkyPoint [top=" + top + ", middle=" + middle + ", bottom=" + bottom + "]";
		}
	}
}
