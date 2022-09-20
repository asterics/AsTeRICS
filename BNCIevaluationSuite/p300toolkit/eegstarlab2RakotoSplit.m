function convertedData = eegstarlab2RakotoSplit(data,decimationFactor, options)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% eegstarlab2Rakoto
% Performs a format conversion between EEGStarlab toolbox and Rakotomamonjy
% code formats. This function is only used for Anton's internal tests (change according in
% svmMoedlSelection)
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
% convertedData: structure with data set signals and ground truth after
% preprocessing and converted to Rakotomamonjy's code format
%                
%----------------------------------------------------------
%Dependencies
% 
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v1		2011/01/11   AAE         First version
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------

dimensions = size(data.signal);
nSamples = dimensions(2);
nChannels = dimensions(1);
% WE NEED TO DECIMATE THE SIGNAL, AS IT'S TOO LONG
firstDecimationFactor = 8;
freq=2048.0;
freq = freq / firstDecimationFactor;
lowcut = 0.1;
highcut = 20;
R=0.5;
W = [lowcut*2/freq highcut*2/freq];
order = 4;
[b,a] = cheby1(order,R,W);
gradient = [1 find(diff(data.GT(:,3)')~=0)+1];
nEpochs = length(gradient);
indexes = [gradient, dimensions(3)+1];
fooData = [];
fooGT = [];
dictionary = ['ABCDEFGHIJKLMNOPQRSTUVWXYZ'];
for i=1:nEpochs
    x = [];
    % follow from index(i) to index(i+1) and extract samples
    for k=indexes(i):indexes(i+1)-1
        xaux = [];
        for j=1:nChannels
            xaux = [xaux,double(data.signal(j,:,k))];    
        end
        x = [x;xaux];
    end
    oneGT=data.GT(indexes(i):indexes(i+1)-1,1);
    oneCode=data.GT(indexes(i):indexes(i+1)-1,2);
    oneTarget=dictionary(data.GT(indexes(i),3));
    
    % need to create a cell array, on cell for each epoch
    fooData{i}.x = x;
    fooData{i}.y = oneGT*2-1;
    fooData{i}.code=oneCode;
    fooData{i}.target=oneTarget;
    fooData{i}.nbChannels = nChannels;
    fooData{i}.channel = [1:nChannels];
    fooData{i}.triallength = 49;
end
convertedData = fooData;