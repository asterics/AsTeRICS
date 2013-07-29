%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% binaryClassData2multiClassData
% Transform a data set prepared for binary classifiers into a data set prepaerd for multiclass ones.
%
%----------------------------------------------------------
%Inputs
% xTrain:	array of dimension K (K = number of classes) of structures with data set features and ground truth
%       features: EEG data as computed by the OVR procedure - matrix channels X
%                 samples X trials  ()
%       GT:       class labels for each trial - column vector 1 x trials ()
% xTest: same type of structure for the test set.
% jointClassSpaces: boolean that if set to one, joints all class spaces in
%                   a unique one.
%----------------------------------------------------------
%Outputs
% dataTrain:	array of 1 component embedding a structure with multi-class 
%               data set features and ground
%               truth (if jointClassSpaces=1, else the array presents K components as 
%               in xTrain)
%       features: EEG data as computed by the OVR procedure - matrix channels X
%                 samples X trials  ()
%       GT:       class labels from set {1...K} for each trial - column vector 1 x trials ()
% dataTest: same type of structure for the test set.
%----------------------------------------------------------
%Dependencies
%
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		18/12/09    ASF     First version
% v2        23/12/09    ASF     Adds a boolean for just generating a
%                               multi-class GT and leaving data in each
%                               class space
%----------------------------------------------------------
% EX. after running open files part of demoBciOVRfgrameworkMulticlass
% k =
% 
%      4
% 
% 
% filename =
% 
% Z:\S1-ONGOING-PROJECTS\UCONTROL-P20070527-01\BCI competition iii\OVR_aproach\features subject k3b\features4aproach1_c4.mat
% 
% 
% filename =
% 
% Z:\S1-ONGOING-PROJECTS\UCONTROL-P20070527-01\BCI competition iii\OVR_aproach\features subject k3b\test_features4aproach1_c4.mat
% 
% >> dataTrain
% 
% dataTrain = 
% 
% 1x4 struct array with fields:
%     features
%     GT
% 
% >> dataTrain(4)
% 
% ans = 
% 
%     features: [4x750x151 double]
%           GT: [1x151 double]
%  
% >> [dataTrain,dataTest]=binaryClassData2multiClassData(dataTrain,dataTest);
% >> dataTrain(1)
% 
% dataTrain(1) = 
% 
%     features: [15x750x151 double]
%           GT: [1x151 double]
%--------------------------------------------------------------
function [dataTrain,dataTest]=binaryClassData2multiClassData(xTrain,xTest,jointClassSpaces)
    if nargin<3
        jointClassSpaces=1 %%%transformation of data sets in a multi-class data structure
    end
    
    if jointClassSpaces
        %concatenate all class projection spaces
        dataTrain(1).features=cat(1,xTrain(1).features,xTrain(2).features,xTrain(3).features,xTrain(4).features);
        dataTest(1).features=cat(1,xTest(1).features,xTest(2).features,xTest(3).features,xTest(4).features);
    else
        dataTrain=xTrain;
        dataTest=xTest;
    end

    %build multiclass ground truth
    multiclassGT=cat(1,xTrain(1).GT,xTrain(2).GT,xTrain(3).GT,xTrain(4).GT);
    [multiclassGT,j,s]=find(multiclassGT); %from binary labels to integer labels
    multiclassGTtest=cat(1,xTest(1).GT,xTest(2).GT,xTest(3).GT,xTest(4).GT);
    [multiclassGTtest,j,s]=find(multiclassGTtest); %from binary labels to integer labels
    if jointClassSpaces
        dataTrain(1).GT=multiclassGT';
        dataTest(1).GT=multiclassGTtest';
    else
        for i=1:length(xTrain)
            dataTrain(i).GT=multiclassGT';
            dataTest(i).GT=multiclassGTtest';
        end
    end
