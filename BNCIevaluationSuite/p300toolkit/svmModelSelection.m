function [svmRecallParameters]=svmModelSelection(trainingData,validationData,Cvalues)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% svmModelSelection
% Performs a selection of the parameter C and the chanels to be used by a SVM in the classification of 
% p300 training data. Function has been adapted from code provided in
% http://asi.insa-rouen.fr/~arakotom/code/bciindex.html. 
% It trains a SVM with the training data set for different values of C and validates the
% result with the validation data set. The validation is done for different subsets of
% the input channels. These subset with best performance is output as part of the SVM
% parameter set. Each of the SVMs generated this way are used as an element of a
% SVM classifier ensemble by the overall framework.
%
% [Description in A. Rakotomamonjy and V. Guigue, "BCI competition III: 
% dataset II- ensemble of SVMs for BCI P300 speller." IEEE transactions on bio-medical engineering, vol. 55, no. 3, pp. 1147-1154, 
% March 2008. [Online]. Available: http://dx.doi.org/10.1109/TBME.2008.915728 ]
%
% REQUIREMENTS:
%   - eegstarlab2RakotoTrain
%   - eegstarlab2RakotoSplit
%----------------------------------------------------------
%Inputs
% trainingData:	structure with data set signals and ground truth
%       signals: EEG data in Starlab Data Cube format - matrix channels X
%                samples X epochs  (e.g. in the paper 64 x 14 x NE)
%       GT:      ground truth for each epoch. In case of p300 data we have agreed the following
%                GT structure:
%                - First column: 0 means unattended stimuli, 1 means attended stimuli
%                - Second Column: Linear code (k) of the image shown in presentation protocol.
%                This maps into a row (i) or column (j) index through the expression:
%                   i=k if k<=R for rows, and j=R+k for columns, where R is the number of  
%                   rows and being rows indexed from left to right and columns from top to bottom.
%                - Third Column: Integer code of the attended stimuli (character/image/icon) for the duration of
%                each run. This is the code of what we are trying to detect through the
%                p300 wave analysis.
%               Data type - matrix epochs x 3 ()
% validationData: Structure with validation data set signals and ground truth. Same
%                 structure as training data set.
% Cvalues: Array of the C values (from which there are K different ones) to be tested in 
%          the SVM - row vector 1 x K (see svmclass for info on possible values)
%----------------------------------------------------------
%Outputs
% svmRecallParameters: Structure with the parameters to be used in the recall of the SVM
%                      classifier. It includes following fields:
%       channel: Array of the M channels to be used - row vector 1 x M (int)
%       xsup: Matrix of support vectors (used by svmval) -
%       w: Weighting matrix (used by svmval) -
%       b: Bias vector (used by svmval) - 
%       kernelType: Kernel type of the SVM (used by svmval) - str
%       kerneloption: Kernel option (used by svmval) - int
%       lengthperchannel
%       mnormalize: flag to determine if normalization should be applied
%       stdnormalize: type of normalization
%       typedata
%----------------------------------------------------------
%Dependencies
% 
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v0.1		20/12/10    ASF         First version only interface
% v1        07/02/11    AAE         First implementation. Tested.
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------
trainDataNoSplit = eegstarlab2RakotoTrain(trainingData);
%trainDataSplit = eegstarlab2RakotoSplit(trainingData);
validDataNoSplit = eegstarlab2RakotoTrain(validationData);
%validDataSplit = eegstarlab2RakotoSplit(validationData);
flagSplit = 0;
if flagSplit==0
    trainData = trainDataNoSplit;
    validData = validDataNoSplit;
% else
%     trainData = trainDataSplit;
%     validData = validDataSplit;
end
data.normalizationtype='normal';
data.typedata='allfilt';
data.highcutofffrequency=20;
Cvec=Cvalues; % tp
kernel='poly';
kerneloptionvec=1;
channelselection=0;   % if channel has been ranked
chanselparam.channelchoice='optimal'; % optimal selection according to criterion 'optimal' or 'adhoc'
chanselparam.nbchannel=trainData{1}.nbChannels; % number of channel to select if 'adhoc
chanselparam.criterion='tp'; % criterion for channel selection
chanselparam.channel=[1:trainData{1}.channel];
intwarning off
chanselparam.lengthperchannel=int16(length(trainData{1}.x(1,:))/length(chanselparam.channel));
intwarning on
feedData{1} = trainData{1};
feedData{2} = validData{1};
cvs = modelselclassifier4(feedData,data,Cvec,kernel,kerneloptionvec,channelselection,chanselparam);

svmRecallParameters = createclassifier3(trainData,cvs,data,channelselection,chanselparam);
