#include<iostream>
#include<fstream>
#include<complex>
#include<vector>
#include<time.h>
#include"readwav.h"
#include"model.h"
#include"reshape.h"
#include "constant.h"

#include <jni.h>

#include "android-utils.h"
#include <android/log.h>




using namespace std;

const int filterNum = 26;//������Ҫ��f[];
int sampleRate = 16000;
int maxlen = 100;
int dim = 39;
#define Win_Time 0.025//��25ms������е���Ϊһ�������
#define Hop_Time 0.01//ÿ��10ms��һ��֡
#define Pi 3.1415927
int hopStep = Hop_Time * sampleRate;//��hopStep���������һ��֡


//1.Ԥ����
void pre_emphasizing(double *sample, int len, double factor, double *Sample) {
    Sample[0] = sample[0];
    for (int i = 1; i < len; i++) {
        //Ԥ���ع���
        Sample[i] = sample[i] - factor * sample[i - 1];
    }
}

void Hamming(double *hamWin, int hamWinSize) {
    for (int i = 0; i < hamWinSize; i++) {
        hamWin[i] = (double) (0.54 - 0.46 * cos(2 * Pi * (double) i / ((double) hamWinSize - 1)));
    }
}

//����ÿһ֡�Ĺ�����
void mfccFFT(double *frameSample, double *FFTSample, int frameSize, int pos) {
    //�Է�֡�Ӵ���ĸ�֡�źŽ���FFT�任�õ���֡��Ƶ��
    //���������źŵ�Ƶ��ȡģƽ���õ������źŵĹ�����
    double dataR[frameSize];
    double dataI[frameSize];
    for (int i = 0; i < frameSize; i++) {
        dataR[i] = frameSample[i + pos];
        dataI[i] = 0.0f;
    }

    int x0, x1, x2, x3, x4, x5, x6, xx, x7, x8;
    int i, j, k, b, p, L;
    float TR, TI, temp;
    /********** following code invert sequence ************/
    for (i = 0; i < frameSize; i++) {
        x0 = x1 = x2 = x3 = x4 = x5 = x6 = x7 = x8 = 0;
        x0 = i & 0x01;
        x1 = (i / 2) & 0x01;
        x2 = (i / 4) & 0x01;
        x3 = (i / 8) & 0x01;
        x4 = (i / 16) & 0x01;
        x5 = (i / 32) & 0x01;
        x6 = (i / 64) & 0x01;
        x7 = (i / 128) & 0x01;
        x8 = (i / 256) & 0x01;
        xx = x0 * 256 + x1 * 128 + x2 * 64 + x3 * 32 + x4 * 16 + x5 * 8 + x6 * 4 + x7 * 2 + x8;
        dataI[xx] = dataR[i];
    }
    for (i = 0; i < frameSize; i++) {
        dataR[i] = dataI[i];
        dataI[i] = 0;
    }

    /************** following code FFT *******************/
    for (L = 1; L <= 9; L++) { /* for(1) */
        b = 1;
        i = L - 1;
        while (i > 0) {
            b = b * 2;
            i--;
        } /* b= 2^(L-1) */
        for (j = 0; j <= b - 1; j++) /* for (2) */
        {
            p = 1;
            i = 9 - L;
            while (i > 0) /* p=pow(2,7-L)*j; */
            {
                p = p * 2;
                i--;
            }
            p = p * j;
            for (k = j; k < 512; k = k + 2 * b) /* for (3) */
            {
                TR = dataR[k];
                TI = dataI[k];
                temp = dataR[k + b];
                dataR[k] = dataR[k] + dataR[k + b] * cos(2 * Pi * p / frameSize) +
                           dataI[k + b] * sin(2 * Pi * p / frameSize);
                dataI[k] = dataI[k] - dataR[k + b] * sin(2 * Pi * p / frameSize) +
                           dataI[k + b] * cos(2 * Pi * p / frameSize);
                dataR[k + b] = TR - dataR[k + b] * cos(2 * Pi * p / frameSize) -
                               dataI[k + b] * sin(2 * Pi * p / frameSize);
                dataI[k + b] = TI + temp * sin(2 * Pi * p / frameSize) -
                               dataI[k + b] * cos(2 * Pi * p / frameSize);
            } /* END for (3) */
        } /* END for (2) */
    } /* END for (1) */
    for (i = 0; i < frameSize / 2 + 1; i++) {
        FFTSample[i + pos] = (dataR[i] * dataR[i] + dataI[i] * dataI[i]) / frameSize;
    }

}

//����˵����frameSampleΪ����֮������飬SampleΪ����������Ԥ����֮�������
//          lenΪSample�ĳ��ȣ�frameSizeΪÿ֡�������������frameSampleLenΪ����֮��ĳ���
double *mfccFrame(double *frameSample, double *Sample, int *len, int frameSize, int &frameSampleLen,
                  int frameNum) {
    double *hamWin;
    int hamWinSize = sampleRate * Win_Time;//16000*0.025
    hamWin = new double[hamWinSize];
    Hamming(hamWin, hamWinSize);//����hamming��

//	int hopStep = Hop_Time * sampleRate;
//    int frameNum = 1+ceil((double(*len)-400)/ hopStep);//����һ�����ж���
//    int frameNum00 = ceil(double(*len) / hopStep);//����һ�����ж���֡
    int frameNum00 = frameNum;

    frameSampleLen = frameNum00 * frameSize;//��������֮��ĳ���
    frameSample = new double[frameSampleLen];
    for (int i = 0; i < frameSampleLen; i++)
        frameSample[i] = 0;

    double *FFTSample = new double[frameSampleLen];
    for (int i = 0; i < frameSampleLen; i++)
        FFTSample[i] = 0;
    for (int i = 0; i < frameNum00; i++)//��֡
    {
        for (int j = 0; j < frameSize; j++) {
            if (j < hamWinSize && i * hopStep + j < *len) {
                frameSample[i * frameSize + j] = Sample[i * hopStep + j] * hamWin[j];
            } else
                frameSample[i * frameSize + j] = 0;//��0
        }

        mfccFFT(frameSample, FFTSample, frameSize, i * frameSize);
    }

//	ofstream fileFrame("D:\\CodeBlocks\\projext\\mfcc_zhou\\zhou\\bin\\Frame.txt");
//	for(int j = 0; j < frameNum; j++)
//    {
//        for(int i = 0; i < frameSize; i++)
//            fileFrame << frameSample[j * frameSize + i] << " ";
//        fileFrame<<endl;
//    }
    delete[]hamWin;
    return FFTSample;
}


void DCT(double **c, int frameNum) {
    for (int k = 0; k < frameNum; k++) {
        for (int i = 0; i < 13; i++) {
            for (int j = 0; j < filterNum; j++) {
                c[k][i] += 2 * c[k][j + 13] * cos(Pi * i * (2 * j + 1) / (2 * filterNum));
                //if(k == 0 && i ==0)
                //cout << c[0][0] << endl;
            }

            if (i == 0) {
                c[k][i] = c[k][i] * sqrt(1 / (4 * 26.0));
            } else {
                c[k][i] = c[k][i] * sqrt(1 / (2 * 26.0));
            }
        }
    }
    //cout << "c[0][0] = " << c[0][0] << endl;
}

void computeMel(double **mel, int sampleRate, double *FFTSample, int frameNum, int frameSize) {

    double f[filterNum + 2] = {0.0, 2.0, 4.0, 7.0, 10.0, 13.0, 16.0, 20.0, 24.0, 29.0, 34.0, 40.0,
                               46.0, 53.0, 60.0, 68.0, 77.0, 87.0, 97.0, 109.0, 122.0, 136.0, 152.0,
                               169.0, 188.0, 209.0, 231.0, 256.0};


    //�����ÿ�������˲��������: ��ÿһ֡���д���
    for (int i = 0; i < frameNum; i++) {
        for (int j = 1; j <= filterNum; j++) {
            double temp = 0;
            for (int z = 0; z < frameSize; z++) {
                if (z < f[j - 1])
                    temp = 0;
                else if (z >= f[j - 1] && z <= f[j])
                    temp = (z - f[j - 1]) / (f[j] - f[j - 1]);
                else if (z >= f[j] && z <= f[j + 1])
                    temp = (f[j + 1] - z) / (f[j + 1] - f[j]);
                else if (z > f[j + 1])
                    temp = 0;
                mel[i][j - 1 + 13] += FFTSample[i * frameSize + z] * temp;
            }
        }
    }
//    double meltest=mel[0][2];
//	ofstream fileMel("D:\\CodeBlocks\\projext\\mfcc_zhou\\zhou\\bin\\Mel.dat");
//	for(int j = 0; j < frameNum; j++)
//    {
//        for(int i = 0; i < filterNum; i++)
//            fileMel << mel[j][i] << " ";
//        fileMel<<endl;
//    }
//    for(int i = 0; i <= filterNum; i++)
//		fileMel << mel[0][i] << endl;

    //ȡ����
    for (int i = 0; i < frameNum; i++) {
        for (int j = 0; j < filterNum; j++) {
            if (mel[i][j + 13] <= 0.00000000001 || mel[i][j + 13] >= 0.00000000001)
                mel[i][j + 13] = log(mel[i][j + 13]);
        }
    }
}

void writeToFile(int frameNum, int frameSize, double **DCT) {
    ofstream fileDCT(Constant::ASR_BASE_PATH + "/DCT.dat");

    for (int j = 0; j < frameNum; j++)//write DCT
    {
        for (int i = 0; i < 39; i++)
            fileDCT << DCT[j][i] << " ";
        fileDCT << endl;
    }
}

//MFCC
void MFCC(double *sample, int len, double **mfcc39, int frameNum) {

    double factor = 0.97;//Ԥ���ز���
    double *Sample = new double[len];
    //1.Ԥ����
    pre_emphasizing(sample, len, factor, Sample);
    //1ms

    //Sample[len],���
//    double yujz=Sample[10];
    //�����ÿ֡�ж��ٸ��㣬Ȼ�������ӽ���ĸ�����2^k��ʹ��ÿ֡�ĵ�ĸ���Ϊ2^k���Ա���в�0
    int frameSize = (int) pow(2, ceil(log(Win_Time * sampleRate) / log(2.0)));
    double *frameSample = NULL, *FFTSample = NULL;
    int frameSampleLen;

    //��֡���Ӵ���������
    FFTSample = mfccFrame(frameSample, Sample, &len, frameSize, frameSampleLen, frameNum);

    delete[]Sample;
    delete[]frameSample;
    //FFTSample[512*frameNum]
//    double fft_test=FFTSample[256];//257-512Ϊ0

//	int frameNum = ceil(double(len) / hopStep);
//	int frameNum =1+ ceil((double(len)-Win_Time * sampleRate) / hopStep);//��������������һ���ж���֡
    //numframes = 1 + int(math.ceil((1.0*slen - frame_len)/frame_step))

//    double mel[400][26];
//    double** mel=new double*[frameNum];
//    for(int i=0;i<frameNum;i++)
//    {
//        mel[i]=new double[filterNum];
//        for(int j = 0; j < filterNum; j++)
//    		mel[i][j] = 0;
//    }
    computeMel(mfcc39, sampleRate, FFTSample, frameNum, frameSize);
    delete[]FFTSample;

    //   double **c[400][26];
//    double** c=new double*[frameNum];
//    for(int i=0;i<frameNum;i++)
//    {
//        c[i]=new double[filterNum];
//        for(int j = 0; j < filterNum; j++)
//    		c[i][j] = 0;
//    }
//    for(int i = 0; i < frameNum; i++)
//    {
//    	for(int j = 0; j < filterNum; j++)
//    		c[i][j] = 0;
//    }
    DCT(mfcc39, frameNum);
    //   delete mel;
    //   writeToFile(frameNum, frameSize, mfcc39);
    //return c;
}


extern "C" {
JNIEXPORT jstring JNICALL
Java_com_weechan_asr_Analyze_injectBasePath(
        JNIEnv *env, jclass klass, jstring basePath) {
    if (Constant::ASR_BASE_PATH.empty()) {
        Constant::ASR_BASE_PATH = jstring2charArray(env, basePath);
    }
        Constant::of = new ofstream(Constant::ASR_BASE_PATH + "/log.txt");

    *Constant::of << endl << sizeof(char);
    *Constant::of << endl << sizeof(short);
    *Constant::of << endl << sizeof(int);
    *Constant::of << endl << sizeof(long);
    *Constant::of << endl << sizeof(long long);
    *Constant::of << endl << sizeof(float);
    *Constant::of << endl << sizeof(double);
    *Constant::of << endl << sizeof(unsigned char);
    *Constant::of << endl << sizeof(unsigned short);
    *Constant::of << endl << sizeof(unsigned int);
    *Constant::of << endl << sizeof(unsigned long);
    *Constant::of << endl << sizeof(unsigned long long);
    *Constant::of << endl << sizeof(float);
    *Constant::of << endl << sizeof(double);

    return (*env).NewStringUTF(Constant::ASR_BASE_PATH.c_str());
}
}


extern "C" {
JNIEXPORT void JNICALL
Java_com_weechan_asr_Analyze_analyze(JNIEnv *env, jclass klass, jstring jpath) {
    //����WAV�ļ���ַ
    string path = jstring2charArray(env, jpath);
    unsigned long wavdata_l;
    double *sample = NULL;
    sample = readwav(path, &wavdata_l);
    *Constant::of << path << endl << flush;
    *Constant::of << "wavdata_l: " << wavdata_l << endl << flush;
    int frameNum = 1 + ceil((double(wavdata_l) - Win_Time * sampleRate) / hopStep);//��������������һ���ж���֡
    int frame_new = (ceil(frameNum / (maxlen / 2 * 1.0)) - 1) * maxlen;
    double **mfcc39 = new double *[frameNum];

    for (int i = 0; i < frameNum; i++) {
        mfcc39[i] = new double[39];
        for (int j = 0; j < 39; j++)
            mfcc39[i][j] = 0;
    }
//9ms
    clock_t startTime = clock();
    MFCC(sample, wavdata_l, mfcc39, frameNum);
    clock_t endTime = clock();
    cout << "time is : " << endTime - startTime << "ms" << endl;
    //741ms

    delete[]sample;
    int overlap = maxlen / 2;
    int num_100 = ceil(frameNum / (overlap * 1.0)) - 1;
    int test_num = num_100 * maxlen;
    double **xtest = new double *[test_num];
    for (int i = 0; i < test_num; i++) {
        xtest[i] = new double[39];
        for (int j = 0; j < 39; j++)
            xtest[i][j] = 0;
    }
    reshape(mfcc39, maxlen, dim, frameNum, xtest);
    //�ͷ�
    for (int i = 0; i < frameNum; i++) {
        delete[] mfcc39[i];
    }
    delete[] mfcc39;


    //==========================================
    int *label = new int[frameNum];

    label = model(xtest, frameNum, frame_new, label);
    //�ͷ�
    for (int i = 0; i < test_num; i++) {
        delete[] xtest[i];
    }
    delete[] xtest;

    //10175ms
    for (int i = 0; i < 30; i++) {
        cout << "��" << i + 1 << "֡:  " << label[i] << '\n';
    }
    //=================================
    cout << wavdata_l << endl;
//	cout<<xtest[0][0];
    vector<int> newlist;//���������
    vector<int> pointlist;//��¼ÿ�����شӵڼ��������㿪ʼ
    trimming(newlist, pointlist, label, frameNum, hopStep);
    //10611ms

    delete[]label;
    int phone_num = newlist.size();
    //�����д��txt�ļ���
    ofstream fileout(Constant::ASR_BASE_PATH + "/output.txt");
    for (int i = 0; i < phone_num; i++) {
        fileout << newlist[i] << ',';
        fileout << pointlist[i] << endl;
    }
    fileout.close();

    newlist.clear();
    pointlist.clear();
    //10919ms
}

}






