function [resultlettre,perf]=TestClassifier(fileclassifier,filet,DirInfo,data,nbshotvec)
% USAGE
%
% resultlettre=TestClassifier(fileclassifier,filet,DirInfo,data,nbshotvec)
%
% fileclassifier    filename in with the classifier has been save
% filet             a cell containing a list of the data filename to be
%                   tested
%
% DirInfo           path of the data
% data              parameters on how the data has been preprocessed. These
%                   parameters are related to the data filename
%                   data.normalizationtype='normal';
%                   data.typedata='allfilt';
%                   data.highcutofffrequency=20;
%
% nbshotvec         vector of nb of sequences to be used for classification
%
%
% OUTPUT
%
% resultlettre      matrix with nbshotvec rows containing the predicted
%                   spelled letter

span=1;
matrix=['ABCDEF';'GHIJKL';'MNOPQR';'STUVWX';'YZ1234';'56789_'];
nbilluminationperletter=180;

VoteMatrix=cell(1,length(nbshotvec));
nberror=zeros(1,length(nbshotvec));
fprintf('\n');
load(fileclassifier)
nbclassifier=length(classifier);
kword=1;
nbcharacter=0;
resultlettre=[];
for filen=filet
    xt=[];
    yt=[];
    yptestall=0;
    filen=[DirInfo.pathdata char(filen) '-' data.typedata int2str(data.highcutofffrequency) '.mat']; % DATA FILE
    load(filen)
    
    nblettre=size(x,1)/nbilluminationperletter;
    vote=zeros(6,6,nblettre,length(nbshotvec));
    xt2=zeros(size(x,1),nbclassifier);
    %---------------------------------------------
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
       %              AUC=svmroccurve(sign(yptest),y)
        yptestall=yptestall+yptest;
        %         if dostage2
        %             xt2(:,ii)=yptest;    
        %         end;
        %
        % word result for a single classifier
        %
        kk=1;
        for nbshot=nbshotvec
            [wordtest{ii,kk} votemat]=TestWord(yptest,code,nbshot,'sum');
            fprintf('%s\t',wordtest{ii,kk});
            for jj=1:length(wordtest{ii,kk});    
                [indlig,indcol]=find(matrix==wordtest{ii,kk}(jj));   
                vote(indlig,indcol,jj,kk)= vote(indlig,indcol,jj,kk) +1;
                
            end;
            kk=kk+1;
        end;
        fprintf('\n');
    end; 
  %  keyboard
   %  AUCall=svmroccurve(sign(yptestall),y)
    %----------------------------------------------------------------
    % vote 
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
        [wordsum]=TestWord(yptestall,code,nbshot,'sum');
        fprintf('%s\t',wordsum);
        nberror(iternbshot)=nberror(iternbshot)+sum(char(target)~=wordsum);
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

if nbcharacter~=0
    % Affichage Resultats si la lettre cible est connu
    for i=1:length(nbshotvec)
        fprintf('%d \t\t',nberror(i))
    end;
    fprintf('Number of errors : \n');
    for i=1:length(nbshotvec)
        fprintf('%2.2f \t',nberror(i)/nbcharacter)
        perf(i)=1-nberror(i)/nbcharacter;
    end;
    
end,

