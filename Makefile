#
# Enterprise JSF project.
#
# Copyright 2022 e-Contract.be BV. All rights reserved.
# e-Contract.be BV proprietary/confidential. Use is subject to license terms.
#

FIGS =
PUML =
TEX_INCLUDES = preface.tex copyright.tex introduction.tex testing.tex production.tex
SVG = e-contract-logoFINAL.svg

.PHONY: all
all: enterprise-jsf.pdf

enterprise-jsf.pdf : enterprise-jsf.tex $(TEX_INCLUDES) $(SVG:.svg=.pdf) $(FIGS:.fig=.pdftex) $(PUML:.puml=.pdf)
	pdflatex -shell-escape $<
	pdflatex -shell-escape $<
	pdflatex -shell-escape $<

%.pdftex %.pdftex_t: %.fig
	fig2dev -L pdftex_t -p $*.pdftex $< $*.pdftex_t
	fig2dev -L pdftex $< $*.pdftex

%.pdf: %.svg
	rsvg-convert -f pdf -o $*.pdf $*.svg

# tried rsvg-convert: does not always rasterize, embeds all fonts, resulting PDF too big
# tried cairosvg: looks terrible
# tried inkscape: does not always rasterize
# tried chromium headless: always outputs in A4
%.pdf: %.puml
	java -jar $(PLANTUML_JAR) -DPLANTUML_LIMIT_SIZE=8192 -Djava.awt.headless=true -charset UTF-8 -tlatex $*.puml
	pdflatex $*.latex
	rm $*.latex

# tried svg2pdf: no shadow effect, but also gives good result
#%.pdf: %.puml
#	java -jar $(PLANTUML_JAR) -DPLANTUML_LIMIT_SIZE=8192 -tsvg $*.puml
#	svg2pdf $*.svg $*.pdf
#	rm $*.svg

.PHONY: version
version:
	pdflatex -version
	java -version
	java -jar $(PLANTUML_JAR) -Djava.awt.headless=true -version

%.png: %.puml
	java -jar $(PLANTUML_JAR) -DPLANTUML_LIMIT_SIZE=8192 -Djava.awt.headless=true $*.puml

.PHONY: clean
clean:
	rm -f *.aux *.log *.pdf $(FIGS:.fig=.pdftex) $(FIGS:.fig=.pdftex_t) *.toc *.lof *.lot *.out *.bbl *.blg *.fls *.fdb_latexmk
