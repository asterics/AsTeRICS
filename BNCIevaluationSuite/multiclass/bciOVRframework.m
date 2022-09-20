%v. 2 adding decision stage and kappa computation
%v. 3 adding output as alebls for test and training

%EX.
% >> load 'W:\UCONTROL\BCI competition iii\OVR_aproach\features subject k3b\features4aproach1_c4.mat'
% %split data in train and test sets with 80% training and non-homogenous data
% [xTrain,xTest,indTest]=splitDataset(data,.8,0);
% >> dataTrain(4)=xTrain;
% >> dataTest(4)=xTest;
% >> load 'W:\UCONTROL\BCI competition iii\OVR_aproach\features subject k3b\features4aproach1_c3.mat'
% %split data in train and test sets with the same indices as used for the
% %former class
% [xTrain,xTest,indTest2]=splitDataset(data,indTest);
% >> dataTrain(3)=xTrain;
% >> dataTest(3)=xTest;
% >> load 'W:\UCONTROL\BCI competition iii\OVR_aproach\features subject k3b\features4aproach1_c2.mat'
% >> [xTrain,xTest,indTest2]=splitDataset(data,indTest);
% >> dataTrain(2)=xTrain;
% >> dataTest(2)=xTest;
% >> load 'W:\UCONTROL\BCI competition iii\OVR_aproach\features subject k3b\features4aproach1_c1.mat'
% >> [xTrain,xTest,indTest2]=splitDataset(data,indTest);
% >> dataTrain(1)=xTrain;
% >> dataTest(1)=xTest;
% >>
% [kap,kapTest,classLabelSamples,classLabelSamplesTest,classMemberships,classMembershipsTest]=bciOVRframework(dataTrain,dataTest);
function [kap,kapTest,classLabelSamples,classLabelSamplesTest,classPrediction,classPredictionTest,labelClass,labelClassTest]=bciOVRframework(xTrain,xTest,baggingRuns,homogeneousBagging,standardizeClassifierOutputs,subtractMeanMembership)
if nargin<3
    baggingRuns=3
end
if nargin<4
    homogeneousBagging=1
end
if nargin<5
    standardizeClassifierOutputs=0
end
if nargin<6
    subtractMeanMembership=0
end

if baggingRuns %%%bagging runs have to be undertaken
    for k=1:length(xTrain)
        %fill in the multiclass GT membership matrix for the training
        multiclassGT(:,k)=xTrain(k).GT;
        multiclassGTtest(:,k)=xTest(k).GT;

        %bagging with 3 runs
        [trainBaggingPrediction,testBaggingPrediction]=baggingFusion(xTrain(k),xTest(k),baggingRuns,homogeneousBagging);
        %joints class predictions in a multi-class matrix of dimensions
        %classIndex X timeSamplesOfAllEpochs X baggingFusionOperator
        %baggingFusionOperator=1 embeds results for majority voting and
        %baggingFusionOperator=2 embeds results for averaging
        classPrediction(k,:,:)=reshape(trainBaggingPrediction,[size(trainBaggingPrediction,1),size(trainBaggingPrediction,2)*size(trainBaggingPrediction,3)])';
        classPredictionTest(k,:,:)=reshape(testBaggingPrediction,[size(testBaggingPrediction,1),size(testBaggingPrediction,2)*size(testBaggingPrediction,3)])';
    end
else %bagging runs are complete and input data is in another format
    classPrediction=xTrain{1};
    classPredictionTest=xTest{1};
    multiclassGT=xTrain{2};
    multiclassGTtest=xTest{2};
end

%generate the class labels needed by bci4eval
[i,j,s]=find(multiclassGT');
classLabels=i;
numberSamplesAfterDecimation=size(classPrediction,2)/length(classLabels); %GT has to be extended by this number
classLabelSamples=repmat(classLabels',numberSamplesAfterDecimation,1); %each sample of the GT is repeated this number
classLabelSamples=classLabelSamples(:);%turn the repeated matrix one-dimensional
[i,j,s]=find(multiclassGTtest');    %"find()" sorts the column index j (it is a really extrange arbitrary convention)
                                    %so we have to transpose the matrix and
                                    %take the row index (i), which is not sorted
classLabelsTest=i;
numberSamplesAfterDecimationTest=size(classPredictionTest,2)/length(classLabelsTest); %GT has to be extended by this number
classLabelSamplesTest=repmat(classLabelsTest',numberSamplesAfterDecimationTest,1); %each sample of the GT is repeated this number
classLabelSamplesTest=classLabelSamplesTest(:);%turn the repeated matrix one-dimensional

% %turn the predictions into decisions and computing kappa

%%%%%%decision stage  - it looks like adding an offset to the predictions
%%%%%%of class 1 and 2 improves performance
if subtractMeanMembership
    classPrediction=classPrediction-repmat(mean(classPrediction,2),[1,size(classPrediction,2),1]);
    classPredictionTest=classPredictionTest-repmat(mean(classPredictionTest,2),[1,size(classPredictionTest,2),1]);
end



for i=0:0
classPredictionOffest=classPrediction;
classPredictionOffestTest=classPredictionTest;
classPredictionOffest(1,:,:)=classPredictionOffest(1,:,:)+i/10.0;
classPredictionOffest(2,:,:)=classPredictionOffest(2,:,:)+i/10.0;
classPredictionOffestTest(1,:,:)=classPredictionOffestTest(1,:,:)+i/10.0;
classPredictionOffestTest(2,:,:)=classPredictionOffestTest(2,:,:)+i/10.0;

[val,labelClass]=max(classPredictionOffest,[],1);
[val,labelClassTest]=max(classPredictionOffestTest,[],1);

kap(i+1,2)=kappa(classLabelSamples,labelClass(:,:,2)');
kapTest(i+1,2)=kappa(classLabelSamplesTest,labelClassTest(:,:,2)');
kap(i+1,1)=kappa(classLabelSamples,labelClass(:,:,1)');
kapTest(i+1,1)=kappa(classLabelSamplesTest,labelClassTest(:,:,1)');
end
