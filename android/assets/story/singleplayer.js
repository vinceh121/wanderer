
const storyBook = new StoryBook([
	new Chapter([
		require("./singleplayer/chapter00/part01").part
	])
]);

exports.storyBook = storyBook;
