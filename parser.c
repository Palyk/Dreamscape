#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "parser.h"



void readPreset(char *presetFile, audEffect_t *a) {
    FILE *inFile;
    char inLine[2555];
    int isValid=0;
    int isStart=0;
    int i=0;
    int j=0;
    inFile = fopen(presetFile, "r");
	if(inFile == NULL) {
		printf("ERROR\n");
		return;
	}
	printf("opened\n");
    while(1) {
        fscanf(inFile, "%s", inLine);
        printf("%s ", inLine);
        if(strcmp(inLine, "447448") && !isValid) {
            
            return;
        }
        else if(!strcmp(inLine, "447448")) {
            isValid = 1;
        }
        else if(!strcmp(inLine, "NAME")) {
            fscanf(inFile, "%s", inLine);
            strcpy(a->name, inLine);
        }
        else if(!strcmp(inLine,"START")) {
            isStart = 1;
            i=0;
            j=0;
	    printf("START CALLED\n");
        }
        else if(!isStart) {
            printf("Malformed preset file.\n");
            return;
            //assume proper formatting from this point forward
        }

        else if(!strcmp(inLine, "STEP")) {
	        ++i;
            j=0;
        }
        else if(!strcmp(inLine, "CLEAN")) {
	  
            a->aOut[i][j] = 0;
	    fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
            
        }
        else if(!strcmp(inLine, "BITCRUSH")) {
            a->aOut[i][j] = 1;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
            
        }
        else if(!strcmp(inLine, "BOOSTER")) {
	        a->aOut[i][j] = 2;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "DELAY")) {
            a->aOut[i][j] = 3;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "DISTORT")) {
            a->aOut[i][j] = 4;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "ECHO")) {
	  a->aOut[i][j] = 5; //deprecated echo into reverb
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
	    a->val2[i][j] = 0; //echo into reverb
	    a->val3[i][j] = 0; //echo into reverb
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "FUZZ")) {
            a->aOut[i][j] = 6;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "LOOPER")) {
            a->aOut[i][j] = 7;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
			fscanf(inFile, "%s", inLine);
			a->doWipe[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "OCTAVER")) {
            a->aOut[i][j] = 8;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->val2[i][j] = atoi(inLine);
			fscanf(inFile, "%s", inLine);
            a->doWipe[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "REVERB")) {
            a->aOut[i][j] = 9;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->val2[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->val3[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
        else if(!strcmp(inLine, "TREMOLO")) {
            a->aOut[i][j] = 10;
            fscanf(inFile, "%s", inLine);
            a->val1[i][j] = atoi(inLine);
            fscanf(inFile, "%s", inLine);
            a->aWeight[i][j] = atof(inLine);
            fscanf(inFile, "%s", inLine);
	    a->fromOrigin[i][j] = atoi(inLine);
	    ++j;
        }
		else if(!strcmp(inLine, "RLOOPER")) {
			a->aOut[i][j] = 11;
			fscanf(inFile, "%s", inLine);
			a->val1[i][j] = atoi(inLine);
			fscanf(inFile, "%s", inLine);
			a->doWipe[i][j] = atoi(inLine);
			fscanf(inFile, "%s", inLine);
			a->aWeight[i][j] = atof(inLine);
			fscanf(inFile, "%s", inLine);
			a->fromOrigin[i][j] = atoi(inLine);
			++j;
			
		}
		else if(!strcmp(inLine, "NOSOUND")) {
			a->aOut[i][j] = 12;
			a->aWeight[i][j]=0;
			++j;
			
		}
		else if(!strcmp(inLine, "FBLOOPER")) {
		  a->aOut[i][j] = 13;
		  fscanf(inFile, "%s", inLine);
		  a->val1[i][j] = atoi(inLine);
		  fscanf(inFile, "%s", inLine);
		  a->doWipe[i][j] = atoi(inLine);
		  fscanf(inFile, "%s", inLine);
		  a->aWeight[i][j] = atof(inLine);
		  fscanf(inFile, "%s", inLine);
		  a->fromOrigin[i][j] = atoi(inLine);
		  ++j;
		}
	else if(!strcmp(inLine, "END")) {
		printf("Loaded Preset %s.\n", a->name);
	    break;


	}
	

        

    }
    fclose(inFile);
	printf("Closed file.\n");
    return;
}

void writePreset(int presetNum) {

}

void clearPreset(audEffect_t *effect) {
  int i,j;
 
  //isRecord = 0;

  for(i=0; i<NUM_EFFECTS; ++i) {
    for(j=0; j<NUM_EFFECTS; ++j) {
      effect->aOut[i][j]=0; //clean
      effect->aWeight[i][j]=9;
	  effect->val1[i][j]=0;
	  effect->val2[i][j]=0;
	  effect->val3[i][j]=0;
    }
  }
}
