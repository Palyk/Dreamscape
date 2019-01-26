#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <string.h>
#include <time.h>

FILE * inFile;
FILE * outFile;
char singleByte;
unsigned int curSample[2]; 
unsigned int fullSample; //use to modify
int i;
int isRun;
char *argExec[10];
clock_t begin;
clock_t cur;
float curTime;
float audioFreq;

void main(int argc, char **argv) {
  i=0;
  isRun = 0;
  int child;
  argExec[0] = "aplay";
  argExec[1] = "./440c.wav";
  argExec[2] = "-f";
  argExec[3] = "S16_LE";
  argExec[4] = "-r";
  argExec[5] = "44100";
  argExec[6] = "-B";
  argExec[7] = "500000";

  audioFreq = (float)1/88202;
  inFile = fopen("./440.wav","r");
  outFile = fopen("./440c.wav","w+");
  begin = clock();
  while(1) {
    cur = clock();
    curTime = (float) (cur-begin)/CLOCKS_PER_SEC;

    while((float)i*audioFreq > curTime) {
      cur = clock();
      curTime = (float) (cur-begin)/CLOCKS_PER_SEC;
    }
    curSample[0]=getc(inFile);
    if(feof(inFile)) {
      break;
    }
    ++i;
    if(i<45) { //still in header
      //putc(curSample[0],outFile);
      continue;
    }
    else {
      
      curSample[1]=getc(inFile);
      ++i;

      //combine chars into sample, then modify
      fullSample = (curSample[0]<<8)+curSample[1];
      //fullSample = fullSample << 4; //removing to get a clean copy
      curSample[0] = fullSample >> 8;
      curSample[1] = fullSample & 255;


      //output sound
      putc(curSample[0], outFile);
      putc(curSample[1], outFile);
      if(!isRun && i > 86666) {
	isRun = 1;
	child = fork();
	if(child == 0) {
	  execvp(argExec[0], argExec);
	}
      }
    }
  }
  fclose(inFile);
  fclose(outFile);
  printf("gets this far\n");
  //execvp(argExec[0], argExec);
  //printf("ERROR: %s\n", strerror(errno));
  //printf("doesn't run\n");
}
