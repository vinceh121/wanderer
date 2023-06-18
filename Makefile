GETTEXT_FILES = $(shell find androis/assets android/src launcher/src desktop/src tools/src core/src -regextype egrep -regex '.*\.(js|java)' -print)
CINEMATICS = $(shell find android/assets/ -name 'cin*.json' -print)
TRANSLATIONS = $(shell find i18n -name '*.po' -print)

i18n:
	xgettext --from-code=UTF-8 --add-comments -o i18n/messages.pot $(GETTEXT_FILES)
	node tools/cinxgettext.js i18n/messages.pot $(CINEMATICS)
	msguniq -o i18n/messages.pot i18n/messages.pot
	for i in $(TRANSLATIONS); do msgmerge -U --previous $$i i18n/messages.pot; done

.PHONY: i18n

