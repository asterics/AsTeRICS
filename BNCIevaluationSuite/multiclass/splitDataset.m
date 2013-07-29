function [varargout]=splitDataset(data,trainPercentage,homogDataSets)
%function that splits datasets in training and test groups.
%before splitting, it also checks the features have all a finite value
%as input it takes:
%% data embeds the BCI data matrix for one subject (a matrix with
%% dimensions channels X samples X epochs).
%%
%% Optional entries:
%   homogDataSets: if 1, equal number of positive and negative examples in the train set
%
%   trainPercentage: between 0 and 1, amount of data used in the trianing
%   default value: 0.7
%   trainPercentage (alternative): vector of indices to be selected in the
%   test set
%
%   Developed ASF ICS JLL October 2008
%
%   Modified ASF November 2008 Adding an extra parameter out: the indices of data in
%   the test. This is necessary for bagging procedures. Changing data.GT
%   reformatting into yapp only if input is a row vector.
%
%   Modified ASF 17/12/08
%   Adding possibility of trainPercentage being a vector of indices for
%   selecting the test subset. This is convenient in case we want to repeat
%   test selection for different data sets (e.g. being of different
%   classes after an OVR procedure)

% Modified ASF 28/08/09 Adding possibility for other labels than -1 and 1.
% Now positive label has to be > than negative label

    if nargin <3
        homogDataSets=1;
    end
    
    if nargin <2 
        trainPercentage=.7;
    end
    if length(trainPercentage)==0 %trainPercentage==[]
        trainPercentage=.7;
    end
    

    xapp=data.features;
    if size(data.GT,1)==1
        yapp=data.GT';
    else
        yapp=data.GT;
    end
    labels=sort(unique(yapp));
    %labels=[-1,1]; %original version
    
    %generate indices for selection
    
    if length(trainPercentage)>1
        indTest=trainPercentage;
    
    %-----------------------------------------------------------------
    %randomly select data subset for train and leave the rest for test in
    %case trainPercentage is not a vector 
    %-----------------------------------------------------------------
    else
        testPercentage=1-trainPercentage;

        if homogDataSets
            positiveInd=find(yapp==labels(2));
            numberPos=length(positiveInd);
            numberTest=round(testPercentage*numberPos);
            indTest=randperm(numberPos);
            indTest=positiveInd(indTest(1:numberTest));

            negativeInd=find(yapp==labels(1));
            numberTrain=numberPos-numberTest;
            indTestNeg=randperm(length(negativeInd));
            indTestNeg=negativeInd(indTestNeg(numberTrain+1:end));
            indTest=cat(1,indTest,indTestNeg);
        %non-balanced data sets
        else
            numberTest=round(testPercentage*size(xapp,3));
            indTest=randint(1,numberTest,[1,size(xapp,3)]);
        end%balanced data sets
    end%train percentage is vector


    %xapp(:,:,indTest)=[]; %??
    xtest=xapp(:,:,indTest);
    ytest=yapp(indTest,1);
    xapp(:,:,indTest)=[];
    yapp(indTest)=[];

    
    xTest.GT=ytest;
    xTest.features=xtest;
    
    xTrain.GT=yapp;
    xTrain.features=xapp;
    
    varargout{1}=xTrain;
    varargout{2}=xTest;
    if nargout>2
        varargout{3}=indTest;
    end
    
    