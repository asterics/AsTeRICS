%function to adjust the parameters of the classifiers.
% adapted from adjust_parameters3.m in v1.0 of multiClass
%
%
%function [meanPIndex, varPIndex]=adjust_parameters(data,option,paramname,param2adjust)
%function [confMatTrain,confMatTest]=adjust_parameters(data,option,paramname,param2adjust)


function [TPRtrain, FPRtrain,TPRtest,FPRtest,info]=adjustParameterClassifier(dataTrain,dataTest,parameters,paramname,param2adjust,Nvalid,dataFilename,decisionThreshold)
%%dataTrain, dataTest: a matrix with dimensions channels X samples X trials).
%
%option chooses the classificator
%'all' combines all of them
%'svm is Support Vector Machines
%'knn' is K Nearest Neighbours
%'lda' is Linear Discriminant Analysis
%
%paramname is a string with the name of the parameter 2 be tested
%param2adjust gives the values of that parameter
%       ex:[1 3 56 5] or for strings {'linear','quadratic'}

%the output is four matrices KxP of TPR and FPR for train and test sets,
%where K is the number of validation tests and P the number of different
%parameters tested
%
%ONLY 1 PARAMETER AT A TIME IS ACCEPTED


if nargin<4
    epsilon_candidates=[.000001, .0001,  .001,  .01, 0.1, 1 ,10 ,100, 1000];
    param2adjust=epsilon_candidates;
    paramname='epsilon';    
    Nvalid=10; %number of times the training is attempted for each feature value
    parameters.svm.epsilon=10;
end
if nargin<7
    dataFilename='not known'
end
parametersInput=parameters;
option=fieldnames(parameters);
if length(option)>1
    disp('You can adjust the parameter of just 1 classifier at a time')
    return
else
    option=option{1};
end

%info.about_data=input ('do you want to add any info about the data? ','s')
Nparamval=length(param2adjust);

% featureDim=size(dataTrain.features,2);
% data2.features=dataTrain.features(:,1:featureDim,:);
% data2.GT=dataTrain.GT;

%prune features with NaNs
indNan=find(any(any(isnan(dataTrain.features))));
dataTrain.features(:,:,indNan)=[];
dataTrain.GT(indNan)=[];

if nargin<8
    decisionThreshold=0;
end
display=0;

%obsolete from version 3 on
%we want to adjust:
%epsilon_candidates=[ .000001,  .00001,  .0001,  .001,  .01, 0.1, 1 ,10 ,100, 1000];


%if necessary:
addpath(genpath('./SVM_KM'))

tic
for k=1:Nvalid
    [xTrain,xTest,indTest]=splitDataset(dataTrain,.9);
    if isempty(dataTest)
        dataTest=xTest;
    end
    %-----------------------------------------------------------------
    %check performance across the iterations...
    %----------------------------------------------------------------- 
    disp(['Validation ',num2str(k), ' of ', num2str(Nvalid),', to test parameter ',paramname,' in classifier: ',option])   
    %we go through all the paramvalues:
    for p=1:Nparamval
        if iscell(param2adjust)==1
            disp(['Parameter ',paramname,' value ',param2adjust{p}])
        else
            disp(['Parameter ',paramname,' value ',num2str(param2adjust(p))])    
        end
            
        if iscell(param2adjust)==1
            eval(['parameters.',option,'.',paramname,'=''',param2adjust{p},''';'])
        else
            eval(['parameters.',option,'.',paramname,'=',num2str(param2adjust(p)),';'])   
        end
        
        [ypredTrain,ypredTest]=starClassify(xTrain,dataTest,parameters);          
        ypredTrain=squeeze(ypredTrain)';
        ypredTest=squeeze(ypredTest)';
        if display
            figure
            errorbar(mean(ypredTrain(xTrain.GT==1,:)),var(ypredTrain(xTrain.GT==1,:)),'b')
            hold all
            errorbar(mean(ypredTrain(xTrain.GT==0,:)),var(ypredTrain(xTrain.GT==0,:)),'g')
            figure
            errorbar(mean(ypredTest(dataTest.GT==1,:)),var(ypredTest(dataTest.GT==1,:)),'b')
            hold all
            errorbar(mean(ypredTest(dataTest.GT==0,:)),var(ypredTest(dataTest.GT==0,:)),'g')
        end

%         if display
%             disp('ypred on test')
%             ypredTest(:,i)
%         end
        labels=sort(unique(dataTrain.GT));
        confMatTrain=measurePerformance2(ypredTrain,xTrain.GT,decisionThreshold,0,labels);
        confMatTest=measurePerformance2(ypredTest,dataTest.GT,decisionThreshold,0,labels);
        clear ypredTrain
        clear ypredTest
        
        if display
            disp('conf matrix train')
            disp(confMatTrain)
            disp('conf matrix test')
            disp(confMatTest)
        end
        [TPRtrain(k,p),FPRtrain(k,p)]=trueAndFalsePositiveRates2(confMatTrain);
        [TPRtest(k,p),FPRtest(k,p)]=trueAndFalsePositiveRates2(confMatTest);
%         %our performance criterion is below (time X parametervalue)
%         PIndex(i,p)=TPR-FPR;

    end %each parameter value 

       
end%for all the validation iterations

toc
info.parameter=paramname;
info.classifier=option;
info.parameter_values=param2adjust;
info.tests_performed=Nvalid;
info.input_file=dataFilename;
info.decisionThreshold=decisionThreshold;
parameters=parametersInput;
end



