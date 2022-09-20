function [TPR,FPR]=trueAndFalsePositiveRates2(classMatrix,subject)
%compute the true positive rate (TPR) and the false positive rate (FPR)
%for a particular subject given a classification matrix. If no subject is given as input
%the other input is supposed to be a
%confusion matrix (result of binary classification) 
if nargin>1
    confusionMatrix=classificationMat2confusionMat(classMatrix,subject);
else
    confusionMatrix=classMatrix;
end
if sum(confusionMatrix(1,:))~=0
    TPR=confusionMatrix(1,1)/sum(confusionMatrix(1,:));
else
    TPR=0;
end
if sum(confusionMatrix(2,:))~=0
    FPR=confusionMatrix(2,1)/sum(confusionMatrix(2,:));
else
    FPR=1;
end
