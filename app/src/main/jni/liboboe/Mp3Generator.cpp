/*
 * Copyright 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include "Mp3Generator.h"
#include "shared/logging_macros.h"

Mp3Generator::Mp3Generator(std::string mp3file, int32_t sampleRate, int32_t channelCount) :
        TappableAudioSource(sampleRate, channelCount) {

    this->mp3file = std::string(mp3file);
    decoderController = new AccompanyDecoderController();
    int* metaData = new int[2];
    decoderController->getMusicMeta(mp3file.c_str(), metaData);
    this->mSampleRate = metaData[0];
    delete[] metaData;
    packetBufferSize = 2 * this->mSampleRate * 2 * 0.2f;
    mBuffer = std::make_unique<short[]>(packetBufferSize);
    mCurLen = 0;
    mCurPos = 0;
    mPlayDone = false;
    decoderController->init(mp3file.c_str(), 0.2f, channelCount);
}

Mp3Generator::~Mp3Generator() {
    LOGD("I am here, ~Mp3Generator");
    if (NULL != decoderController) {
        decoderController->destroy();
        delete decoderController;
        decoderController = NULL;
    }
}

void Mp3Generator::renderAudio(void *audioData, int32_t numFrames) {
    short *outputBuffer = static_cast<short *>(audioData);
    if (mCurLen < numFrames * mChannelCount && !mPlayDone) {
//        LOGD("I am here, %d, %d:%d, %d:%d", numFrames, mCurPos, mCurLen, mSampleRate, mChannelCount);
        if (mCurLen > 0 && mCurPos > 0) {
            memcpy(&mBuffer[0], &mBuffer[mCurPos], sizeof(short) * mCurLen);
        }
        mCurPos = 0;
        if (NULL != decoderController) {
            int result = decoderController->readSamples(&mBuffer[mCurLen], packetBufferSize / 2,
                                                        NULL);
            if (0 < result) {
                mCurLen += result;
            } else if (result == -1) {
                // decoderController->readSamples会调用packetPool->getDecoderAccompanyPacket(&accompanyPacket, true);
                // 阻塞等待新包，result == -1表明读取完成了
                // 后续再decoderController->readSamples，会阻塞
                // 这样mAudioSource的引用计数在oboe stream里面还有（renderAudio是在oboe stream的onAudioReady回调上下文里面执行的）
                // Mp3OboeEngine::stop中，mAudioSource中计数不为0，无法真正去销毁decoderController，stream无法close，两者死锁了
                mPlayDone = true;
            }
        }
    }
    if (mCurLen >= numFrames * mChannelCount && !mPlayDone) {
//        LOGD("I am here 1, %d, %d:%d, %d:%d", numFrames, mCurPos, mCurLen, mSampleRate, mChannelCount);
        memcpy(outputBuffer, &mBuffer[mCurPos], sizeof(short) * numFrames * mChannelCount);
        mCurLen -= numFrames * mChannelCount;
        mCurPos += numFrames * mChannelCount;
    } else {
//        LOGD("I am here 2, %d, %d:%d, %d:%d", numFrames, mCurPos, mCurLen, mSampleRate, mChannelCount);
        memset(outputBuffer, 0, sizeof(short) * numFrames * mChannelCount);
    }
}

void Mp3Generator::tap(bool isOn) {
//    for (int i = 0; i < mChannelCount; ++i) {
//        mOscillators[i].setWaveOn(isOn);
//    }
}
