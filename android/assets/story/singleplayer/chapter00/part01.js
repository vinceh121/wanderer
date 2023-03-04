
const part = new Part("The Old Master Builder", [
	"Find a backpack", // "Explore the island",
	"Collect all lighthouses" // "Learn from the mighty Master Builder"
]);

part.state.collectedBuildings = 0;

part.setPartStart(() => {
	const backpack = this.game.findFirstEntityByClass(BackpackArtifact.__javaObject__);
	backpack.addEventListener("pickedUp", e => {
		part.addObjectiveCompleted(0);
	});

	this.game.findEntitiesByClass(BuildingArtifactEntity.__javaObject__).forEach(building => {
		building.addEventListener("pickedUp", e => {
			part.state.collectedBuildings++;
			if (part.state.collectedBuildings == 3) {
				part.addObjectiveCompleted(1);
			}
		});
	});
});

exports.part = part;
