This folder should contain assets extracted and converted from the original game with:

	java -jar nebula2-assets-extractor.jar extract -i "~/Games/Project Nomads/Run/data.npk" -o assets/orig
	cp -r "~/Games/Project Nomads/Run/book/feedback" assets/orig/feedback
	cp --parents "~/Games/ProjectNomads/Project\ Nomads/Run/book/**/*.wav" assets/orig/book

Assets shouldn't be commited to Git.

