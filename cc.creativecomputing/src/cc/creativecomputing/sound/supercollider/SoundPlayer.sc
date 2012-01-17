(
x = SynthDef( "SoundPlayerMono", 
{
      arg out=0, bufnum=0, loopFlag=0, volume=1;
      var player = PlayBuf.ar(1, [bufnum, bufnum], BufRateScale.kr(bufnum), loop: loopFlag, doneAction: 2);
      Out.ar( out, player * volume);
});
y = SynthDef( "SoundPlayerStereo", 
{
      arg out=0, bufnum=0, loopFlag=0, volume=1;
      var player = PlayBuf.ar(2, bufnum, BufRateScale.kr(bufnum), loop: loopFlag, doneAction: 2);
      Out.ar( out, player * volume);
});
x.writeDefFile("/tmp/");
y.writeDefFile("/tmp/");
)

(
x.send(s);
y.send(s);
)

(
b = Buffer.read(s, "/home/robert/Desktop/one.wav");
)

(
z = Synth.new("SoundPlayerMono",["bufnum",b,"loopFlag",0,"volume",0.2]);
)

(
s.boot;
)
(
s.quit;
)


