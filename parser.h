#define NUM_EFFECTS 3
#define DELAY_MAX 10000000

typedef struct audSignal {
  int signal[DELAY_MAX];
}audSignal_t;

typedef struct audEffect {
  //audEffect will be the struct used for storing each individual step.
  //aOut will be an array cooresponding to the different effects, each to be done in
  //parallel. if only one is used, set others to clean.
  int aOut[NUM_EFFECTS][NUM_EFFECTS];
  //aWeight will represent the weighting of each individual effect on the end sounds.
  //the values of each value in the aWeight array must sum to 1, check to confirm.
  //use 1 for solo effect.
  float aWeight[NUM_EFFECTS][NUM_EFFECTS];
  //in the future, add configuration values here too, in case a different value is needed
  //across the same effect multiple times. isRecord will remain global for obvious reasons.
  //uncomment next few lines once implemented.
  //assume constant values, implement changing values later
    /*int crushVal;
    int boostVal;
    int delayVal;
    int distortVal;
    int fuzzVal;
    int maxCount;
    int delayVal1;
    int delayVal2;
    int delayVal3;
    int octaverVal;*/
  int val1[NUM_EFFECTS][NUM_EFFECTS];
  int val2[NUM_EFFECTS][NUM_EFFECTS];
  int val3[NUM_EFFECTS][NUM_EFFECTS];
  audSignal_t looper[NUM_EFFECTS][NUM_EFFECTS];
  char name[100];
  int fromOrigin[NUM_EFFECTS][NUM_EFFECTS];
  int doWipe[NUM_EFFECTS][NUM_EFFECTS];
   
}audEffect_t;


void readPreset(char *presetFile, audEffect_t *a);
void clearPreset(audEffect_t *effect);
void writePreset(int presetNum);
