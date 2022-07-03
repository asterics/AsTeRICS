%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% baggingFusion
% Applies the bagging to input data. After splitting this data, it returns
% the prediction for train and test. The bagging fusion is given for
% majority voting and mean operators.
%
% [B:\STAR2STAR - Internal\TN Technical Notes (TB+KN)\TN00146 - BCI Classification - OVR aproach]
%----------------------------------------------------------
%Inputs
% data:	structure with data set features and ground truth
%       features: EEG data as computed by the OVR procedure - matrix channels X
%                 samples X trials  ()
%       GT:       class labels for each trial - column vector 1 x trials ()
% testData: same type of structure for the test set.
% baggingRuns: number of times bagging is repeated - integer
% homogeneousBagging: select homogeneous bagging data sets
%----------------------------------------------------------
%Outputs
% trainPrediction: prediction for the training set - matrix 2 X samples X
%                   trials
%                   majority voting result is in (1,:,:)
%                   mean fusion is in (2,:,:)
% testPrediction: prediction for the test set - matrix 2 X samples X
%                   trials
%                   majority voting result is in (1,:,:)
%                   mean fusion is in (2,:,:)
%----------------------------------------------------------
%Dependencies
% svmclass, svmval: SVM-KM (./SVM_KM)
% measurePerformance, starClassify, starFusion, starDecimate, joinDataSets: EEGStarlab (TBD)
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		28/11/08    ASF     First version based on adjustBaggingFusion.m
% v2        04/12/08    ASF     Changing input interface to a train and
%                               test sets
% v3        11/08/09    ASF     1. Adding recall with the part of the training
%                               data set that is not used for training due
%                               to bagging.
%                               2. Returning the bagging fusion prediction
%                               with NaNs in case there are any in the
%                               input data
%----------------------------------------------------------
% EX.
% >> load '\\PHACT\data\UCONTROL\BCI competition iii\OVR_aproach\features subject k3b\features4aproach1_c4.mat'
% >> data
% 
% data = 
% 
%     features: [60x750x180 double]
%           GT: [1x180 double]
% 
% >> [trainPrediction,testPrediction]=baggingFusion(data,testData)
%--------------------------------------------------------------
function [trainBaggingPrediction,testBaggingPrediction]=baggingFusion(data,testData,baggingRuns,homogeneousBagging,standardizeClassifiers)
if nargin<4
    homogeneousBagging=1;
end
if nargin<5
    standardizeClassifiers=0;
end
%we remove any non-finite value:
indNanTrain=find(any(any(isnan(data.features))));
data.features(:,:,indNanTrain)=[];
data.GT(indNanTrain)=[];
indNanTest=find(any(any(isnan(testData.features))));
testData.features(:,:,indNanTest)=[];
testData.GT(indNanTest)=[];

%we ensure the ground truth is in the right format (this applies if GT is coded as labels in {0,1},
%if it is not the case, has no effect):
data.GT(~data.GT)=-1;
testData.GT(~testData.GT)=-1;

%parameters
if nargin<3
    baggingRuns=9;
end
decimateRatio=25;
decisionThreshold=0;


tic
for k=1:baggingRuns

    %re-splitting training data in train and test sets, where the resulting
    %training set is the one used for training the classifierrs and the
    %test data from here is not used at all
    [xTrain,xTest,indTest]=splitDataset(data,.8,homogeneousBagging);
    indTrain=setdiff(1:length(data.GT),indTest)';
    disp(['Bagging run ',num2str(k), ' of ', num2str(baggingRuns),', to evaluate framework'])   

    %SVM, KNN, LDA classifier module
    [borderIndex,testDataAll]=joinDataSets(testData,xTest);     %xTest (which does not take part in training)
                                                             %is added to
                                                             %test data set
    [yTrain4Fusion,yDataStar]=starClassify(xTrain,testDataAll); %data is only used for recalling 
                                                          %(and xTrain2 for training)
    if standardizeClassifiers
        yTrain4Fusion=standardizeData(yTrain4Fusion);
        yDataStar=standardizeData(yDataStar);
    end
    
    %min, max, majority voting, median, average, product, sum fusion
    [trainFusedStar,listFusion]=starFusion(yTrain4Fusion,xTrain.GT, yTrain4Fusion);
    [dataFusedStar,listFusion]=starFusion(yDataStar, xTrain.GT, yTrain4Fusion);

    %decimation
    trainFaDStar=starDecimate(trainFusedStar,decimateRatio);
    testFaDStar=starDecimate(dataFusedStar,decimateRatio);

    %using the test indices for defining result of train and test data sets
    trainPredictionIteration=zeros(length(data.GT),size(trainFaDStar,2));
    trainPredictionIteration(indTrain,:)=trainFaDStar;
    trainPredictionIteration(indTest,:)=testFaDStar(borderIndex:end,:); %adding again the part that ended up in test set
    testFaDStar(borderIndex:end,:)=[]; %deleting trials that come from the training set
    testDataAll=[];
    
    %saving results for bagging
    trainPrediction(:,:,k)=trainPredictionIteration;
    testPrediction(:,:,k)=testFaDStar;

end%for all the bagging iterations

%fusing the bagging results with majority voting
trainPrediction=permute(trainPrediction,[3,2,1]);
trainBaggingPrediction(1,:,:)=starFusion(trainPrediction,[],[],{'majorVoting'},0);
testPrediction=permute(testPrediction,[3,2,1]);
testBaggingPrediction(1,:,:)=starFusion(testPrediction,[],[],{'majorVoting'},0);

%fusing the bagging results with average fusion
trainPrediction=mean(trainPrediction,1);
trainBaggingPrediction(2,:,:)=trainPrediction;
testPrediction=mean(testPrediction,1);
testBaggingPrediction(2,:,:)=testPrediction;

%%%%%%%restoring the NaNs to the trial indexes where they were
if ~isempty(indNanTrain)
    for j=1:length(indNanTrain)
        i=indNanTrain(j)
        aux=trainBaggingPrediction(:,:,i:end);
        trainBaggingPrediction(:,:,i)=NaN;
        trainBaggingPrediction(:,:,i+1:i+size(aux,3))=aux;
    end
    disp('There are projected epochs with NaNs in the training set')
end
if ~isempty(indNanTest)
    for j=1:length(indNanTest)
        i=indNanTest(j)
        aux=testBaggingPrediction(:,:,i:end);
        testBaggingPrediction(:,:,i)=NaN;
        testBaggingPrediction(:,:,i+1:i+size(aux,3))=aux;
    end
    testBaggingPrediction(:,:,indNanTest)=NaN;
    disp('There are projected epochs with NaNs in the training set')
end


end%all