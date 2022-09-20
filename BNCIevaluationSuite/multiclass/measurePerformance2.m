function [confusionMat]=measurePerformance2(ypred,gt,decisionThres,print_result,labelClass)
%receives the results of prediction and the ground truth and computes the
%confusion matrix (it prints as well the TPR and FPR indices) given the
%decision threshold
%v2: does not compute the average over samples
%
if nargin < 4
    print_result=1;
end

if nargin<5
    labelClass=[-1,1];
end

%%%%this has been changed from version 1
%ypred=mean(ypred,2);
if size(gt,2)>1
    gt=gt';
end
gt=repmat(gt,1,size(ypred,2));


ypred(ypred>decisionThres)=labelClass(2);
ypred(ypred<=decisionThres)=labelClass(1);

confusionMat(1,1)=sum(ypred(ypred==gt)==labelClass(2));
confusionMat(2,2)=sum(ypred(ypred==gt)==labelClass(1));
confusionMat(1,2)=sum(ypred(ypred~=gt)==labelClass(1));
confusionMat(2,1)=sum(ypred(ypred~=gt)==labelClass(2));
if print_result
    [TPR,FPR]=trueAndFalsePositiveRates2(confusionMat)
end
