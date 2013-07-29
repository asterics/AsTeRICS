function varargout=starFusionMultiClass(testData, GT, data, fusionOp, decisionThreshold)
%function that performs the fusion of streams of several binary class classificators
%
%this function fuses several temporal series obtained as outputs of
%some classifiers into one output, by selecting for each classification
%result the best fusion rule.
%
%Output-------------------------------------------------------------------
% The output is a data structure of trials X samples in case the fusion
% rule is selected for each time sample. Here the additional output 
% gives the fusion rule employed at each stage.
% 
% In the case the Ground Truth is not provided, the output is of the form
% Number of Operators X samples X trials
%
% Optional Output:
%   the list of the winner operators for each time sample
%
%
%Input-------------------------------------------------------------------
% The input is testData, with dimensions classifiers X samples X trials.
% Optional inputs are:
%       GT, a ground truth vector of size trials. It is expected to 
%       be of values 1 and -1. If it is provided, the best fusion operator 
%       is chosen for each time sample.
%       if not, the diferent fusion candidates are provided as output.
%
%       data, with the same format than testData, and used to find out 
%       which is the right fusion operator used. Similar to a training set
%       of a classifier. It has to be the same size than GT.
%
%
%       fusionOp, which is a cell with a list of fusionOperators,
%       which can be: 'sum','product','max','min','median','majorVoting'
%           In this case, GT and data are not needed, therefore,
%           the function can be called like:
%           fusionPrediction=starFusion(classifyPrediction,[],[],{'max','major
%           Voting'},0);
%   
%
%
%       decisionThresold, to assign to 1 class or another. Default value is
%       0
%
% DEPENDENCIES:------------------------------------------------------
% for the majority voting fusion, fusion_operator_majority_voting.m is used
% TODO change to varargin in order not to input data and GT in case we do
% not want to select the best



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
                    dataFusedCandidate(:,:,j)=sum(data(:,t,:,:),1);
                case 'product'
                    dataFusedCandidate(:,:,j)=prod(data(:,t,:,:),1);
                case 'max'
                    dataFusedCandidate(:,:,j)=max(data(:,t,:,:),[],1);
                case 'min'
                    dataFusedCandidate(:,:,j)=min(data(:,t,:,:),[],1);
                case 'median'
                    dataFusedCandidate(:,:,j)=median(data(:,t,:,:),1);
                case 'majorVoting'
                    %                 disp('mV')

                    %             %%%class results have to be first converted into class
                    %             %%%labels
                    %             %%%%%%%%%%%%%%%%attention real classifier results are
                    %             %%%%%%%%%%%%%%%%lost from here on

      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%WE ARE HERE
      %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%!!!!

                    [c,indMax]=max(data(:,t,:,:),[],4); %compute win class for each classifier
                    dataCand=permute(indMax,[3,1,2]); 
                    tmp=fusion_operator_majority_voting(dataCand); %fuse with MV
                    indMV=cleanRandomMV(tmp); %clean ties, which are characterize by negative results
                    dataFusedCandidate(:,:,j)=zeros(size(dataFusedCandidate(:,:,j-1),1),size(dataFusedCandidate(:,:,j-1),2));
                    dataFusedCandidate(:,indMV,j)=1; %put 1 in the winning class
                    clear tmp
                    clear indMV
                    clear indMax
                otherwise
                    disp([fusionOp{j},' has not been implemented for fusion'])
            end %switch fusion operator
        end % different fusion operators

    
        %now we have to select the rule with maximal times winning (by
        %comparing to the GT)
        %1. transform decision into labels
        [c,dataFusedCandidate]=max(dataFusedCandidate,[],2);
        dataFusedCandidate=permute(dataFusedCandidate,[1,3,2]);
        
%         dataFusedCandidate(dataFusedCandidate>decisionThreshold)=1;
%         dataFusedCandidate(dataFusedCandidate<decisionThreshold)=-1;

        %2. compute number of times each operator wins
        numberTimesWin=sum(dataFusedCandidate==repmat(GT,1,size(dataFusedCandidate,2)));
        [aux,winFusion]=max(numberTimesWin);

        %3. assign the result to the final prediction for this time sample
        %dataFused(1,:,t)=dataFusedCandidate(:,winFusion);
        usedFusion(t)=winFusion;
        %size(usedFusion) %debugging
        clear dataFusedCandidate
        %----------from the result of the previous, now the best rule can
        %be selected:
         switch fusionOp{winFusion}
                %%diferent fusion operators
             case 'sum'
                 testDataFused(:,t,:)=sum(testData(:,t,:,:),1);
             case 'product'
                 testDataFused(:,t,:)=prod(testData(:,t,:,:),1);
             case 'max'
                 testDataFused(:,t,:)=max(testData(:,t,:,:),[],1);
             case 'min'
                 testDataFused(:,t,:)=min(testData(:,t,:,:),[],1);
             case 'median'
                 testDataFused(:,t,:)=median(testData(:,t,:,:),1);
             case 'majorVoting'

%                  testDataMV=testData(:,t,:);
%                  testDataMV(testDataMV>decisionThreshold)=3;
%                  testDataMV(testDataMV<decisionThreshold)=1;
% 
%                  testDataMV=permute(testDataMV,[3 1 2]);
%                  %             %now comes the fusion
%                  %             %2 is substracted because we want the label result to
%                  %             %be 1 or -1
%                  testDataFused(:,t)=fusion_operator_majority_voting(testDataMV)-2;
                 
                 
                 [c,indMax]=max(testData(:,t,:,:),[],4); %compute win class for each classifier
                 aux1=permute(indMax,[3,1,2]); 
                 tmp=fusion_operator_majority_voting(aux1) %fuse with MV
                 indMV=cleanRandomMV(tmp); %clean ties, which are characterize by negative results
                 testDataFused(:,t,:)=zeros(size(testDataFused(:,:,j-1),1),1,size(testDataFused(:,:,j-1),3))-1;
                 testDataFused(:,t,indMV)=testData(:,t,:,indMV); %put 1 in the winning class
             otherwise
                 disp([fusionOp{winFusion},' has not been implemented for fusion'])
         end %switch fusion opeerator

%%%%%%%%%%%%%%%%%%%%%%%%%%multi-class is not implemented with the option of
%%%%%%%%%%%%%%%%%%%%%%%%%%no training fusion
%     else %there is no training set for the fusion procedure---------------
%         %dataFused(:,:,t)=dataFusedCandidate;
%         for j=1:length(fusionOp)
%             switch fusionOp{j}
%                 %%diferent fusion operators
%                 case 'sum'
%                     testDataFusedCandidate(:,j,:)=sum(testData(:,t,:,:),1);
%                 case 'product'
%                     testDataFusedCandidate(:,j)=prod(testData(:,t,:),1);
%                 case 'max'
%                     testDataFusedCandidate(:,j)=max(testData(:,t,:),[],1);
%                 case 'min'
%                     testDataFusedCandidate(:,j)=min(testData(:,t,:),[],1);
%                 case 'median'
%                     testDataFusedCandidate(:,j)=median(testData(:,t,:),1);
%                 case 'majorVoting'
% 
%                     %             %%%class results have to be first converted into class
%                     %             %%%labels
%                     %             %%%%%%%%%%%%%%%%attention real classifier results are
%                     %             %%%%%%%%%%%%%%%%lost from here on
%                     testDataFusedCandidate=testData(:,t,:);
%                     testDataFusedCandidate(testDataFusedCandidate>decisionThreshold)=3;
%                     testDataFusedCandidate(testDataFusedCandidate<decisionThreshold)=1;
% 
%                     testDataFusedCandidate=permute(testDataFusedCandidate,[3 1 2]);
%                     %             %now comes the fusion
%                     %             %2 is substracted because we want the label result to
%                     %             %be 1 or -1
%                     testDataFusedCandidate(:,j)=fusion_operator_majority_voting(testDataFusedCandidate)-2;
% 
%                 otherwise
%                     disp([fusionOp{j},' has not been implemented for fusion'])
%             end %switch fusion operator
%             testDataFused(j,t,:)=testDataFusedCandidate(:,j);
%         end %for each fusion operator
        
    end %end if training set is provided or not
end %time samples

varargout{1}=testDataFused;
if nargout >1
    varargout{2}=usedFusion;
%     size(usedFusion)
end
end