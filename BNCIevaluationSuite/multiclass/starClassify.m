function [ypredTrain,ypredTest]=starClassify(dataTrain, dataTest,parameters)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% starClassify
% Performs a classsification using some of the diferent classifiers available (K-nearest, LDA, SVM) and these
% in BIOSIG toolbox. It also verifies there are no NaN in the data provided.
%
% [Use example in B:\STAR2STAR - Internal\TN Technical Notes (TB+KN)\TN00146 - BCI Classification - OVR aproach]
%----------------------------------------------------------
%Inputs
% dataTrain:	structure with data set features and ground truth
%       features: EEG data as computed by the OVR procedure - matrix channels X
%                 samples X trials  ()
%       GT:       class labels for each trial - column vector 1 x trials ()
% dataTest: same type of structure for the test set.
% parameters: It is a structure defining the parameters of each classifier
%               type. Which kind of classifier is called depends on the first field (e.g. run
%               the function with parameters.svm.dummy=0 for running a SVM
%               with default parameters. These are the following for the non-biosig ones:
%                                               .svm    .c=inf
%                                                       .epsilon=1e-7
%                                                       .kerneloption=0.3
%                                                       .kernel='gaussian'
%                                               .knn    .Kneigh=15
%                                                       .m=1
%                                               .lda    .cl_type='diagquadratic'
%                                                       .priorProb='empirical'
%               See train_sc in BIOSIG toolbox for the classifier type codes and their
%               default parameters (e.g. if you define
%               parameters.SVM.dummy=0 the SVM function in biosig is called with its
%               default parameters). For the moment it is not possible to change
%               parameters in BIOSIG classifiers.
%----------------------------------------------------------
%Outputs
% trainPrediction: Classification/prediction for the training set - matrix classifierNumber X samples X
%                   trials
%					If kind='lda'|'knn'|'svm'results are in (1,:,:)
%					If kind='all'
%                   SVM result is in (1,:,:)
%                   KNN result is in (2,:,:)
%					LDA result is in (3,:,:)
%                   Otherwise the results are organized in the same order as parameter
%                   fields are defined. E.g. if fieldnames(parameters)=={'mda';'lda'}
%                   biosig MDA results are in (1,:,:) and LDA in (2,:,:).
% testPrediction: Classification/prediction for the test set with same structure as train - matrix classifierNumber X samples X
%                   trials
%----------------------------------------------------------
%Dependencies
% svmclass, svmval: SVM-KM (./SVM_KM)
% U_C_knn: EEGStarlab
% classify: Matlab Statistics Toolbox
% train_sc, test_sc: BIOSIG toolbox
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v1		01/12/08    IC,JLL,ASF  First version
%%v2        01/08/09    ASF         Changing the output of LDA to the posterior probability
%                                   matrix and computing the prior probability from the training set (option
%                                   'empirical'). Changing the input of the parameters, now in form of a
%                                   structure (default values are shown):
%                                   parameters  .svm    .c=inf
%                                                       .epsilon=1e-7
%                                                       .kerneloption=0.3
%                                                       .kernel='gaussian'
%                                               .knn    .Kneigh=15
%                                                       .m=1
%                                               .lda    .cl_type='diagquadratic'
%                                                       .priorProb='empirical'  
% v3        11/08/10    ASF         StarEEGlab header is added and biosig
%                                   classification functions integrated. These functions
%                                   can deliver a class label (option not enabled) or a
%                                   posterior probability, which is what the current
%                                   version does.For the moment it is not possible to change
%                                   parameters in BIOSIG classifiers.
%                                   It is worth pointing out that it is not clear which is
%                                   the range of the biosig classifiers, so its employment
%                                   has to be done careful. For the moment we have
%                                   observed two types, whose output is differently treated via
%                                   employment of boolean biosigStatClassifier (if 0 output range 
%                                   is supposed to be [-infty,0], else [0,1]).
% v3        03/11/10    ASF         After some discussions with Alois Schloegl responsible
%                                   of BIOSIG development it is clear that each classifier
%                                   has its own output range and than no normalization
%                                   function can be recommended in a general form for all
%                                   of them. Therefore I decided to output BIOSIG
%                                   classifiers aqs they are (leaving normalization to be
%                                   implemented outside this function). The only
%                                   normalized outputs will be those of the original LDA
%                                   and fuzzy KNN, which are normalized to range [-1,1].
%                                   This is indicated through the boolean
%                                   probabilisticClassifier. This decision has been made
%                                   for the sake of compatibility with previous versions
%                                   of the function.
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------


if nargin <3 %this runs classifiers not from biosig for compatibility reasons
%     kind='all';
    parameters.svm.dummy=0;
    parameters.knn.dummy=0;
    parameters.lda.dummy=0;
% else
%     methods=fieldnames(parameters);
%     switch length(methods)
%         case 3,
%             kind='all';
%         case 2,
%             disp('Sorry function has not been implemented for 2 classifiers')
%             return
%         case 1,
%             %kind=methods(1) is not accepted by the switch later in code
%             if isequal(methods{1},'svm')
%                 kind='svm';
%             else
%                 if isequal(methods{1},'lda')
%                     kind='lda';
%                 else
%                     kind='knn';
%                 end
%             end
%     end 
end
methods=fieldnames(parameters);

if nargin <2
    disp('not enough inputs, data for test is needed');
end





%%%GT should be given in a column vector
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


%in case Ground Truth is in {0, 1}, we switch to: {-1,+1} (needed for SVM-KM)
yapp(~yapp)=-1;
probabilisticClassifier=0; %boolean for indicating the classifier has output in range [0,1] and has to be normalized to range [-1,1].
% biosigStatClassifier=0;
% statisticalClassifiers={'MDA','MD2','MD3','GRB','QDA','LD2','LD3','LD4','GDBC'};

%%default parameters CFV selected
c=inf; 
epsilon=1e-7;  
kerneloption=.3; 
kernel='gaussian';
verbose = 0;
yappKNN=yapp;
f=find(yapp==-1);
yappKNN(f)=2;
ytestKNN=ytest;
f=find(ytest==-1);
ytestKNN(f)=2;
Kneigh=5;
m=2;
cl_type='diagquadratic';
priorProb='empirical';

%%%%parameter setting
for j=1:length(methods)
    kind=methods{j};
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
%         case 'all',
%              %addpath(genpath('./SVM_KM')) %include SVM implementation
%             %change parameters SVM in case some of them is defined
%             parameterNames=fieldnames(parameters.svm);
%             for i=1:length(parameterNames)
%                 which=getfield(parameters.svm,parameterNames{i});
%                 if isnumeric(which)
%                     eval([parameterNames{i},'=',num2str(which),';']);
%                 else
%                     command=[parameterNames{i},'=''',which,''';'];
%                     eval(command);
%                 end
% 
%             end
%             %change parameters KNN in case some of them is defined
%             parameterNames=fieldnames(parameters.knn);
%             for i=1:length(parameterNames)
%                 which=getfield(parameters.knn,parameterNames{i});
%                 eval([parameterNames{i},'=',num2str(which),';'])
%             end
%             %LDA
%             parameterNames=fieldnames(parameters.lda);
%             for i=1:length(parameterNames)
%                 which=getfield(parameters.lda,parameterNames{i});
%                 if isnumeric(which)
%                     eval([parameterNames{i},'=',num2str(which),';']);
%                 else
%                     command=[parameterNames{i},'=''',which,''';'];
%                     eval(command);
%                 end
%             end
        otherwise,
            disp('Classification requested from BIOSIG')
            disp(kind)
            %return;
    end
end


%%%%%classification
ypredTrain=[];
ypredTest=[];
%for each method
for j=1:length(methods)
    kind=methods{j}
    %for each time sample
    for i=1:size(xapp,2)
        %data of one time sample for all channels simultenously
        sampleData=permute(xapp(:,i,:),[3,1,2]);
        testData=permute(xtest(:,i,:),[3,1,2]);
        switch kind
            case 'svm' ,
                [xsup,w,b,pos]=svmclass(sampleData,yapp,c,epsilon,kernel,kerneloption,verbose);
                ypredTrainTime(i,:)= svmval(sampleData,xsup,w,b,kernel,kerneloption);
                ypredTestTime(i,:) = svmval(testData,xsup,w,b,kernel,kerneloption);

            case 'knn',
                [predicted,memberships, numhits] = U_C_knn(sampleData, yappKNN, testData, ytestKNN, Kneigh, 0, 1,m);
                ypredTestTime(i,:)=memberships(:,1);
                [predicted,memberships, numhits] = U_C_knn(sampleData, yappKNN, sampleData, yappKNN, Kneigh, 0, 1,m);
                ypredTrainTime(i,:)=memberships(:,1);
                if i==1 %flag for mapping classifier result (see below)
                   probabilisticClassifier=1;
                end


            case 'lda',
                %%%%%These lines are added in order to set the classify function to this
                %%%%%of the statistics toolbox (and not biosig's classify function)
                tmp = path;
                path([matlabroot,'\toolbox\stats'],tmp);
                [cl,err,posterior]=classify(sampleData,sampleData,yapp,cl_type,priorProb);
                ypredTrainTime(i,:)=posterior(:,2);
                [cl,err,posterior]=classify(testData,sampleData,yapp,cl_type,priorProb);
                ypredTestTime(i,:)=posterior(:,2);
                %restore path
                path(tmp);
                if i==1 %flag for mapping classifier result (see below)
                   probabilisticClassifier=1;
                end

%            case 'all'
%                 [xsup,w,b,pos]=svmclass(sampleData,yapp,c,epsilon,kernel,kerneloption,verbose);
%                 ypredTrainSVM(i,:) = svmval(sampleData,xsup,w,b,kernel,kerneloption);
%                 ypredTestSVM(i,:) = svmval(testData,xsup,w,b,kernel,kerneloption);
% 
%                 [cl,err,posterior]=classify(sampleData,sampleData,yapp,cl_type,priorProb);
%                 ypredTrainLDA(i,:)=2*posterior(:,2)-1;%we want [-1, 1]
%                 [cl,err,posterior]=classify(testData,sampleData,yapp,cl_type,priorProb);
%                 ypredTestLDA(i,:)=2*posterior(:,2)-1;%we want [-1, 1]
% 
%                 [predicted,memberships, numhits] = U_C_knn(sampleData, yappKNN, testData, ytestKNN, Kneigh, 0, 1,m);
%                 ypredTestKNN(i,:)=2*memberships(:,1)-1;%we want [-1, 1]
%                 [predicted,memberships, numhits] = U_C_knn(sampleData, yappKNN, sampleData, yappKNN, Kneigh, 0, 1,m);
%                 ypredTrainKNN(i,:)=2*memberships(:,1)-1;%we want [-1, 1]
            otherwise
               %yapp(yapp<0)=2; %this GT format (GT>0) is needed by BIOSIG classifiers
               %Former line is not necessary since we can use yappKNN, which has this format
               CC=train_sc(sampleData,yappKNN,kind); %for the moment no parameter passing to classifiers
               R  = test_sc(CC,sampleData);
               %ypredTrainTime(i,:)= group(R.classlabel);%this is for class label outputs
               ypredTrainTime(i,:)= R.output(:,1);
               R  = test_sc(CC,testData);
               %ypredTestTime(i,:) = group(R.classlabel);%this is for class label outputs
               ypredTestTime(i,:)= R.output(:,1);
               
%                if i==1 %flag for mapping back classifier result (see below)
%                    probabilisticClassifier=1;
%                    if ~isempty(strmatch(kind,statisticalClassifiers)) %BIOSIG so-called statistical classifiers need extra mapping
%                        biosigStatClassifier=1;
%                    end
%                end


        end
        clear predicted
    end %each temporal sample
    
    %if classification comes from BIOSIG or is KNN/LDA we have to map it back to interval {-1,1}
    if probabilisticClassifier
        %%%%this was commented because is only valid for class label outputs.
%         ypredTrainTime(ypredTrainTime==2)=-1;
%         ypredTestTime(ypredTestTime==2)=-1;
        %%%%no normalization for BIOSIG classifiers
%         if biosigStatClassifier %if is coming from biosig it needs a further mapping from a distance to a probability 
%             ypredTrainTime=exp(ypredTrainTime);
%             ypredTestTime=exp(ypredTestTime);
%             biosigStatClassifier=0;
%         end
        % linear mapping from interval [0,1] to interval [-1,1]
        ypredTrainTime=2*ypredTrainTime-1;
        ypredTestTime=2*ypredTestTime-1;
        probabilisticClassifier=0; %reset boolean value
    end
    
    %j is first dimension index for output array
    ypredTrain(j,:,:)=ypredTrainTime;
    ypredTest(j,:,:)=ypredTestTime;
    
end %each method













