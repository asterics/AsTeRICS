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
function [kap,kapTest,classLabelSamples,classLabelSamplesTest,classPrediction,classPredictionTest,labelClass,labelClassTest]=bciOVRframeworkMultiClass(dataTrain,dataTest,baggingRuns,homogeneousBagging,standardizeClassifierOutputs,subtractMeanMembership)
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
        %bagging with 3 runs
        for k=1:length(dataTrain)
%         [trainBaggingPrediction,testBaggingPrediction]=baggingFusion(xTrain(k),xTest(k),baggingRuns,homogeneousBagging);
            [classPrediction(:,:,:,k),classPredictionTest(:,:,:,k)]=baggingFusionMultiClass(dataTrain(k),dataTest(k),baggingRuns,homogeneousBagging); 
        end
        numberSamplesAfterDecimation=size(classPrediction,2);
        numberSamplesAfterDecimationTest=size(classPredictionTest,2);
        %joints class predictions in a multi-class matrix of dimensions
        %classIndex X timeSamplesOfAllEpochs X number of class spaces
        %used
        classPrediction=reshape(classPrediction,[size(classPrediction,1),size(classPrediction,2)*size(classPrediction,3),size(classPrediction,4)]);
        classPredictionTest=reshape(classPredictionTest,[size(classPredictionTest,1),size(classPredictionTest,2)*size(classPredictionTest,3),size(classPredictionTest,4)]);
        %transforms the dimensions in 
        % timeSamplesOfAllEpochs X classIndex X classSpaceIndex
        classPrediction=permute(classPrediction,[2,1,3]); %transpose 
        classPredictionTest=permute(classPredictionTest,[2,1,3]);
    %end
        

end
if length(dataTrain)>1 %we are using separate feature spaces, so we need to fuse the classification in the 4 spaces
    disp('doing average over class space classifier results, a DT would be better')
    classPrediction=mean(classPrediction,3);
    classPredictionTest=mean(classPredictionTest,3);
    
    %%%%%%this was not programmed when running the last iteration
end

%we use the first component of the dataTrain array for 
%getting the labels of GT. In case of joint spaces (dimensionality of dataTrain is 1) 
%this does not give any error. In case of separate spaces (dimensionality dataTrain is
%the number of classes) all GTs are the same, so we can take the first one
classLabels=dataTrain(1).GT; %already with integer labels now
%numberSamplesAfterDecimation=size(classPrediction,2)/length(classLabels); %GT has to be extended by this number
classLabelSamples=repmat(classLabels,numberSamplesAfterDecimation,1); %each sample of the GT is repeated this number
classLabelSamples=classLabelSamples(:);%turn the repeated matrix one-dimensional

classLabelsTest=dataTest(1).GT; %already with integer labels
%numberSamplesAfterDecimationTest=size(classPredictionTest,2)/length(classLabelsTest); %GT has to be extended by this number
classLabelSamplesTest=repmat(classLabelsTest,numberSamplesAfterDecimationTest,1); %each sample of the GT is repeated this number
classLabelSamplesTest=classLabelSamplesTest(:);%turn the repeated matrix one-dimensional

% %turn the predictions into decisions and computing kappa

%%%%%%decision stage  - it looks like adding an offset to the predictions
%%%%%%of class 1 and 2 improves performance

if subtractMeanMembership
    [val,labelClass]=max(classPrediction-repmat(mean(classPrediction),size(classPrediction,1),1),[],2);
    [val,labelClassTest]=max(classPredictionTest-repmat(mean(classPredictionTest),size(classPredictionTest,1),1),[],2);
else
    [val,labelClass]=max(classPrediction,[],2);
    [val,labelClassTest]=max(classPredictionTest,[],2);
end

kap=kappa(classLabelSamples,labelClass);
kapTest=kappa(classLabelSamplesTest,labelClassTest);

