#!/usr/bin/node

const { argv } = require("node:process");
const { exit, readFileSync, createWriteStream } = require("node:fs");

if (argv.length < 3) {
	console.log("cinxgettext.js out.pot files...");
	console.log("Reads JSON cinematic files and generates a POT translation template to out.pot");
	exit(-1);
}

const outFile = argv[2];
const out = createWriteStream(outFile, {flags: "as"});
out.write("\n");

function stringBlock(str) {
	let block = "";
	const parts = str.split("\n");

	for (let i = 0; i < parts.length; i++) {
		block += '"';
		block += parts[i];
		block += '"';

		if (i !== parts.length - 1) {
			block += "\n";
		}
	}
	return block;
}

for (let i = 3; i < argv.length; i++) {
	const jsonFile = argv[i];
	const cin = JSON.parse(readFileSync(jsonFile));

	for (const track of cin) {
		if (!track.actions || !track.actions.keys) {
			continue;
		}

		for (const action of track.actions.keys) {
			if (!action.text || action["@class"] !== "me.vinceh121.wanderer.cinematic.SubtitleKeyFrame") {
				continue;
			}

			out.write("#: ");
			out.write(jsonFile);
			out.write("\n");
			out.write("msgid ");
			out.write(stringBlock(action.text));
			out.write('\n');
			out.write('msgstr ""\n\n');
		}
	}
}
