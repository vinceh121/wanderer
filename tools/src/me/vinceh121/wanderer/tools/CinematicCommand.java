package me.vinceh121.wanderer.tools;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Callable;

import javax.imageio.ImageIO;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import me.vinceh121.n2ae.script.ClassCommandCall;
import me.vinceh121.n2ae.script.ICommandCall;
import me.vinceh121.n2ae.script.NOBClazz;
import me.vinceh121.n2ae.script.tcl.TCLParser;
import me.vinceh121.wanderer.animation.QuaternionKeyFrame;
import me.vinceh121.wanderer.animation.Vector3KeyFrame;
import me.vinceh121.wanderer.cinematic.AudioKeyFrame;
import me.vinceh121.wanderer.cinematic.CinematicData;
import me.vinceh121.wanderer.cinematic.InvisibleKey;
import me.vinceh121.wanderer.cinematic.LetterBoxFadeOutKey;
import me.vinceh121.wanderer.cinematic.SubtitleKeyFrame;
import me.vinceh121.wanderer.cinematic.VisibleKey;
import me.vinceh121.wanderer.json.WandererJsonModule;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "cinematic", description = { "Converts an nanimsequence to CinematicData" })
public class CinematicCommand implements Callable<Integer> {
	@Option(names = { "-a", "--append" })
	private boolean append;

	@Option(names = { "-s", "--symbolic-name" })
	private String symbolicName;

	@Option(names = { "-i", "--input" })
	private File input;

	@Option(names = { "-o", "--output" })
	private File output;

	@Option(names = { "-m", "--model" })
	private File model;

	@Option(names = { "-t", "--tesseract" })
	private boolean tesseract;

	@Override
	public Integer call() throws Exception {
		final TCLParser parser = new TCLParser();

		final ObjectMapper mapper = new ObjectMapper();
		WandererJsonModule.registerModules(mapper);
		final Map<String, NOBClazz> model = mapper.readValue(this.model, new TypeReference<Map<String, NOBClazz>>() {
		});
		parser.setClassModel(model);
		parser.getClassStack().push("nanimsequence");

		try (final InputStream in = new FileInputStream(this.input)) {
			parser.read(in);
		}

		final CinematicData data = new CinematicData();
		data.setSymbolicName(this.symbolicName);

		for (final ICommandCall cmd : parser.getCalls()) {
			if (!(cmd instanceof ClassCommandCall)) {
				throw new IllegalStateException("Unexpected command type: " + cmd);
			}

			final ClassCommandCall clsCmd = (ClassCommandCall) cmd;
			final Object[] args = clsCmd.getArguments();
			switch (clsCmd.getPrototype().getName()) {
			case "addtranslate":
				data.getPosition()
					.addKeyframe(new Vector3KeyFrame((float) args[0],
							new Vector3((float) args[1], (float) args[2], (float) args[3])));
				break;
			case "addquaternion":
				final Quaternion q = new Quaternion((float) args[1], (float) args[2], (float) args[3], (float) args[4]);
				data.getRotation().addKeyframe(new QuaternionKeyFrame((float) args[0], q));
				break;
			case "addvisual":
				this.addVisual(args, data);
				break;
			case "addaudio":
				if ("book:nix.wav".equals(args[1])) {
					break;
				}
				data.getActions().addKeyframe(new AudioKeyFrame((float) args[0], this.convertPath((String) args[1])));
				break;
			}
		}

		mapper.writerWithDefaultPrettyPrinter().writeValue(System.out, data);

		return 0;
	}

	private String convertPath(final String path) {
		if (path.startsWith("data:")) {
			return "orig/" + path.replace("data:", "").toLowerCase();
		} else if (path.startsWith("book:")) {
			return "orig/" + path.replace("book:", "book/").toLowerCase();
		} else if (path.startsWith("lib:")) {
			return "orig/" + path.replace("lib:", "lib/").toLowerCase();
		}
		return "orig/" + path.toLowerCase();
	}

	private void addVisual(final Object[] args, final CinematicData data) throws IOException, InterruptedException {
		final String visName = (String) args[1];

		// special case for subtitles
		if (visName.startsWith("book:") && visName.endsWith(".bmp")) {
			final String text;
			if ("book:nix.bmp".equals(visName)) {
				text = null;
			} else if (this.tesseract) {
				final Path path = this.input.toPath()
					.getParent()
					.getParent()
					.resolve("subtitle")
					.resolve(Paths.get(this.convertPath(visName)).getFileName());
				text = this.getSubtitleText(path.toFile()).strip();
			} else {
				text = visName;
			}

			data.getActions().addKeyframe(new SubtitleKeyFrame((float) args[0], text));
			return;
		}

		switch (visName) {
		case "visual/16zu9_out":
			data.getActions().addKeyframe(new LetterBoxFadeOutKey((float) args[0]));
			break;
		case "visual/invis":
			data.getActions().addKeyframe(new InvisibleKey((float) args[0]));
			break;
		case "visual/normal":
			data.getActions().addKeyframe(new VisibleKey((float) args[0]));
			break;
		default:
			System.err.println("Warning: Unknown visual " + visName);
			break;
		}
	}

	private String getSubtitleText(final File bmp) throws IOException, InterruptedException {
		final BufferedImage img = ImageIO.read(bmp);
		final File png = File.createTempFile("subTxt", ".png");
		ImageIO.write(img, "png", png);

		final Process p = Runtime.getRuntime().exec(new String[] { "tesseract", png.getAbsolutePath(), "-" });
		p.waitFor();

		final StringWriter str = new StringWriter();
		new InputStreamReader(p.getInputStream()).transferTo(str);
		return str.toString();
	}
}
