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
%----------------------------------------------------------
%Outputs
% dataTrain:	structure with multi-class data set features and ground truth
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
% >> dataTrain
% 
% dataTrain = 
% 
%     features: [15x750x151 double]
%           GT: [1x151 double]
%--------------------------------------------------------------
function [dataTrain,dataTest]=binaryClassData2multiClassData(xTrain,xTest)
    %%%transformation of data sets in a multi-class data structure
    
    %concatenate all class projection spaces
    dataTrain.features=cat(1,xTrain(1).features,xTrain(2).features,xTrain(3).features,xTrain(4).features);
    dataTest.features=cat(1,xTest(1).features,xTest(2).features,xTest(3).features,xTest(4).features);

    %build multiclass ground truth
    multiclassGT=cat(1,xTrain(1).GT,xTrain(2).GT,xTrain(3).GT,xTrain(4).GT);
    [multiclassGT,j,s]=find(multiclassGT); %from binary labels to integer labels
    multiclassGTtest=cat(1,xTest(1).GT,xTest(2).GT,xTest(3).GT,xTest(4).GT);
    [multiclassGTtest,j,s]=find(multiclassGTtest); %from binary labels to integer labels
    dataTrain.GT=multiclassGT';
    dataTest.GT=multiclassGTtest';
