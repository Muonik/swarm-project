// import javax.sound.midi.*;
// import javax.sound.sampled.*;
// import java.io.File;
// import java.io.IOException;
// import javax.swing.*;
// import java.awt.event.*;
// import java.awt.Dimension;

// public class MakeMusic {


// }
// // 	private static int instrument = 0;
// // 	public static File recording = new File("C:/Users/muriel/Desktop/testRecording.wav");
// // 	public static AudioFileFormat.Type fileType = AudioFileFormat.Type.WAVE;
// // 	public static TargetDataLine line;

// // 	public static void playNote() throws MidiUnavailableException, InvalidMidiDataException{
// // 		Synthesizer synth = MidiSystem.getSynthesizer();
// // 		synth.open();
// // 		// Receiver recv = synth.getReceiver();
// // 		// ShortMessage myMsg = new ShortMessage();
// // 		// long timeStamp = -1;

// // 		MidiChannel[] channels = synth.getChannels();
// // 		//Instrument[] instr = synth.getDefaultSoundbank().getInstruments();

// // 		channels[7].programChange(0,1); //bank, preset of the instrument 
// // 		channels[7].noteOn(440, 90); //pitch, volume

// // 		// myMsg.setMessage(ShortMessage.NOTE_ON, 0, 52, 120);
// // 		// recv.send(myMsg, timeStamp);
// // 		// try {
// // 		// 	Thread.sleep(100);
// // 		// } catch (InterruptedException e) {}
// // 		// myMsg.setMessage(ShortMessage.NOTE_OFF, 0, 52, 0);
// // 		// recv.send(myMsg, timeStamp);
// // 		// try {
// // 		// 	Thread.sleep(1000);
// // 		// } catch (InterruptedException e) {}

// // 		//synth.close();
// // 	}

// // 	public static void playSample() throws InvalidMidiDataException{
// // 		File audioFile = new File("C:/Users/muriel/Desktop/swarm/slowly but surely.wav");

// // 		try {
// // 			AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
// // 			AudioFormat format = audioStream.getFormat();
// // 			DataLine.Info info = new DataLine.Info(Clip.class, format);
// // 			Clip audioClip = (Clip) AudioSystem.getLine(info);
// // 			audioClip.open(audioStream);
// // 			audioClip.start();           
// // 			try {
// // 				Thread.sleep(2000);
// // 			} catch (InterruptedException ex) {
// // 				ex.printStackTrace();
// // 			}

// // 			audioClip.close();
// // 		}

// // 		catch (UnsupportedAudioFileException ex) {
// // 			System.out.println("The specified audio file is not supported.");
// // 			ex.printStackTrace();
// // 		} catch (LineUnavailableException ex) {
// // 			System.out.println("Audio line for playing back is unavailable.");
// // 			ex.printStackTrace();
// // 		} catch (IOException ex) {
// // 			System.out.println("Error playing the audio file.");
// // 			ex.printStackTrace();
// // 		}

// // 	}
// // 		// try {
// // 		// 	AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("slowly but surely.wav"));
// // 		// 	Clip clip = AudioSystem.getClip();
// // 		// 	clip.open(audioInputStream);
// // 		// 	clip.start();
// // 		// 	Thread.sleep(1000);
// // 		// 	clip.stop();
// // 		// } catch (Exception ex) {
// // 		// 	ex.printStackTrace();
// // 		// }


// // 	public static void main (String[] args) {
// // 		final JFrame frame = new JFrame("my sole life purpose is playing a note");
// // 		JPanel panel = new JPanel();
// // 		JButton midiButton = new JButton("click to die instantly");
// // 		panel.add(midiButton);
// // 		JButton sampledButton = new JButton("click to die slowly but surely");
// // 		panel.add(sampledButton);
// // 		// JLabel label = new JLabel("instrument "+ instrument);
// // 		// panel.add(label);
// // 		JButton recordButton = new JButton("record");
// // 		panel.add(recordButton);
// // 		JButton stopButton = new JButton("stop");
// // 		panel.add(stopButton);

// // 		frame.setPreferredSize(new Dimension(600, 300));
// // 		frame.getContentPane().add(panel);
// // 		frame.pack();
// // 		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
// // 		frame.show();

// // 		midiButton.addActionListener(new ActionListener(){
// // 			public void actionPerformed(ActionEvent event) {
// // 				try{
// // 					playNote();
// // 					//instrument++;
// // 				}
// // 				catch(MidiUnavailableException e){}
// // 				catch(InvalidMidiDataException e){}
// // 			}		
// // 		});

// // 		sampledButton.addActionListener(new ActionListener(){
// // 			public void actionPerformed(ActionEvent event) {
// // 				try{
// // 					playSample();
// // 				}
// // 				catch(InvalidMidiDataException e){}
// // 			}		
// // 		});

// // 		recordButton.addActionListener(new ActionListener(){
// // 			public void actionPerformed(ActionEvent event) {
// // 				try {
// // 					AudioFormat format = new AudioFormat(16000, 8, 2, true, true);
// // 					DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

// // 					if (!AudioSystem.isLineSupported(info)) {System.exit(0);}
// // 					line = (TargetDataLine) AudioSystem.getLine(info);
// // 					line.open(format);
// // 					line.start();   // start capturing

// // 					AudioInputStream ais = new AudioInputStream(line);
// // 					AudioSystem.write(ais, fileType, recording);

// // 				} catch (LineUnavailableException ex) {
// // 					ex.printStackTrace();
// // 				} catch (IOException ioe) {
// // 					ioe.printStackTrace();
// // 				}
// // 			}
// // 		});

// // 		stopButton.addActionListener(new ActionListener(){
// // 			public void actionPerformed(ActionEvent event) {
// // 				line.stop();
// // 				line.close();
// // 			}
// // 		});



// // 	}

// // }