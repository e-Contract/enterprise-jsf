.PHONY: info
.SILENT: info
info:
	echo "Makefile usage:"
	echo "make clean"
	echo "\tCleanup the work directories."
	echo "make install"
	echo "\tInstall all dependencies."
	echo "make start"
	echo "\tStarts development server."

.PHONY: install
install:
	npm install

.PHONY: start
start:
	npm start

.PHONY: clean
clean:
	rm -rf package-lock.json build node_modules
