
const part = new Part("The Old Master Builder", [
	"Find a backpack", // "Explore the island",
	"Collect all lighthouses" // "Learn from the mighty Master Builder"
]);

part.state.collectedBuildings = 0;

part.setPartStart(() => {
	setDayTime(3, 15);

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
				console.log("Picked up lighthouses");
			}
		});
	});

	loadMapFragment("./fragIntro.json");
	play("orig/book/music/native44a.wav");
	playCinematic("./cinIntro.json").addEventListener("over", () => {
		console.log("over");
	});
});

exports.part = part;
