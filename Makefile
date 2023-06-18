i18n:
	xgettext --from-code=UTF-8 --add-comments -o i18n/messages.pot **/*.java **/*.js
	node tools/cinxgettext.js i18n/messages.pot android/assets/**/cin*.json
	msguniq -o i18n/messages.pot i18n/messages.pot

