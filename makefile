all: wavIO audiocopy

wavIO: wavIO.c
	@gcc -o wavIO wavIO.c effectMux.c parser.c io.c -g -lportaudio -lbcm2835 -lX11 -lpthread

audiocopy: audiocopy.c
	@gcc -o audiocopy audiocopy.c

clean:
	@rm -r wavIO
	@rm -r audiocopy
