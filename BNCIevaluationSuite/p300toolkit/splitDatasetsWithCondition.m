function [trainingData,validationData]=splitDatasetsWithCondition(data,GTcondition,spellerMatrixNumberElements)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% splitDatasetsWithCondition
% Performs split of p300 data as described in Rakotomnamonjy and Guige 2008
% paper. Function has been adapted from code provided in
% http://asi.insa-rouen.fr/~arakotom/code/bciindex.html. 
% It splits the data in a training
% and a validation data sets. The training data set is formed by those points in the input
% data set, which were acquired when N (length of GTcondition) different p300 matrix 
% symbols were attended.
% The generation of the validation data set, which is described in one of the appendices
% of the paper cited below, is based on an implicit splitting of the data set in two
% groups. These two groups are formed by the first and second halves of the speller
% matrix. If the N characters are in the first/second halft, the validation is formed by points
% corresponding to symbols in this first/second halft and not included in the training set. 
% Second halft includes more points if spellerMatrixNumberElements is odd (TBC in code).
%
% [Description in A. Rakotomamonjy and V. Guigue, "BCI competition III: 
% dataset II- ensemble of SVMs for BCI P300 speller." IEEE transactions on bio-medical engineering, vol. 55, no. 3, pp. 1147-1154, 
% March 2008. [Online]. Available: http://dx.doi.org/10.1109/TBME.2008.915728 ]
%----------------------------------------------------------
%Inputs
% data:	structure with data set signals and ground truth
%       signals: EEG data in Starlab Data Cube format - matrix channels X
%                samples X epochs  (e.g. in the paper 64 x 14 x 15300)
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
% GTcondition: Array of the integer codes of the data points to be included in the
%               training set - row vector 1 x N
% spellerMatrixNumberElements: Number of elements of the speller matrix - int
%----------------------------------------------------------
%Outputs
% trainingData: Structure with training data set signals and ground truth. 
%               Same structure as input data (e.g. for Rakotomamonjy paper matrix 
%               64 x 14 x 6300 or 64 x 14 x 7200)
% validationData: Structure with validation data set signals and ground truth.
%----------------------------------------------------------
%Dependencies
% 
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v0.1		01/12/10    ASF         First version only interface
% v1        04/02/11    AAE         First implementation. 
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------
% 
trainingData = [];
validationData = [];
% first we see how many elements we have, and we split the elements in two
% groups:
intwarning off
group1 = [int16(1:spellerMatrixNumberElements/2)];
group2 = [int16((spellerMatrixNumberElements/2)+1):spellerMatrixNumberElements];
intwarning on
selectedGroup = [];
% check if the stimuly seek are from group1 or group2
if ~isempty(find(GTcondition(1)==group1, 1))
    selectedGroup=group1;
else
    selectedGroup=group2;
end
% iterate trought stimuly
trainStInd = [];
valStInd = [];
for i=1:length(selectedGroup)
    % if its a selected stimuly
    if ~isempty(find(GTcondition==selectedGroup(i), 1))
        trainStInd = [trainStInd selectedGroup(i)];
    else
        valStInd = [valStInd selectedGroup(i)];
    end
end
% iterate through train stimuly
trainInd = [];
for i=1:length(trainStInd)
    trainInd = [trainInd find(data.GT(:,3)==trainStInd(i))];
end
% iterate through validate stimuly
valInd = [];
for i=1:length(valStInd)
    valInd = [valInd find(data.GT(:,3)==valStInd(i))];
end
trainingData.signal = data.signal(:,:,trainInd);
trainingData.GT = data.GT(trainInd,:);
validationData.signal = data.signal(:,:,valInd);
validationData.GT = data.GT(valInd,:);
