
const part = new Part(gettext("The Old Master Builder"), [
	gettext("Find a backpack"), // "Explore the island",
	gettext("Collect all lighthouses") // "Learn from the mighty Master Builder"
]);

part.state.collectedBuildings = 0;

part.setPartStart(() => {
	setDayTime(1, 15);
	setSky(require("./skyIntro").skyIntro);

	const backpack = findFirstEntityByClass(BackpackArtifact);
	backpack.addEventListener("pickedUp", e => {
		part.addObjectiveCompleted(0);
		console.log("Picked up boosterpack");
	});

	findEntitiesByClass(BuildingArtifactEntity).forEach(building => {
		building.addEventListener("pickedUp", e => {
			part.state.collectedBuildings++;
			if (part.state.collectedBuildings == 3) {
				part.addObjectiveCompleted(1);
				console.log("Picked up Lighthouses");
			}
		});
	});

	loadMapFragment("./fragIntro.json");
	playCinematic("./cinIntro.json").addEventListener("over", () => {
		console.log("over");
	});
});

exports.part = part;
