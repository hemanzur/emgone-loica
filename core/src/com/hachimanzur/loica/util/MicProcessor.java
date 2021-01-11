package com.hachimanzur.loica.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.AudioRecorder;

import java.util.Collections;
import java.util.LinkedList;

import uk.me.berndporr.iirj.Butterworth;


public class MicProcessor {

    private AudioRecorder recorder;
    private int micDataLength;
    private short[] micData;
    private LinkedList<Float> peaks;
    private float[] absoluteMicData;
    private LinkedList<Float> historicMean;
    private int historicMeanWindow;

    private Butterworth bw1 = new Butterworth();
    private Butterworth bw2 = new Butterworth();
    private Butterworth bw3 = new Butterworth();
    private Butterworth bw4 = new Butterworth();
    private Butterworth bw5 = new Butterworth();
    private Butterworth bw6 = new Butterworth();
    private Butterworth bw7 = new Butterworth();
    private Butterworth bw8 = new Butterworth();
    private Butterworth bwLP = new Butterworth();
    private Butterworth bwHP = new Butterworth();
    private int filtOrd = 4;
    private float filtWidth = (float) 10.0;
    private int SampleRate = 44100;

    public MicProcessor() {
        recorder = Gdx.audio.newAudioRecorder(SampleRate, true);
        micDataLength = 1024;
        micData = new short[micDataLength];
        absoluteMicData = new float[micDataLength];
        historicMean = new LinkedList<Float>();
        historicMeanWindow = 30;

        bw1.bandStop(filtOrd, SampleRate, 50.0, filtWidth);
        bw2.bandStop(filtOrd, SampleRate, 100.0, filtWidth);
        bw3.bandStop(filtOrd, SampleRate, 150.0, filtWidth);
        bw4.bandStop(filtOrd, SampleRate, 200.0, filtWidth);
        bw5.bandStop(filtOrd, SampleRate, 250.0, filtWidth);
        bw6.bandStop(filtOrd, SampleRate, 300.0, filtWidth);
        bw7.bandStop(filtOrd, SampleRate, 350.0, filtWidth);
        bw8.bandStop(filtOrd, SampleRate, 400.0, filtWidth);
        bwLP.lowPass(filtOrd, SampleRate, 400.0);
        bwHP.highPass(filtOrd, SampleRate, 2.0);
    }

    public float getMovingAverage() {
        retrieveMicData();

        // Get peaks
        peaks = getPeaks(absoluteMicData);

        if (peaks.size() <=1) {
            if (historicMean.size() == 0) return 0;
            return historicMean.get(historicMean.size()-1);
        }

        float median = getMedian(peaks);

        // Calculates movingAverage

        historicMean.offer(median);
        if (historicMean.size() >  historicMeanWindow ) {
            historicMean.poll();
        }
        float sum = 0;
        for(float value : historicMean) {
            sum += value;
        }
        float movAUX = sum/historicMean.size();

        //10.000 - 21.000 para ejercicio apretar puño con disp1
        //15.000 - 24.000 para ejercicio levantar peso con brazo estirado con disp1
        //800 - 10.000 para audio hablado y soplar, maximo hablado 7k, soplando 10k
        return movAUX;
    }

    private void retrieveMicData() {
        // micData is populated
        recorder.read(micData, 0, micData.length);

        // signal's absolute value. Short datatype's range: [-32768, 32767]
        for (int i = 0; i < micDataLength; i++) {

            absoluteMicData[i] = (float)Math.abs(bwHP.filter(bwLP.filter(bw8.filter(bw7.filter(bw6.filter(bw5.filter(bw4.filter(bw3.filter(bw2.filter(bw1.filter(micData[i])))))))))));

            //absoluteMicData[i] = Math.abs(micData[i]);
        }
    }

    private float getMedian(LinkedList<Float> data) {

        int dataLength = data.size();

        Collections.sort(data);

        if (dataLength % 2 == 0) {
            return (data.get(dataLength/2) + data.get(dataLength/2 - 1)) / 2;
        }
        return data.get(dataLength/2);

        //Si se quiere usar promedio
        /*
        float sum = 0;
        for (float value:data) {
            sum+=value;
        }
        return sum/data.size();
        */
    }

    private LinkedList<Float> getPeaks(float[] data){
        LinkedList<Float> aux = new LinkedList<Float>();
        for(int i = 1; i < data.length - 1; i++) {
            if (data[i - 1] < data[i] && data[i] > data[i+1]) aux.offer(data[i]);
        }
        return aux;
    }

    public void dispose() {
        recorder.dispose();
    }
}
