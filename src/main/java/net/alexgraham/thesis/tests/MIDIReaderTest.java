package net.alexgraham.thesis.tests;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Track;



public class MIDIReaderTest {
    public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;
    public static final String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    public static void main(String[] args) throws Exception {
    	doSequence("C:/Users/Alex/files/Long.mid");
    	doSequence("C:/Users/Alex/files/played.mid");

    	doSequence("C:/Users/Alex/files/simple.mid");
    	doSequence("C:/Users/Alex/files/sibslower.mid");
    	doSequence("C:/Users/Alex/files/sib34.mid");
    	doSequence("C:/Users/Alex/files/sib68.mid");
    	doSequence("C:/Users/Alex/files/slow.mid");
    
    }
    
    public static void doSequence(String fileName) throws InvalidMidiDataException, IOException {
    	System.out.println(fileName);
        Sequence sequence = MidiSystem.getSequence(new File(fileName));
        System.out.println("Type of division " + sequence.getDivisionType());
        System.out.println("Resolution " + sequence.getResolution());
        int trackNumber = 0;
        for (Track track :  sequence.getTracks()) {
            trackNumber++;
            System.out.println("Track " + trackNumber + ": size = " + track.size() + " ticks " + track.ticks());
            System.out.println();
            for (int i=0; i < track.size(); i++) { 
                MidiEvent event = track.get(i);
                System.out.print("@" + event.getTick() + " ");
                MidiMessage message = event.getMessage();
                if (message instanceof ShortMessage) {
                    ShortMessage sm = (ShortMessage) message;
                    System.out.print("Channel: " + sm.getChannel() + " ");
                    if (sm.getCommand() == NOTE_ON) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note on, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else if (sm.getCommand() == NOTE_OFF) {
                        int key = sm.getData1();
                        int octave = (key / 12)-1;
                        int note = key % 12;
                        String noteName = NOTE_NAMES[note];
                        int velocity = sm.getData2();
                        System.out.println("Note off, " + noteName + octave + " key=" + key + " velocity: " + velocity);
                    } else {
                        System.out.println("Command:" + sm.getCommand());
                    }
                } else if (message instanceof MetaMessage) {
                	MetaMessage m = (MetaMessage)message;
                    System.out.println("Meta message: " + Integer.toHexString(m.getType()) + " " + m.getType());

                    if (Integer.toHexString(m.getType()).equals("58")) {
                    	byte[] arr = m.getMessage();
                    	int numerator = arr[3];
                    	int power = arr[4];
                    	int clickperclock = arr[5];
                    	int thirtysecondperbeat = arr[6];
                        //System.out.println("Numerator " + numerator + " power " + power + " clicks per clock " + clickperclock + " thirty seconds per beat " + thirtysecondperbeat);
                        String timesig = String.format("Numerator: %d \n Denominator: %d (power %d)\n Clicks per clock: %d\n 32nd per beat: %d", numerator, (int)Math.pow(2,power), power, clickperclock, thirtysecondperbeat);
                        System.out.println(timesig);
                    }

                } else {
                	System.err.println("Other: " + message.getClass());
                }
            }

            System.out.println();
        }
    }
    
    public static void printByteArray(byte[] arr) {
    	for(int i = 0; i < arr.length; i++) {
    		System.out.print(arr[i] + "  ");
    	}
    	System.out.println();
    }
    
    public static byte[] removeFirst(byte[] arr) {
    	byte[] newArr = new byte[arr.length - 1]; 
    	printByteArray(arr);
    	for(int i = 1; i < arr.length; i++) {
    		newArr[i - 1] = arr[i];
    	}
    	printByteArray(newArr);
    	return newArr;
    }
}