%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% adjustBaggingFusion
% Applies the bagging to what in adjust_fusion3 were the cross-fold
% validation runs. It returns the performance measure after bagging. You
% can plot them by using plotClassifiersPerformance1.m.

% [B:\STAR2STAR - Internal\TN Technical Notes (TB+KN)\TN00146 - BCI Classification - OVR aproach]
%----------------------------------------------------------
%Inputs
% data:	structure with data set features and ground truth
%       features: EEG data as computed by the OVR procedure - matrix channels X
%                 samples X trials  ()
%       GT:       class labels for each trial - column vector 1 x trials ()
%----------------------------------------------------------
%Outputs
% performMeasures:	TPR and FPR of train and test for different stages of
% the system - Structure of double matrices 1 x number of CFV runs
% performMeasures = 
% 
%         TPRtrain: [1x10 double]
%         FPRtrain: [1x10 double]
%          TPRtest: [1x10 double]
%          FPRtest: [1x10 double]
%     TPRtrainFuse: [1x10 double]
%     FPRtrainFuse: [1x10 double]
%      TPRtestFuse: [1x10 double]
%      FPRtestFuse: [1x10 double]
%      TPRtrainSVM: [1x10 double]
%      FPRtrainSVM: [1x10 double]
%       TPRtestSVM: [1x10 double]
%       FPRtestSVM: [1x10 double]
%      TPRtrainKNN: [1x10 double]
%      FPRtrainKNN: [1x10 double]
%       TPRtestKNN: [1x10 double]
%       FPRtestKNN: [1x10 double]
%      TPRtrainLDA: [1x10 double]
%      FPRtrainLDA: [1x10 double]
%       TPRtestLDA: [1x10 double]
%       FPRtestLDA: [1x10 double]
% TPRtrainBaggMean: double
% FPRtrainBaggMean: double
%  TPRtestBaggMean: double
%  FPRtestBaggMean: double
%   TPRtrainBaggMV: double
%   FPRtrainBaggMV: double
%    TPRtestBaggMV: double
%    FPRtestBaggMV: double
%----------------------------------------------------------
%Dependencies
% svmclass, svmval: SVM-KM (./SVM_KM)
% measurePerformance, starClassify, starFusion, starDecimate: EEGStarlab (TBD)
%----------------------------------------------------------
% Version	Date		Author	Changes 
% v1		21/10/08    ASF     First version based on adjust_fusion3.m
% v2        01/12/08    ASF     A bug was detected in the computation of
%                               trainBaggingPrediction and
%                               testBaggingPrediction. Hence the indices of
%                               the test and train sets in the original
%                               data were not kept after splitData and
%                               therefore the fusion was done as if
%                               trainPrediction and testPrediction present
%                               the same data in each cross fold iteration.
%                               The bug was corrected.
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
% >> [performMeasures]=adjustBaggingFusion(data)
%--------------------------------------------------------------
function [performMeasures]=adjustBaggingFusion(data)

%we remove any non-finite value:
indNan=find(any(any(isnan(data.features))));
data.features(:,:,indNan)=[];
data.GT(indNan)=[];

%we ensure the ground truth is in the right format:
data.GT(~data.GT)=-1;

%parameters
baggingRuns=3;
decimateRatio=25;
decisionThreshold=0;

tic
%splitting data in train and test sets
[xTrain,xTest,indTest]=splitDataset(data,.9,0);
% sum(xTrain.GT==1)
% sum(xTrain.GT==-1)
% sum(xTest.GT==1)
% sum(xTest.GT==-1)

for k=1:baggingRuns
    %re-splitting training data in train and test sets, where the resulting
    %training set is the one used for training the classifierrs and the
    %test data from here is not used at all
    [xTrain2,xTest2,indTest2]=splitDataset(xTrain,.8);
%     sum(xTrain2.GT==1)
%     sum(xTrain2.GT==-1)
%     sum(xTest2.GT==1)
%     sum(xTest2.GT==-1)
    %-----------------------------------------------------------------
    %check performance across the iterations...
    %----------------------------------------------------------------- 
    disp(['Bagging run ',num2str(k), ' of ', num2str(baggingRuns),', to evaluate framework'])   

    %SVM, KNN, LDA classifier module
    parameters.svm.dummy=0;
    parameters.knn.dummy=0;
    parameters.lda.dummy=0;
    %%%now you can add BISIG classifiers as well
%     parameters.MD2.dummy=0;
%     parameters.RBF.dummy=0;
    [yTrain4Fusion,yDataStar]=starClassify(xTrain2,data,parameters); %data is only used for recalling 
                                                          %(and xTrain2 for training)
    
    
    %min, max, majority voting, median, average, product, sum fusion
    [dataFusedStar,listFusion]=starFusion(yDataStar, xTrain2.GT, yTrain4Fusion);

    %decimation
    trainFaDStar=starDecimate(dataFusedStar,decimateRatio);

    %using the test indices for defining result of train and test data sets
    testFaDStar=trainFaDStar(indTest,:); 
    trainFaDStar(indTest,:)=[]; %deleting test epochs
    alsoTestResults=trainFaDStar(indTest2,:);
    trainFaDStar(indTest2,:)=NaN; %disabling test bagging subset for fusion
    
    %saving results for bagging
    trainPrediction(:,:,k)=trainFaDStar;
    testPrediction(:,:,k)=testFaDStar;

    %%%%%%%%%%%%%%%%%%%%%%%%%MEASURING PERFORMANCE
    testFaDStar=cat(1,testFaDStar,alsoTestResults);       %adding the test subset of this bagging iteartion to the prediction for measureing performance
    trainFaDStar(indTest2,:)=[]; %deleting data samples not in train set for measuring performance
    trainGT=xTrain.GT; 
    testGT=cat(1,xTest.GT,trainGT(indTest2));
    trainGT(indTest2)=[]; %deleting corresponding GT
    
    confMatTest=measurePerformance2(testFaDStar,testGT,decisionThreshold,0);
    confMatTrain=measurePerformance2(trainFaDStar,trainGT,decisionThreshold,0);

    displayMat=1;
    if displayMat
        disp('conf matrix train')
    disp(confMatTrain)
        disp('conf matrix test')

        disp(confMatTest)

    end

   [performMeasures.TPRtrain(k),performMeasures.FPRtrain(k)]=trueAndFalsePositiveRates2(confMatTrain);
   [performMeasures.TPRtest(k),performMeasures.FPRtest(k)]=trueAndFalsePositiveRates2(confMatTest);

   %%%%%%measure performance without decimation (signals have to be
   %%%%%%generated)
   trainFusedStar=dataFusedStar;
   trainFusedStar(indTest,:)=[];
   testFusedStar=dataFusedStar(indTest,:);
   testFusedStar=cat(1,testFusedStar,trainFusedStar(indTest2,:));
   trainFusedStar(indTest2,:)=[]; %deleting data samples not in train set for measuring performance
   
   
   
   confMatTrain=measurePerformance2(trainFusedStar,trainGT,decisionThreshold,0);
   confMatTest=measurePerformance2(testFusedStar,testGT,decisionThreshold,0);
   [performMeasures.TPRtrainFuse(k),performMeasures.FPRtrainFuse(k)]=trueAndFalsePositiveRates2(confMatTrain);
   [performMeasures.TPRtestFuse(k),performMeasures.FPRtestFuse(k)]=trueAndFalsePositiveRates2(confMatTest);
    
   %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
   %%%%%%measure classification performance
   yTrainStar=yDataStar;
   yTrainStar(:,:,indTest)=[];
   yTestStar=yDataStar(:,:,indTest);
   yTestStar=cat(3,yTestStar,yTrainStar(:,:,indTest2));
   yTrainStar(:,:,indTest2)=[]; %deleting data samples not in train set for measuring performance
   
   %%%%measure SVM performance
   confMatTrain=measurePerformance2(permute(yTrainStar(3,:,:),[3,2,1]),trainGT,decisionThreshold,0);
   confMatTest=measurePerformance2(permute(yTestStar(3,:,:),[3,2,1]),testGT,decisionThreshold,0);
   [performMeasures.TPRtrainSVM(k),performMeasures.FPRtrainSVM(k)]=trueAndFalsePositiveRates2(confMatTrain);
   [performMeasures.TPRtestSVM(k),performMeasures.FPRtestSVM(k)]=trueAndFalsePositiveRates2(confMatTest);

   %%%%measure KNN performance
   confMatTrain=measurePerformance2(permute(yTrainStar(2,:,:),[3,2,1]),trainGT,decisionThreshold,0);
   confMatTest=measurePerformance2(permute(yTestStar(2,:,:),[3,2,1]),testGT,decisionThreshold,0);
   [performMeasures.TPRtrainKNN(k),performMeasures.FPRtrainKNN(k)]=trueAndFalsePositiveRates2(confMatTrain);
   [performMeasures.TPRtestKNN(k),performMeasures.FPRtestKNN(k)]=trueAndFalsePositiveRates2(confMatTest);

   %%%%measure LDA performance
   confMatTrain=measurePerformance2(permute(yTrainStar(1,:,:),[3,2,1]),trainGT,decisionThreshold,0);
   confMatTest=measurePerformance2(permute(yTestStar(1,:,:),[3,2,1]),testGT,decisionThreshold,0);
   [performMeasures.TPRtrainLDA(k),performMeasures.FPRtrainLDA(k)]=trueAndFalsePositiveRates2(confMatTrain);
   [performMeasures.TPRtestLDA(k),performMeasures.FPRtestLDA(k)]=trueAndFalsePositiveRates2(confMatTest);



end%for all the bagging iterations

%fusing the bagging results with majority voting
trainPrediction=permute(trainPrediction,[3,2,1]);
trainBaggingPrediction=starFusion(trainPrediction,[],[],{'majorVoting'},0);
testPrediction=permute(testPrediction,[3,2,1]);
testBaggingPrediction=starFusion(testPrediction,[],[],{'majorVoting'},0);

%measuring performance with majority voting
trainBaggingPrediction=permute(trainBaggingPrediction,[3,2,1]);
testBaggingPrediction=permute(testBaggingPrediction,[3,2,1]);
confMatTrain=measurePerformance2(trainBaggingPrediction,xTrain.GT,decisionThreshold,0);
confMatTest=measurePerformance2(testBaggingPrediction,xTest.GT,decisionThreshold,0);
[performMeasures.TPRtrainBaggMV,performMeasures.FPRtrainBaggMV]=trueAndFalsePositiveRates2(confMatTrain);
[performMeasures.TPRtestBaggMV,performMeasures.FPRtestBaggMV]=trueAndFalsePositiveRates2(confMatTest);
if displayMat
    disp('BAGGING MV conf matrix train')
disp(confMatTrain)
    disp('conf matrix test')

    disp(confMatTest)

end

%fusing the bagging results with average fusion
trainBaggingPrediction=mean(trainPrediction,1);
trainBaggingPrediction=permute(trainBaggingPrediction,[3,2,1]);
testBaggingPrediction=mean(testPrediction,1);
testBaggingPrediction=permute(testBaggingPrediction,[3,2,1]);
if displayMat
    disp('BAGGING MEAN conf matrix train')
disp(confMatTrain)
    disp('conf matrix test')

    disp(confMatTest)

end

%measuring performance average fusion bagging
confMatTrain=measurePerformance2(trainBaggingPrediction,xTrain.GT,decisionThreshold,0);
confMatTest=measurePerformance2(testBaggingPrediction,xTest.GT,decisionThreshold,0);
[performMeasures.TPRtrainBaggMean,performMeasures.FPRtrainBaggMean]=trueAndFalsePositiveRates2(confMatTrain);
[performMeasures.TPRtestBaggMean,performMeasures.FPRtestBaggMean]=trueAndFalsePositiveRates2(confMatTest);

end%adjust bagging