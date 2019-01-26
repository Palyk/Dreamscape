// CC-by-www.Electrosmash.com Pedl-Pi open-source project
// clean.c effect pedal, the signal is read by the ADC and written again using 2 PWM signals. 


// EDITED for custom project ---> Senior Design group DEC18 #21





  
#include <stdio.h>
#include <bcm2835.h> 
//#include <portaudio.h>


 
  
 
//main program 
int main(int argc, char **argv)
{



    // Start the BCM2835 Library to access GPIO.
    if (!bcm2835_init())
    {printf("bcm2835_init failed. Are you running as root??\n"); return 1;}
 
 
    // Start the SPI BUS.
    if (!bcm2835_spi_begin())
    {printf("bcm2835_spi_begin failed. Are you running as root??\n"); return 1;}



  
    //define SPI bus configuration for ADC
    bcm2835_spi_setBitOrder(BCM2835_SPI_BIT_ORDER_MSBFIRST);      // The default
    bcm2835_spi_setDataMode(BCM2835_SPI_MODE0);                   // The default
    bcm2835_spi_setClockDivider(BCM2835_SPI_CLOCK_DIVIDER_256);   // 4MHz clock with _256 
    bcm2835_spi_chipSelect(BCM2835_SPI_CS0);                      // The default
    bcm2835_spi_setChipSelectPolarity(BCM2835_SPI_CS0, LOW);      // the default
    bcm2835_spi_setChipSelectPolarity(BCM2835_SPI_CS1, LOW);      // the default


  
    uint8_t ADC_write[3] = { 0x01, 0x00, 0x00 }; //12 bit ADC read channel 0. 
    uint8_t ADC_miso[3] = { 0 };

    uint8_t DAC_mosi[3] = { 0 };



    while(1) //Main Loop
    {
    	


	

	bcm2835_spi_chipSelect(BCM2835_SPI_CS0);


    	//read 12 bits ADC
    	bcm2835_spi_transfernb(ADC_write, ADC_miso, 3);

	
	bcm2835_spi_chipSelect(BCM2835_SPI_CS1);


	DAC_mosi[0] = (ADC_miso[1] & 0x0F) + 0x10;
	DAC_mosi[1] = ADC_miso[2];

	bcm2835_spi_writenb(DAC_mosi, 2);


    }
  
    //close all and exit
    bcm2835_spi_end();
    bcm2835_close();
    return 0;


}




