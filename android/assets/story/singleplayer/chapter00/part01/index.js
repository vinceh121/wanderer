
const part = new Part("The Old Master Builder", [
	"Find a backpack", // "Explore the island",
	"Collect all lighthouses" // "Learn from the mighty Master Builder"
]);

part.state.collectedBuildings = 0;

part.setPartStart(() => {
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

	spawn("a_dd").setSymbolicName("dd");
	spawn("a_flieger01").setSymbolicName("flieger01");
	spawn("a_j_scout01").setSymbolicName("j_scout");

	play("orig/book/music/native44a.wav");
	playCinematic("./cinIntro.json");
});

exports.part = part;
