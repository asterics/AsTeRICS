function [ypredTrain,ypredTest]=starClassify(dataTrain, dataTest,parameters)
%this function performs a classsification using some of the diferent
%classifiers available for this purpose. 
%
% Output: 
%
%   ypredTrain has as output a 3 dim matrix with: K classifiers X N samples
%   X T trials
%
%   ypredTest has the same format
%
%
% dataTrain: the data used in the training. a format such as:
%       data.GT         containing the ground truth value (expected to be 1 or -1)
%       data.features   containing the value of feature for the training
%
% dataTest has the same format than before.
%
%
% Optional Arguments: 
%       kind
%
%
%       kind= 'lda'    perform a linear discriminant analysis classification
%
%       kind= 'knn' performs a k nearest neighbours classification
%
%       kind = 'svm' performs support vector machine classification
%
%       kind='all' performs all of them, and provides the result in the order:
%   'lda', then 'knn' and then 'svm'
%
%  It also verifies there are no NaN in the data provided
%
% ICS ASF & JLL @ Starlab October 2008
%v2 ASF Aug 2009 changing the output of LDA to the posterior probability
%matrix and computing the prior probability from the training set (option
%'empirical'). Changing the input of the parameters, now in form of a
%structure (default values are shown):
% parameters  .svm    .c=inf
%                     .epsilon=1e-7
%                     .kerneloption=0.3
%                     .kernel='gaussian'
%             .knn    .Kneigh=15
%                     .m=1
%             .lda    .cl_type='diagquadratic'
%                     .priorProb='empirical'



if nargin <3
    kind='all';
    parameters.svm.dummy=0;
    parameters.knn.dummy=0;
    parameters.lda.dummy=0;
else
    methods=fieldnames(parameters);
    switch length(methods)
        case 3,
            kind='all';
        case 2,
            disp('Sorry function has not been implemented for 2 classifiers')
            return
        case 1,
            %kind=methods(1) is not accepted by the switch later in code
            if isequal(methods{1},'svm')
                kind='svm';
            else
                if isequal(methods{1},'lda')
                    kind='lda';
                else
                    kind='knn';
                end
            end
    end 
end

if nargin <2
    disp('not enough inputs');
end






ytest=dataTest.GT;
xtest=dataTest.features;
if size(ytest,1)~=size(xtest,3)
    ytest=ytest';
end

yapp=dataTrain.GT;
xapp=dataTrain.features;
if size(yapp,1)~=size(xapp,3)
    yapp=yapp';
end


%in case Ground Truth is in {0, 1}, we switch to: {-1,+1} (needed for SVM)
yapp(~yapp)=-1;

%%default parameters CFV selkected
%c=inf;
C1=100000;
C2=100000;
C3=100000;
C4=100000;
C=[C1*ones(sum(dataTrain.GT==1),1); C2*ones(sum(dataTrain.GT==2),1); C3*ones(sum(dataTrain.GT==3),1); C4*ones(sum(dataTrain.GT==4),1);];
epsilon=1e-7;  
kerneloption=.3; 
kernel='gaussian';
verbose = 0;
Kneigh=5;
m=2;
cl_type='diagquadratic';
priorProb='empirical';


switch kind
    case 'svm',
        %addpath(genpath('./SVM_KM')) %include SVM implementation
        %change parameters in case some of them is defined
        parameterNames=fieldnames(parameters.svm);
        for i=1:length(parameterNames)
            which=getfield(parameters.svm,parameterNames{i});
            if isnumeric(which)
                eval([parameterNames{i},'=',num2str(which),';']);
            else
                command=[parameterNames{i},'=''',which,''';'];
                eval(command);
            end
        end
    case 'knn' ,
        %change parameters in case some of them is defined
        parameterNames=fieldnames(parameters.knn);
        for i=1:length(parameterNames)
            which=getfield(parameters.knn,parameterNames{i});
            eval([parameterNames{i},'=',num2str(which),';'])
        end
    case 'lda',
        %change parameters in case some of them is defined
        parameterNames=fieldnames(parameters.lda);
        for i=1:length(parameterNames)
            which=getfield(parameters.lda,parameterNames{i});
            if isnumeric(which)
                eval([parameterNames{i},'=',num2str(which),';']);
            else
                command=[parameterNames{i},'=''',which,''';'];
                eval(command);
            end

        end
    case 'all',
         %addpath(genpath('./SVM_KM')) %include SVM implementation
        %change parameters SVM in case some of them is defined
        parameterNames=fieldnames(parameters.svm);
        for i=1:length(parameterNames)
            which=getfield(parameters.svm,parameterNames{i});
            if isnumeric(which)
                eval([parameterNames{i},'=',num2str(which),';']);
            else
                command=[parameterNames{i},'=''',which,''';'];
                eval(command);
            end

        end
        %change parameters KNN in case some of them is defined
        parameterNames=fieldnames(parameters.knn);
        for i=1:length(parameterNames)
            which=getfield(parameters.knn,parameterNames{i});
            eval([parameterNames{i},'=',num2str(which),';'])
        end
        %LDA
        parameterNames=fieldnames(parameters.lda);
        for i=1:length(parameterNames)
            which=getfield(parameters.lda,parameterNames{i});
            if isnumeric(which)
                eval([parameterNames{i},'=',num2str(which),';']);
            else
                command=[parameterNames{i},'=''',which,''';'];
                eval(command);
            end
        end
    otherwise,
        disp('This Classification option requested is not understood')
        return;
end




ypredTrain=[];
ypredTest=[];
%for each time sample
for i=1:size(xapp,2)
    %data of one time sample for all channels simultenously
    sampleData=permute(xapp(:,i,:),[3,1,2]);
    testData=permute(xtest(:,i,:),[3,1,2]);
    switch kind
        case 'svm' ,
            [xsup,w,b,nbsv]=svmmulticlass(sampleData,yapp,4,C,1e-7,kernel,kerneloption,0);
            [ypred,numberClasses,memberships] = svmmultival(sampleData,xsup,w,b,nbsv,kernel,kerneloption);
            ypredTrainSVM(i,:,:) = memberships;
            clear memberships
            [ypred,numberClasses,memberships] = svmmultival(testData,xsup,w,b,nbsv,kernel,kerneloption);
            %ypredXXXXSVM(timeSample,dataSample,classIndex)
            ypredTestSVM(i,:,:) = memberships;
            clear memberships
    
        case 'knn',
            [predicted,memberships, numhits] = U_C_knn(sampleData, yapp, testData, ytest, Kneigh, 0, 1,m);
            ypredTestKNN(i,:,:)=2*memberships-1;%we want [-1, 1]
            clear memberships
            [predicted,memberships, numhits] = U_C_knn(sampleData, yapp, sampleData, yapp, Kneigh, 0, 1,m);
            ypredTrainKNN(i,:,:)=2*memberships-1;%we want [-1, 1]
            clear memberships
            
        case 'lda',
            [cl,err,posterior]=classify(sampleData,sampleData,yapp,cl_type,priorProb);
            ypredTrainLDA(i,:,:)=2*posterior-1;%we want [-1, 1]
            clear posterior
            [cl,err,posterior]=classify(testData,sampleData,yapp,cl_type,priorProb);
            ypredTestLDA(i,:,:)=2*posterior-1;%we want [-1, 1]

       case 'all'

            %[xsup,w,b,pos]=svmclass(sampleData,yapp,c,epsilon,kernel,kerneloption,verbose);
            [xsup,w,b,nbsv]=svmmulticlass(sampleData,yapp,4,C,1e-7,kernel,kerneloption,0);
            [ypred,numberClasses,memberships] = svmmultival(sampleData,xsup,w,b,nbsv,kernel,kerneloption);
            ypredTrainSVM(i,:,:) = memberships;
            clear memberships
            [ypred,numberClasses,memberships] = svmmultival(testData,xsup,w,b,nbsv,kernel,kerneloption);
            %ypredXXXXSVM(timeSample,dataSample,classIndex)
            ypredTestSVM(i,:,:) = memberships;
            clear memberships

            [cl,err,posterior]=classify(sampleData,sampleData,yapp,cl_type,priorProb);
            ypredTrainLDA(i,:,:)=2*posterior-1;%we want [-1, 1]
            clear posterior
            [cl,err,posterior]=classify(testData,sampleData,yapp,cl_type,priorProb);
            ypredTestLDA(i,:,:)=2*posterior-1;%we want [-1, 1]
            clear posterior
            
            [predicted,memberships, numhits] = U_C_knn(sampleData, yapp, testData, ytest, Kneigh, 0, 1,m);
            ypredTestKNN(i,:,:)=2*memberships-1;%we want [-1, 1]
            clear memberships
            [predicted,memberships, numhits] = U_C_knn(sampleData, yapp, sampleData, yapp, Kneigh, 0, 1,m);
            ypredTrainKNN(i,:,:)=2*memberships-1;%we want [-1, 1]
            clear memberships

 
    
    end
    clear predicted
end %each temporal sample


switch kind
    case 'svm' ,
        ypredTrain(1,:,:)=ypredTrainSVM;
        ypredTest(1,:,:)=ypredTestSVM;

    case 'knn',
        ypredTrain(1,:,:)=ypredTrainKNN;
        ypredTest(1,:,:)=ypredTestKNN;
    case 'lda',
        ypredTrain(1,:,:)=ypredTrainLDA;
        ypredTest(1,:,:)=ypredTestLDA;
    case 'all'
        ypredTrain(1,:,:,:)=ypredTrainLDA;
        ypredTest(1,:,:,:)=ypredTestLDA;

        ypredTrain(2,:,:,:)=ypredTrainKNN;
        ypredTest(2,:,:,:)=ypredTestKNN;

        ypredTrain(3,:,:,:)=ypredTrainSVM;
        ypredTest(3,:,:,:)=ypredTestSVM;

end












