function [symbolMemberships]=svmRecallAndP300Averages(testData,svmRecallParameters,numberSequencesAverage,spellerMatrixNumberElements)
%----------------------------------------------------------
% EEGStarlab Toolbox
% (C) Starlab S.L.
%----------------------------------------------------------
% svmRecallAndP300Averages
% Performs the recall of a SVM and the mapping into each symbol membership. 
% Function has been adapted from code provided in
% http://asi.insa-rouen.fr/~arakotom/code/bciindex.html. 
% It recalls a SVM on the test data with the parameters specified in the structure svmRecallParameters.  
% The stage includes as well two averaging stages. The first one is done over row/columns
% of the speller matrix. The second one is done over numberSequencesAverage sequences of
% symbol illuminations.
%
% [Description in A. Rakotomamonjy and V. Guigue, "BCI competition III: 
% dataset II- ensemble of SVMs for BCI P300 speller." IEEE transactions on bio-medical engineering, vol. 55, no. 3, pp. 1147-1154, 
% March 2008. [Online]. Available: http://dx.doi.org/10.1109/TBME.2008.915728 ]
%
% REQUIREMENTS:
%   - TestWordAsterics
%----------------------------------------------------------
%Inputs
% testData:	structure with data set signals and ground truth
%       signals: EEG data in Starlab Data Cube format - matrix channels X
%                samples X epochs  (e.g. in the paper 64 x 14 x NE)
%       GT:      ground truth for each epoch. In case of p300 data we have agreed the following
%                GT structure:
%                - First column: 0 means unattended stimuli, 1 means attended stimuli
%                - Second Column: Linear code (k) of the image shown in presentation protocol.
%                This maps into a row (i) or column (j) index through the expression:
%                   i=k if k<=R for rows, and j=R+k for columns, where R is the number of  
%                   rows and being rows indexed from left to right and columns from top to bottom.
%                - Third Column: Integer code of the attended stimuli (character/image/icon) for the duration of
%                each run. This is the code of what we are trying to detect through the
%                p300 wave analysis.
%               Data type - matrix epochs x 3 ()
%
% svmRecallParameters: Structure with the parameters to be used in the recall of the SVM
%                      classifier. It includes following fields:
%       channels: Array of the M channels to be used - row vector 1 x M (int)
%       suportVectors: Matrix of support vectors (used by svmval) -
%       weights: Weighting matrix (used by svmval) -
%       bias: Bias vector (used by svmval) - 
%       kernelType: Kernel type of the SVM (used by svmval) - str
%       kerneloption: Kernel option (used by svmval) - int
%
% numberSequencesAverage: Number of illumination sequences of each symbol to average - int. In
%                         the paper this has been set up to 5 or 15.
%
% spellerMatrixNumberElements: Number of elements of the speller matrix - int
%----------------------------------------------------------
%Outputs
% symbolMemberships: Matrix of memberships to each symbol. The estimation is done for
%                    NE/numberSequencesAverage test elements - matrix
%                    spellerMatrixNumberElements X
%                    NE/numberSequencesAverage X numberOfAttendedStimuly
%----------------------------------------------------------
%Dependencies
% 
%----------------------------------------------------------
% Version	Date		Author		Changes 
% v1		20/12/10    ASF         First version only interface
%----------------------------------------------------------
% EX.
% 
%--------------------------------------------------------------
filet = eegstarlab2RakotoTrain(testData);
classifier = svmRecallParameters;
intwarning off
sideSpellerMatrix = int16(sqrt(spellerMatrixNumberElements));
intwarning on
span=1;
%matrix = ['ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890'];
matrix = ['ADG';'BEH';'CFI'];
%matrix = ['ABC';'DEF';'GHI'];
%nbilluminationperletter=size(testData(1).GT,1);
nbilluminationperletter=90;
nbshotvec = [1:(nbilluminationperletter/spellerMatrixNumberElements-numberSequencesAverage)];
VoteMatrix=cell(1,length(nbshotvec));
nberror=zeros(1,length(nbshotvec));
nbclassifier=length(classifier);
kword=1;
nbcharacter=0;
resultlettre=[];
data.normalizationtype='normal';
data.typedata='allfilt';
data.highcutofffrequency=20;
% we see how many shots/epochs we have
% and how many 'groups' we'll have regarding
% how many we want to use to average

% iterate throught all the epochs/targets we have
% maximum number of lettres / attended stimuly per run
maxlettre = 2;
symbolMemberships = zeros(spellerMatrixNumberElements,length(nbshotvec),maxlettre,nbclassifier);
for i=1:length(filet)
    xt=[];
    yt=[];
    yptestall=0;
    x = filet{i}.x;
    code = filet{i}.code;
    target = filet{i}.target;
    triallength = filet{i}.triallength;
    nblettre=size(x,1)/nbilluminationperletter;
    vote=zeros(sideSpellerMatrix,sideSpellerMatrix,nblettre,length(nbshotvec));
    outputVote=zeros(spellerMatrixNumberElements,length(nbshotvec),nblettre);
    xt2=zeros(size(x,1),nbclassifier);
    % iterate through the classifiers we have
    for ii=1:nbclassifier
        xt=[x];
        xsup=classifier(ii).xsup;
        w=classifier(ii).w;
        b=classifier(ii).b;
        mnormalize=classifier(ii).mnormalize;
        stdnormalize=classifier(ii).stdnormalize;
        channel=classifier(ii).channel;
        lengthperchannel=classifier(ii).lengthperchannel;
        xt=KeepChannel(xt,channel,lengthperchannel);
        [aux,xt]=normalize([],xt,[],[],channel,triallength,mnormalize,stdnormalize,data.normalizationtype);
        if ~isfield(classifier,'kernel')  | ~isfield(classifier,'kerneloption')
            kernel='poly';
            kerneloption=1;
        else
            kernel=classifier(ii).kernel;
            kerneloption=classifier(ii).kerneloption;
        end;
        
        yptest=svmval(xt,xsup,w,b,kernel,kerneloption,span);
        yptestall=yptestall+yptest;
        kk=1;
        for nbshot=nbshotvec
            [wordtest{ii,kk} votemat]=TestWordAsterics(yptest,code,nbshot,'sum',numberSequencesAverage);
            fprintf('%s\t',wordtest{ii,kk});
            for jj=1:length(wordtest{ii,kk});    
                [indlig,indcol]=find(matrix==wordtest{ii,kk}(jj));   
                vote(indlig,indcol,jj,kk)= vote(indlig,indcol,jj,kk) +1;
                fooMat = reshape(votemat(jj,:,:),3,3);
                symbolMemberships(:,nbshot,jj,ii) = reshape(fooMat',spellerMatrixNumberElements,1);
            end;
            kk=kk+1;
        end;
        fprintf('\n');
    end;
    %----------------------------------------------------------------
    fprintf('-----------------------------------------------\n');
    for kk=1:length(nbshotvec)
        wordvote=''   ; 
        for jj=1:nblettre
            [aux,indmaxcol]=max(max(vote(:,:,jj,kk)));     
            [aux,indmaxlig]=max(max(vote(:,:,jj,kk)'));  
            wordvote=strcat(wordvote,matrix(indmaxlig,indmaxcol));
        end;
        
        fprintf('%s\t',wordvote);
        
    end;
    fprintf('\n-----------------------------------------------\n');
    
    
    %----------------------------------------------------------------
    % sum 
    %----------------------------------------------------------------
    %     if dostage2
    %         
    %         yptestall=svmval(xt2,xsupMerged,wMerged,bMerged,kernelM,kerneloptionM,span);
    %         keyboard
    %     end;
    
    
    fprintf('-----------------------------------------------\n');
    iternbshot=1;
    kkk=1;
    for nbshot=nbshotvec
        wordsum=''   ; 
        [wordsum]=TestWordAsterics(yptestall,code,nbshot,'sum',numberSequencesAverage);
        fprintf('%s\t',wordsum);
        nberror(iternbshot)=nberror(iternbshot)+sum(char(target')~=wordsum);
        iternbshot=iternbshot+1;
        wordmat(kkk,:)=wordsum;
        kkk=kkk+1;
    end;
    fprintf('| \t%s\t',target);
    fprintf('\n-----------------------------------------------\n');
    resultlettre=[resultlettre wordmat];
    kword=kword+1;
    nbcharacter=nbcharacter+length(target);
end;