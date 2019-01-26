#python 3 btw
import os
import glob
import time
import signal
from pathlib import Path
#import RPi.GPIO as GPIO
from bluetooth import *

#os.system commands likely unneeded for this project
#run "sudo hciconfig hci0 piscan"
#BIG NOTE, TEST NEW CODE BELOW TOMORROW
#GPIO.setmode(GPIO.BOARD)

Motor1A = 16
Motor1B = 18
Motor1E = 22

Motor2A = 7
Motor2B = 5
Motor2E = 3
sigPID = 0
my_file = Path("pid.txt")
isDownload = 0
#isUpload = 0 not needed afaik
readByte = 0
fileList = ["./presets/preset1","./presets/preset2","./presets/preset3","./presets/preset4","./presets/preset5","./presets/preset6","./presets/preset7","./presets/preset8","./presets/preset9","./presets/preset10"]
#fileList 'may' error but I think I have it right, check tomorrow
#GPIO setup, Motor1 = Drive motor and Motor2 = turn motor


server_sock=BluetoothSocket( RFCOMM )
server_sock.bind(("",PORT_ANY))
server_sock.listen(1)

port = server_sock.getsockname()[1]

uuid = "00001101-0000-1000-8000-00805F9D34FB" #check if true

advertise_service( server_sock, "RPiServer",
                   service_id = uuid,
                   service_classes = [ uuid, SERIAL_PORT_CLASS ],
                   profiles = [ SERIAL_PORT_PROFILE ]
                   #protocols = [ OBX_UUID ]
                   )
while True:
    print("Waiting for connection on RFCOMM channel %d" % port)

    client_sock, client_info = server_sock.accept()
    print("Accepted connection from ", client_info)

    try:
        while True:
            data = client_sock.recv(1024)
            if data == "":
                print("being caught here")
                continue
            print("received [%s]" % data.decode('utf-8'))
            f = open("command.txt", "w")
	    #TODO handling downloading a preset instead of just passing the proper preset
            f.write(data.decode('utf-8'))
            f.close()
            f = open("command.txt", "r")
            readByte = f.read(1)
            if readByte == "d":
                print("downloading...")
                #is download
                presetFile = int(f.read(1))
                print("read", str(presetFile))
                g = open(fileList[int(presetFile)-1], "w")
                print("got here")
                g.write(f.read())
                f.close()
                g.close()
                isDownload = 1
                print("success")
            elif readByte == "u":
		#is upload
                readByte = f.read(1)
                if readByte == '\x00':
                    continue
		#read the next byte which will point to the preset desired
                g = open(fileList[int(readByte)-1], "r")
                data = g.read()
                f.close()
                g.close()
                client_sock.send(data)
				
			
            if sigPID == 0 and my_file.exists():
                f = open("pid.txt", "r")
                if f.mode == 'r':
                    sigPID = int(f.read())
                    f.close()
                else:
                    print("Nothing here")
                    f.close()
            if sigPID != 0:
                if isDownload == 1:
                        os.kill(sigPID, signal.SIGUSR1)
                        isDownload = 0
                else:
                        os.kill(sigPID, signal.SIGCONT)
            
            
    except IOError:
        pass

    except KeyboardInterrupt:
        print("disconnected")
        client_sock.close()
        server_sock.close()
        print("all done")
        break
