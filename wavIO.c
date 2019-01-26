#include <stdio.h>
#include <stdlib.h>
#include <termios.h>
#include <unistd.h>
#include <signal.h>
#include <stdint.h>
#include <bcm2835.h>
#include "io.h"
#include <pthread.h>

#include "effectMux.h"
#include "portaudio.h"
#include "parser.h"

#define SAMPLE_RATE (22050)
//#define SAMPLE_RATE (44100)
#define NUM_EFFECTS 3
#define NUM_PARALLEL 3 //use with parallel, make 2d array for effects
//typedef void sigfunc(int)

uint32_t read_timer=0;
uint32_t input_signal=0;
uint32_t output_signal=0;
uint8_t mosi[10] = { 0x01, 0x00, 0x00 }; //12 bit ADC read channel 0. 
uint8_t miso[10] = { 0 };
uint8_t DAC_mosi[10] = { 0 };

int data_ready = 0;
int pid;
pthread_mutex_t lock;


FILE * inFile;
FILE * appComm;
int inEffect;
int input[2];
audEffect_t effect; //TODO add parallel capabilities later

int selEffect[2];
char fileNames[][30] = {"./presets/preset1","./presets/preset2","./presets/preset3","./presets/preset4","./presets/preset5","./presets/preset6","./presets/preset7","./presets/preset8","./presets/preset9","./presets/preset10"};
int selFile = 0;

//int sample;
/*
int crushVal;
int boostVal;
int delayVal;
int distortVal;
int fuzzVal;
int maxCount;
int delayval1[j][k];
int delayval2[j][k];
int delayval3[j][k];
int isRecord;
int octaverVal;
*/
int isRecord[3];
unsigned int i, j, k;

int wSummer(int aud1, int w1, int aud2, int w2, int aud3, int w3) {
  int a1=aud1-2048;
  int a2=aud2-2048;
  int a3=aud3-2048;
  if(w1+w2+w3==0) {
    return 0; //no weight
  }
  long long temp = (a1*w1)+(a2*w2)+(a3*w3);
  temp = temp / (w1+w2+w3);
  temp = temp +2048;
  //long long temp = (aud1 * w1 * 4096) + (aud2 * w2 * 4096) + (aud3 * w3 * 65536);
  
  //temp = temp / ((w1+w2+w3)*4096);
  
  //printf("summed of %d,%d,%d=%d\n",aud1,aud2,aud3,temp);
  return temp & 0xFFF;
}

int doEffect(int temp, int effNum, int j, int k) {
  switch(effNum) {
		case 1:
			temp = bitcrush(temp, effect.val1[j][k]);
			//printf("%d\n",effect.crushVal);
			break;
		case 2:
			temp = booster(temp, effect.val1[j][k]);
			break;
		case 3:
			temp = delayeff(temp, effect.val1[j][k]);
			break;
		case 4:
			temp = distortion(temp, effect.val1[j][k]);
			break;
		case 5:
			temp = echo(temp, effect.val1[j][k]);
			break;
		case 6:
			temp = fuzz(temp, effect.val1[j][k]);
			break;
		case 7:
			temp = looper(temp, effect.val1[j][k], isRecord[j], j, effect.doWipe[j][k]);
			break;
  case 8:
    temp = octaver(temp, effect.val1[j][k], effect.val2[j][k], effect.doWipe[j][k], isRecord[j], j);
		  break;
		case 9:
			temp = reverb(temp, effect.val1[j][k], effect.val2[j][k], effect.val3[j][k]);
			break;
		case 10:
			temp = tremolo(temp, effect.val1[j][k]);
			break;
		case 11:
			temp = invertLooper(temp, effect.val1[j][k], isRecord[j], j,effect.doWipe[j][k]);
			break;
		case 12:
			temp = noSound();
			break;
			case 13:
    temp = fbLooper(temp, effect.val1[j][k], isRecord[j], j,effect.doWipe[j][k]);
    break;
		default:
			temp = clean(temp);
		}
}

static int paWavCallback( const void *inputBuffer, void *outputBuffer,
			  unsigned long framesPerBuffer,
			  const PaStreamCallbackTimeInfo* timeInfo,
			  PaStreamCallbackFlags statusFlags,
			  void *userData )
{
  int *data = (int*)userData;
  int *out = (int*)outputBuffer;
  
  
  
  (void) inputBuffer;
  
  for( i=0; i<framesPerBuffer; i++) {
    //apply effect
    int temp = *data;
    //printf("In:%d\n ", temp);
    int temp2[3];
    int temp3[3];
    for( j=0; j<3; ++j) {
      for(k=0; k<3; ++k) {
		
		if(effect.fromOrigin[j][k] == 1 || j==0) {
		 
		  
			temp2[k] = doEffect(temp, effect.aOut[j][k],j,k);
		}
		else if(effect.fromOrigin[j][k] == 2 && j == 2) {
		  temp2[k] = doEffect(temp3[0], effect.aOut[j][k],j,k);
		}
		else {
			temp2[k] = doEffect(temp3[j-1], effect.aOut[j][k],j,k);
		}
		
		
      }
      temp3[j] = wSummer(temp2[0], effect.aWeight[j][0], temp2[1], effect.aWeight[j][1], temp2[2], effect.aWeight[j][2]);
      
      

    }
    //printf("in: %d", temp);
    temp = temp3[2];
    //printf(" out: %d\n", temp);
    *out++ = temp;
    //writeDAC();
	//printf("%d\n", temp);
    //printf("Out:%d\n", temp);
    //*data = data << 3;
    fread(data, 2, 1, inFile); //21
	bcm2835_spi_transfernb(mosi, miso, 3);
	//data = miso[2] + ((miso[1] & 0x0F) << 8);
 
    
  }
  return 0;

}
static int data;

static int lowest = 4096;
static int highest = 0;

int writeDAC() {
  
    data_ready = 0;
    //printf("Write\n");
    
    //write to DAC
    //pthread_mutex_lock(&lock);
    
    int temp = ((miso[1] & 0x0F)<<8)+miso[2];
 
    //printf("In:%d\n ", temp);
    int temp2[3];
    int temp3[3];
    for( j=0; j<3; ++j) {
      for(k=0; k<3; ++k) {
	
	if(effect.fromOrigin[j][k] == 1 || j==0) {
	  
	  
	  temp2[k] = doEffect(temp, effect.aOut[j][k],j,k);
	}
	else if(effect.fromOrigin[j][k] == 2 && j == 2) {
	  temp2[k] = doEffect(temp3[0], effect.aOut[j][k],j,k);
	}
	else {
	  temp2[k] = doEffect(temp3[j-1], effect.aOut[j][k],j,k);
	}
	
	
      }
      temp3[j] = wSummer(temp2[0], effect.aWeight[j][0], temp2[1], effect.aWeight[j][1], temp2[2], effect.aWeight[j][2]);
      
      
      
    }
    
    temp = temp3[2];
    
    

    
    //printf("out:%d\n",temp);
    
    //DAC_mosi[0] = (miso[1] & 0x0F) + 0x30;
    //DAC_mosi[1] = miso[2];
    DAC_mosi[0] = ((temp >> 8)&0x0F) + 0x10;
    DAC_mosi[1] = temp & 0xFF;
    bcm2835_spi_writenb(DAC_mosi, 2);
    //pthread_mutex_unlock(&lock);
    //printf("0");
}
char inBuf[255];
void sig_handler(int signo) {
  if(signo == SIGCONT) {
    //put in file changing code here
    appComm = fopen("./command.txt", "r");
    fscanf(appComm, "%s", inBuf);
    fscanf(appComm, "%s", inBuf);
    inEffect = atoi(inBuf) - 1;
    if(inEffect != selFile) {
      printf("Hey this is new\n");
      selFile = inEffect;
      fclose(appComm);
      readPreset(fileNames[selFile], &effect);
      
    }
	else {
	  fclose(appComm); //unlikely to be run but still safe to close the file JIC
	}
  }
  else if(signo == SIGINT) {
    //maybe rig up to properly exit the program?
    //considering that part of the code might soon be moot
    //it might not be worth it but it's good to have JIC
    remove("./pid.txt");
    ioClose();
    exit(0);
  }
  else if(signo == SIGUSR1) {
	//will be used to signal resetting the current preset
	//The server side will handle downloading and modifying the preset files
	//In the event of a preset file being changed, simply reread the current preset
	//It's likely nothing will change unless the current set preset is changed
	//But there's no easy way of determining if the current preset changed otherwise
	readPreset(fileNames[selFile], &effect);
  }
  else if(signo == SIGUSR2) {
    //writeDAC();
  }
}

void* readADC(void *vargp) {
  //pthread_detach(pthread_self());
  //printf("read once");
  //while(1) {
    //printf("read");
    //read ADC
    bcm2835_spi_chipSelect(BCM2835_SPI_CS0);
    
    //pthread_mutex_lock(&lock);
    //read 12 bits ADC
    bcm2835_spi_transfernb(mosi, miso, 3);

    
    bcm2835_spi_chipSelect(BCM2835_SPI_CS1);
    
    //printf("\npre kill\n");
	//pthread_mutex_unlock(&lock);
	writeDAC();
	//printf("post kill\n");
    
    //input_signal = miso[2] + ((miso[1] & 0x0F) << 8); 
    
    
    //data_ready = 1;
    //writeDAC();
  //}
  
	//pthread_exit(NULL);
}



int main(int argc, char **argv) {
  ioInit();
  pthread_mutex_init(&lock,NULL);
printf("Init\n");
  pthread_t adcThread;
  
  pid_t serv_pid;
  if((serv_pid=fork()) == 0) {
    char *cmd = "sudo";
    char *argv[4];
    argv[0] = "sudo";
    argv[1] = "python3";
    argv[2] = "./RpiServer.py";
    argv[3] = NULL;
    execvp(cmd,argv); //UNCOMMENT LATER
  }
  //isRecord = 1;
  if(signal(SIGCONT, sig_handler) == SIG_ERR) {
    printf("\nCan't catch the signal!\n");
  }
  if(signal(SIGINT, sig_handler) == SIG_ERR) {
    printf("\nCan't catch the sigint!\n");
  }
  if(signal(SIGUSR1, sig_handler) == SIG_ERR) {
	printf("\nCan't catch sigusr1!\n");  
  }
  if(signal(SIGUSR2, sig_handler) == SIG_ERR) {
    printf("\nCan't catch sigusr2!\n");
  }
  
  // Start the BCM2835 Library to access GPIO.
  if (!bcm2835_init())
  {printf("bcm2835_init failed. Are you running as root??\n"); return 1;}
 
 
    // Start the SPI BUS.
  if (!bcm2835_spi_begin())
  {printf("bcm2835_spi_begin failed. Are you running as root??\n"); return 1;}
	bcm2835_spi_setBitOrder(BCM2835_SPI_BIT_ORDER_MSBFIRST);      // The default
    bcm2835_spi_setDataMode(BCM2835_SPI_MODE0);                   // The default
    bcm2835_spi_setClockDivider(BCM2835_SPI_CLOCK_DIVIDER_256);    // 4MHz clock with _64 
    bcm2835_spi_chipSelect(BCM2835_SPI_CS0);                      // The default
    bcm2835_spi_setChipSelectPolarity(BCM2835_SPI_CS0, LOW);      // the default
    bcm2835_spi_setChipSelectPolarity(BCM2835_SPI_CS1, LOW);
	//pushbutton code
	bcm2835_gpio_fsel(20, BCM2835_GPIO_FSEL_INPT);
	bcm2835_gpio_set_pud(20, BCM2835_GPIO_PUD_UP);
	bcm2835_gpio_fsel(21, BCM2835_GPIO_FSEL_INPT);
	bcm2835_gpio_set_pud(21, BCM2835_GPIO_PUD_UP);
	bcm2835_gpio_fsel(16, BCM2835_GPIO_FSEL_INPT);
	bcm2835_gpio_set_pud(16, BCM2835_GPIO_PUD_UP);
	bcm2835_gpio_fsel(23, BCM2835_GPIO_FSEL_INPT); // Pin 16
bcm2835_gpio_fsel( 19, BCM2835_GPIO_FSEL_INPT); // Pin 35
bcm2835_gpio_fsel(17, BCM2835_GPIO_FSEL_INPT); // Pin 11
bcm2835_gpio_fsel( 6, BCM2835_GPIO_FSEL_INPT); // Pin 31
bcm2835_gpio_fsel(24, BCM2835_GPIO_FSEL_INPT); // Pin 18
bcm2835_gpio_fsel(13, BCM2835_GPIO_FSEL_INPT); // Pin 33
bcm2835_gpio_fsel(22, BCM2835_GPIO_FSEL_INPT); // Pin 15
bcm2835_gpio_fsel( 4, BCM2835_GPIO_FSEL_INPT); // Pin 7
//bcm2835_gpio_fsel(20, BCM2835_GPIO_FSEL_INPT); // Pin 38
bcm2835_gpio_fsel(26, BCM2835_GPIO_FSEL_INPT); // Pin 37
//bcm2835_gpio_fsel(16, BCM2835_GPIO_FSEL_INPT); // Pin 36

bcm2835_gpio_set_pud(19, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(17, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(6, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(24, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(13, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(22, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(4, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(26, BCM2835_GPIO_PUD_UP);
bcm2835_gpio_set_pud(23, BCM2835_GPIO_PUD_UP);
	int counter = 0;
	inEffect = selFile;
	
  selEffect[0] = 0;
  selEffect[1] = 0;
  int inputc = 'a';
  //set default effect values
  
  isRecord[0] = 0;
  isRecord[1] = 0;
  isRecord[2] = 0;
  for(i=0; i<NUM_EFFECTS; ++i) {
    for(j=0; j<NUM_EFFECTS; ++j) {
      effect.aOut[i][j]=0; //clean
      effect.aWeight[i][j]=9;
	  effect.val1[i][j]=0;
	  effect.val2[i][j]=0;
	  effect.val3[i][j]=0;
	  effect.doWipe[i][j]=0;
    }
  }
  
  //set other values
  PaStream *stream;
  PaError err;
  readPreset(fileNames[selFile], &effect);
  printf("Loading preset %s.\n", effect.name);
  if(argc <2) {
    printf("You forgot to include the wavefile dummy\n");
    return -1;
  }
  inFile = fopen(argv[1],"r");
  fread(&data, 2, 1, inFile);
  //bcm2835_spi_transfernb(mosi, miso, 3);
	//data = miso[2] + ((miso[1] & 0x0F) << 8);
  //start audio stream
  /*err = Pa_Initialize();
  if( err != paNoError ) goto error;
  err = Pa_OpenDefaultStream( &stream,
  		      0,
  			      1,
  			      paInt16,
  			      SAMPLE_RATE,
  			      1,
  			      paWavCallback,
  			      &data );
  
   if( err != paNoError) goto error;
  err = Pa_StartStream( stream );
  if( err != paNoError ) goto error;*/
  printf("Current effects:\n");
  printf("Effect 1: %d %d %d\n", effect.aOut[0][0], effect.aOut[0][1], effect.aOut[0][2]);
  printf("Effect 2: %d %d %d\n", effect.aOut[1][0], effect.aOut[1][1], effect.aOut[1][2]);
  printf("Effect 3: %d %d %d\n", effect.aOut[2][0], effect.aOut[2][1], effect.aOut[2][2]);
  printf("%d\n", effect.val1[0][0]);
  pid = getpid();
  appComm = fopen("./pid.txt", "w");
  fprintf(appComm, "%d", pid);
  fclose(appComm);
  int delpushbutton = 0;
  int refreshWindow = 0;
  //pthread_create(&adcThread, NULL, readADC, NULL);
  
  printDetails(selFile, effect.name);
  //printDetails(*selEffect +1, effect.name);
  
 rerun:
  while(1) {
    if(feof(inFile)) {
      fseek(inFile, 100, SEEK_SET);
    }
   
    //writeDAC();
    	//writeDAC();
	//pushbutton code
	++delpushbutton;
	++refreshWindow;
	//printf("delpushbutton %d\n", delpushbutton);
	if(delpushbutton > 1000) {
		if(bcm2835_gpio_lev(16)) {
			isRecord[0] = 1;
			//printf("isRecord0\n");
		}
		else {
			isRecord[0] = 0;
			//printf("notRecord0\n");
		}
		if(bcm2835_gpio_lev(26)) { //originally 21, replacing with 26?
			isRecord[1] = 1;
			//printf("isRecord1\n");
		}
		else {
			isRecord[1] = 0;
			//printf("notRecord1\n");
		}
		if(bcm2835_gpio_lev(20)) {
			isRecord[2] = 1;
			//printf("isRecord2\n");
		}
		else {
			isRecord[2] = 0;
			//	printf("notRecord2\n");
		}
		if( bcm2835_gpio_lev(23) && selFile!=0) {
			
		//Activate Preset 1
			printf("1");
			selFile = 0;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(19)&& selFile!=1) { //??
		//Activate Preset 2
			printf("2");
			selFile = 1;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(17)&& selFile!=2) {
		//Activate Preset 3
			printf("3");			
			selFile = 2;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(6)&& selFile!=3) {
		//Activate Preset 4
			printf("4");
			selFile = 3;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(24)&& selFile!=4) {
		//Activate Preset 5
			printf("5");
			selFile = 4;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(13)&& selFile!=5) {
		//Activate Preset 6
			printf("6");
			selFile = 5;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(22)&& selFile!=6) {
		//Activate Preset 7
			printf("7");
			selFile = 6;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}  if (bcm2835_gpio_lev(4)&& selFile!=7) {
		//Activate Preset 8
			printf("8");
			selFile = 7;
			readPreset(fileNames[selFile], &effect);
			printDetails(selFile, effect.name);
		}
		delpushbutton = 0;
		
	}
	if(refreshWindow == 100000) {
	  refreshWindow = 0;
	  //printDetails(selFile +1, effect.name);
	}
	readADC(NULL);
    /* 
	if(certain switch != current preset value) {
		selFile = whatever the switch value is
		readPreset(fileNames[selFile], &effect);
	}
	*/	
  }
  
  //close all
 exit:
 bcm2835_spi_end();
 bcm2835_close();
  err = Pa_StopStream( stream );
  if( err != paNoError ) goto error;
  err = Pa_CloseStream( stream );
  if( err != paNoError ) goto error;

  err = Pa_Terminate();
  if( err != paNoError) {
    printf("PortAudio error on close: %s\n", Pa_GetErrorText( err ) );
  }
  fclose(inFile);
  return err;

 error:
  Pa_Terminate();
  fprintf( stderr, "An error occured while using portaudio.\n");
  fprintf( stderr, "Error # %d\n", err );
  fprintf( stderr, "Error message: %s\n", Pa_GetErrorText( err ) );
  return err;

}
