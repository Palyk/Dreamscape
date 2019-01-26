int summer(int aud1, int aud2);
int clean(int sound); //0
int bitcrush(int sound, int crushVal); //1:qa
int booster(int sound, int boostVal); //2:ws
int delayeff(int sound, int delayVal); //3:ed
int distortion(int sound, int distortVal); //4:rf
int echo(int sound, int delayVal); //5:ed
int fuzz(int sound, int fuzzVal); //6:yh
int looper(int sound, int delayVal, int isRecord, int j, int doWipe); //7:ed,t
int invertLooper(int sound,  int delayVal, int isRecord, int j, int doWipe);
int octaver(int sound, int delayVal, int octaverVal, int doWipe, int isRecord, int j); //8:ed,gb
int reverb(int sound, int delayVal1, int delayVal2, int delayVal3); //9:ol
int tremolo(int sound, int maxCount); //::+-
int refOrigin(int sound);
int noSound();
int fbLooper(int sound, int delayVal, int isRecord, int j, int doWipe);
