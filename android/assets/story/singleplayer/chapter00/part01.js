
const part = new Part("The Old Master Builder", [
	"Explore the island",
	"Learn from the mighty Master Builder"
]);

part.setPartStart(() => {
	console.log("aa", "a");
});

exports.part = part;
