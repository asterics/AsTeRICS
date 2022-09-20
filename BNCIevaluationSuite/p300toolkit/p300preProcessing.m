function [preProcessedData]=p300preProcessing(data,decimationFactor)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% p300preProcessing
% Performs pre-processing of p300 data as described in Rakotomnamonjy and Guige 2008
% paper. Function has been adapted from code provided in
% http://asi.insa-rouen.fr/~arakotom/code/bciindex.html. It implements basically 3 stages:
% 1. Windowing in 667 ms temporal sequences
% 2. BPF between 0.1 and 10 Hz
% 3. Decimation by a factor of 12 (that delivers sequences of length 14 samples)
%
% [Description in A. Rakotomamonjy and V. Guigue, "BCI competition III: 
% dataset II- ensemble of SVMs for BCI P300 speller." IEEE transactions on bio-medical engineering, vol. 55, no. 3, pp. 1147-1154, 
% March 2008. [Online]. Available: http://dx.doi.org/10.1109/TBME.2008.915728 ]
%----------------------------------------------------------
%Inputs
% data:	structure with data set signals and ground truth
%       signals: EEG data in Starlab Data Cube format - matrix channels X
%                samples X epochs  (e.g. in the paper 64 x N x 15300)
%       GT:      ground truth for each epoch. In case of p300 data we have agreed the following
%                GT structure:
%                - First column: 0 means unattended stimuli, 1 means attended stimuli
%                - Second Column: Linear code (k) of the image shown in presentation protocol.
%                This maps into a row (i) or column (j) index through the expression:
%                   i=k if k<=R for rows, and j=R+k for columns, where R is the number of  
%                   rows and being rows indexed from left to right and columns from top to bottom.
%                - Third Column: Code of the attended stimuli (character/image/icon) for the duration of
%                each run. This is the code of what we are trying to detect through the
%                p300 wave analysis.
%               Data type - matrix epochs x 3 ()
% decimationFactor: Factor to be applied in the decimation. In the Rakotomamonjy code this
%                   is 12, which delivers sequences of 14 samples as output of this function
%----------------------------------------------------------
%Outputs
% preProcessedData: structure with data set signals and ground truth after preprocessing
%                   the signals. Same structure as input data (e.g. for Rakotomamonjy paper matrix 64 x 14 x 15300) 
%----------------------------------------------------------
%Dependencies
% 
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v1		01/12/10    ASF         First version only interface
% v1        04/02/11    AAE         First implementation. Tested. 
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------
firstDecimationFactor = 8;
freq=2048.0;
freqOrig = freq;
freq = freq / firstDecimationFactor;
lowcut = 0.1;
highcut = 20;
R=0.5;
W = [lowcut*2/freq highcut*2/freq];
order = 4;
[b,a] = cheby1(order,R,W);
dimensions = size(data.signal);
nEpochs = dimensions(3);
nSamples = dimensions(2);
nChannels = dimensions(1);
for i=1:nEpochs
    for j=1:nChannels
        sample = data.signal(j,:,i);
        % CUT: sample starts at -0.5 segs. We want segment from 0-0.667 seg
        intwarning off
        sample = sample(0.5*freqOrig:int16(((0.5*freqOrig)+(0.667*freqOrig))));
        intwarning on
        % DECIMATE:
        sample = decimate(double(sample),firstDecimationFactor);
        % FILTER:
        sample = filter(b,a,sample);
        % DECIMATE:
        sample = decimate(double(sample),decimationFactor);
        preProcessedData.signal(j,:,i) = sample;
    end
end
preProcessedData.GT = data.GT;