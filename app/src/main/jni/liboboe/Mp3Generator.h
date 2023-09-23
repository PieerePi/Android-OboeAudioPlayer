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

#ifndef SAMPLES_MP3GENERATOR_H
#define SAMPLES_MP3GENERATOR_H


#include "shared/Oscillator.h"
#include "shared/TappableAudioSource.h"
#include "../libmusicdecoder/accompany_decoder_controller.h"

/**
 * Generates a fixed frequency tone for each channel.
 * Implements RenderableTap (sound source with toggle) which is required for AudioEngines.
 */
class Mp3Generator : public TappableAudioSource {
    static constexpr size_t kSharedBufferSize = 1024;
public:
    /**
     * Create a new Mp3Generator object.
     *
     * @param sampleRate - The output sample rate.
     * @param maxFrames - The maximum number of audio frames which will be rendered, this is used to
     * calculate this object's internal buffer size.
     * @param channelCount - The number of channels in the output, one tone will be created for each
     * channel, the output will be interlaced.
     *
     */
    Mp3Generator(std::string mp3file, int32_t sampleRate, int32_t channelCount);
    ~Mp3Generator();

    Mp3Generator(Mp3Generator&& other) = default;
    Mp3Generator& operator= (Mp3Generator&& other) = default;

    // Switch the tones on
    void tap(bool isOn) override;

    void renderAudio(void *audioData, int32_t numFrames) override;

private:
    std::string mp3file;
    AccompanyDecoderController *decoderController;
    int packetBufferSize;
    std::unique_ptr<short[]> mBuffer;
    int mCurLen;
    int mCurPos;
    bool mPlayDone;
//    std::unique_ptr<Oscillator[]> mOscillators;
//    std::unique_ptr<float[]> mBuffer = std::make_unique<float[]>(kSharedBufferSize);
};


#endif //SAMPLES_MP3GENERATOR_H
