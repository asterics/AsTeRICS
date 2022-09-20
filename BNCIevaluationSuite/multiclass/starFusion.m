function varargout=starFusion(testData, GT, data, fusionOp, decisionThreshold)
%
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% starFusion
% Performs the fusion of streams of several classificator result with an optional 
% training stage.
%
% It fuses several temporal series obtained as outputs of
% some classifiers into one output. If training data and GT are given, it selects for each classification
% result the best fusion rule. In this case the GT is the one of a binary classification with labels +1 and -1
% respectively for the positive and negative classes.
%
% [Example of use in B:\STAR2STAR - Internal\TN Technical Notes (TB+KN)\TN00146 - BCI Classification - OVR aproach]
%----------------------------------------------------------
%Inputs
% testData:	structure with classification results as computed by starClassify - matrix numberClassifiers X samples X trials
% Optional
% GT:	ground truth vector - vector 1 X trials 
%		It is expected to 
%       be of values 1 and -1. If it is provided, the best fusion operator 
%       is chosen for each time sample. 
%       If not, the diferent fusion candidates are provided as output.
% data: training data with the same format than testData, and used to find out 
%       which is the right fusion operator used. Similar to a training set
%       of a classifier. It has to present the same number of trials than GT.
% fusionOp:	cell array of strings with a list of fusionOperators to be applied - {'sum'|'product'|'max'|'min'|'median'|'majorVoting'}
%           In this case, GT and data are not needed, therefore,
%           the function can be called like:
%           fusionPrediction=starFusion(classifyPrediction,[],[],{'max','majorVoting'},0);
% decisionThresold: In case a binary classification is wished this is the threshold used to assign 1 or 0 - double in [-1,1] 
%					Default value is 0
%----------------------------------------------------------
%Outputs
% varargout{1}: Fusion result with two different dimensionalities depending on the fact if the function is called with
% 				training data or not.
%				If training - matrix trials X samples
%				If no training - matrix numberOperators X samples X trials
%               Ther result of fusionOp{j} can be found in (j,:,:)
% varargout{2}: Fusion operator used as an array of its corresponding indices of fusionOp.
%               Vector of 1 X samples
%----------------------------------------------------------
%Dependencies
% fusion_operator_majority_voting: EEGStarlab
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v1		09/12/08    JLL,ASF     First version
% v2        10/11/10    ASF         Update header.
% TODO 		change to varargin in order not to input data and GT in case we do
% 			not want to select the best
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------

if nargin <5
    decisionThreshold=0;
end

if nargin <4
    fusionOp={'sum','product','max','min','median','majorVoting'};
end

if ~iscell(fusionOp)
    fusionOp={fusionOp};
end

if length(fusionOp{1})==3 %condition added in order to avoid error
    if all(fusionOp{1}=='all')
        %%%%%%%%%%fusion operators
        fusionOp={'sum','product','max','min','median','majorVoting'};
    end
end

selectBestOperator=1;
if nargin < 3
    selectBestOperator=0;
    GT=[];
end
if nargin>4
    selectBestOperator=0;
end
if sum(size(GT))<2 %we don't have anything as GT
    selectBestOperator=0;
end
if sum(size(data))<2 %we don't have anything as data
    selectBestOperator=0;
end
GT=GT(:);
if size(data,3)==size(GT,1) 
else
%    display('Training set and Ground Truth dimensions do not seem to match')
    selectBestOperator=0;
end


%%%go through the different fusion operators in order to select the
%%%best
for t=1:size(testData,2)
    if selectBestOperator
        for j=1:length(fusionOp)
            switch fusionOp{j}
                %%diferent fusion operators
                case 'sum'
                    dataFusedCandidate(:,j)=sum(data(:,t,:),1);
                case 'product'
                    dataFusedCandidate(:,j)=prod(data(:,t,:),1);
                case 'max'
                    dataFusedCandidate(:,j)=max(data(:,t,:),[],1);
                case 'min'
                    dataFusedCandidate(:,j)=min(data(:,t,:),[],1);
                case 'median'
                    dataFusedCandidate(:,j)=median(data(:,t,:),1);
                case 'majorVoting'
                    %                 disp('mV')

                    %             %%%class results have to be first converted into class
                    %             %%%labels
                    %             %%%%%%%%%%%%%%%%attention real classifier results are
                    %             %%%%%%%%%%%%%%%%lost from here on
                    dataCand=data(:,t,:);
                    dataCand(dataCand>decisionThreshold)=3;
                    dataCand(dataCand<decisionThreshold)=1;

                    dataCand=permute(dataCand,[3 1 2]);
                    %             %now comes the fusion
                    %             %2 is substracted because we want the label result to
                    %             %be 1 or -1
                    dataFusedCandidate(:,j)=fusion_operator_majority_voting(dataCand)-2;

                otherwise
                    disp([fusionOp{j},' has not been implemented for fusion'])
            end %switch fusion operator
        end % different fusion operators

    
        %now we have to select the rule with maximal times winning (by
        %comparing to the GT)
        %1. transform decision in binary ones
        dataFusedCandidate(dataFusedCandidate>decisionThreshold)=1;
        dataFusedCandidate(dataFusedCandidate<decisionThreshold)=-1;

        %2. compute number of times each operator wins
        numberTimesWin=sum(dataFusedCandidate==repmat(GT,1,size(dataFusedCandidate,2)));
        [aux,winFusion]=max(numberTimesWin);

        %3. assign the result to the final prediction for this time sample
        dataFused(1,:,t)=dataFusedCandidate(:,winFusion);
        usedFusion(t)=winFusion;
        %size(usedFusion) %debugging

        %----------from the result of the previous, now the best rule can
        %be selected:
         switch fusionOp{winFusion}
                %%diferent fusion operators
             case 'sum'
                 testDataFused(:,t)=sum(testData(:,t,:),1);
             case 'product'
                 testDataFused(:,t)=prod(testData(:,t,:),1);
             case 'max'
                 testDataFused(:,t)=max(testData(:,t,:),[],1);
             case 'min'
                 testDataFused(:,t)=min(testData(:,t,:),[],1);
             case 'median'
                 testDataFused(:,t)=median(testData(:,t,:),1);
             case 'majorVoting'

                 testDataMV=testData(:,t,:);
                 testDataMV(testDataMV>decisionThreshold)=3;
                 testDataMV(testDataMV<decisionThreshold)=1;

                 testDataMV=permute(testDataMV,[3 1 2]);
                 %             %now comes the fusion
                 %             %2 is substracted because we want the label result to
                 %             %be 1 or -1
                 testDataFused(:,t)=fusion_operator_majority_voting(testDataMV)-2;

             otherwise
                 disp([fusionOp{winFusion},' has not been implemented for fusion'])
         end %switch fusion opeerator

    else %there is no training set for the fusion procedure---------------
        %dataFused(:,:,t)=dataFusedCandidate;
        for j=1:length(fusionOp)
            switch fusionOp{j}
                %%diferent fusion operators
                case 'sum'
                    testDataFusedCandidate(:,j)=sum(testData(:,t,:),1);
                case 'product'
                    testDataFusedCandidate(:,j)=prod(testData(:,t,:),1);
                case 'max'
                    testDataFusedCandidate(:,j)=max(testData(:,t,:),[],1);
                case 'min'
                    testDataFusedCandidate(:,j)=min(testData(:,t,:),[],1);
                case 'median'
                    testDataFusedCandidate(:,j)=median(testData(:,t,:),1);
                case 'majorVoting'

                    %             %%%class results have to be first converted into class
                    %             %%%labels
                    %             %%%%%%%%%%%%%%%%attention real classifier results are
                    %             %%%%%%%%%%%%%%%%lost from here on
                    testDataFusedCandidate=testData(:,t,:);
                    testDataFusedCandidate(testDataFusedCandidate>decisionThreshold)=3;
                    testDataFusedCandidate(testDataFusedCandidate<decisionThreshold)=1;

                    testDataFusedCandidate=permute(testDataFusedCandidate,[3 1 2]);
                    %             %now comes the fusion
                    %             %2 is substracted because we want the label result to
                    %             %be 1 or -1
                    testDataFusedCandidate(:,j)=fusion_operator_majority_voting(testDataFusedCandidate)-2;

                otherwise
                    disp([fusionOp{j},' has not been implemented for fusion'])
            end %switch fusion operator
            testDataFused(j,t,:)=testDataFusedCandidate(:,j);
        end %for each fusion operator
        
    end %end if training set is provided or not
end %time samples

varargout{1}=testDataFused;
if nargout >1
    varargout{2}=usedFusion;
%     size(usedFusion)
end
end