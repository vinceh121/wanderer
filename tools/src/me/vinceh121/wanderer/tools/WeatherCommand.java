package me.vinceh121.wanderer.tools;

import static java.util.Map.entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
			entry("galaxy_diff", "galaxyColor"));
	private static final float DAY_LENGTH = 86400f;

	@Option(names = { "-i", "--input" })
	private File input;

	@Option(names = { "-m", "--model" })
	private File model;

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

		System.out.println(doc.toPrettyString());

		return 0;
	}
}
