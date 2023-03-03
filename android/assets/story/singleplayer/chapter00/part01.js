
const part = new Part("The Old Master Builder", [
	"Explore the island",
	"Learn from the mighty Master Builder"
]);

part.setPartStart(() => {
	const artifact = this.game.findFirstEntityByClass(BackpackArtifact.__javaObject__);
	artifact.addEventListener("pickedUp", e => {
		this.part.setObjectivesCompleted(1);
	});
});

exports.part = part;
