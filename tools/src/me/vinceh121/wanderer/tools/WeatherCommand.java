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
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.n2ae.script.ClassCommandCall;
import me.vinceh121.n2ae.script.ICommandCall;
import me.vinceh121.n2ae.script.NOBClazz;
import me.vinceh121.n2ae.script.NewCommandCall;
import me.vinceh121.n2ae.script.tcl.TCLParser;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "weather", description = { "Converts a weather object to a sky configuration" })
public class WeatherCommand implements Callable<Integer> {
	private static final Map<String, String> IPOL_NAMES = Map.ofEntries(entry("amb_color", "ambLightColor"));
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
				final String ipolName = getIpolName(((NewCommandCall) call).getVarName());

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

					final float time = ((float) arguments[1]) / DAY_LENGTH;

					JsonNode keyFrame;

					if (cmd.equals("setkey4f")) {
						ObjectNode keyFrameObj = mapper.createObjectNode();
						keyFrame = keyFrameObj;

						keyFrameObj.put("r", (float) arguments[2]);
						keyFrameObj.put("g", (float) arguments[3]);
						keyFrameObj.put("b", (float) arguments[4]);
						keyFrameObj.put("a", (float) arguments[5]);
					} else {
						continue;
					}

					ipol.set(Float.toString(time), keyFrame);
				}

				i += keys;
			}
		}

		System.out.println(doc.toPrettyString());

		return 0;
	}

	private static String getIpolName(String pnName) {
		String wName = IPOL_NAMES.get(pnName);

		if (wName == null) {
			System.err.println("Unknown ipol " + pnName);
			return pnName;
		} else {
			return wName;
		}
	}
}
