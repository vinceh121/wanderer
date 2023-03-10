
const storyBook = new StoryBook([
	new Chapter([
		require("./singleplayer/chapter00/part01/index").part
	])
]);

exports.storyBook = storyBook;
