#include <X11/Xlib.h>
#include <X11/Xutil.h>
#include <X11/Xos.h>
#include "parser.h"
#include <string.h>
#include <stdio.h>

Display *dis;
int screen;
Window win;
GC gc;
char * text;
XFontStruct * font;

void ioInit() {
  unsigned long black,white;
  dis=XOpenDisplay((char *)0);
  screen=DefaultScreen(dis);
  black = BlackPixel(dis,screen);
  white = WhitePixel(dis,screen);
  
  win = XCreateSimpleWindow(dis,DefaultRootWindow(dis),0,0,800,480,0,white,white);
  XSetStandardProperties(dis,win,"multi-effect software", "display",None,NULL,0,NULL);
  XSelectInput(dis,win, StructureNotifyMask);
  XMapWindow(dis,win);
  gc=XCreateGC(dis,win,0,0);
  //XSetBackground(dis,gc,white);
  //XSetForeground(dis,gc,white);
  const char * fontname = "-*-helvetica-*-r-*-*-72-*-*-*-*-*-*-*";
  font = XLoadQueryFont(dis, fontname);
  if(!font) {
	printf("Loading font failed, using fallback\n");
	font = XLoadQueryFont(dis, "fixed");
	
  }
  XSetFont(dis,gc,font->fid);
  XClearWindow(dis,win);
  XMapRaised(dis,win);
	
}

void ioClose() {
	XFreeGC(dis,gc);
	XDestroyWindow(dis,win);
	XCloseDisplay(dis);
}

void printDetails(int presetNum, char* name) {
	char line1[40] = "";
	char line2[100];
	int i;
	line1[9]='\0';
	line1[8]='\n';
	for(i=0; i<8; ++i) {
		if(i==presetNum) {
			//line1[i]='0   ';
			strcat(line1,"0   ");
		}
		else {
			//line1[i]='*   ';
			strcat(line1,"*   ");
		}
	}
	sprintf(line2, "Preset Name: %s\n", name);
	XClearWindow(dis,win);
   
	XDrawString(dis,win,gc,10,100,line1, strlen(line1)-1);
	XDrawString(dis,win,gc,10,300,line2, strlen(line2)-1);
	//XDrawString(dis,win,gc,20,20,"Hello", strlen("Hello"));
	
	XFlush(dis);
	XMoveWindow(dis,win,0,0);
	XRaiseWindow(dis,win);
}
