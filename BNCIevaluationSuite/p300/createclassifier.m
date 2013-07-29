function createclassifier(DirInfo,File,data,channelselection,chanselparamclass)

% USAGE
%
%  createclassifier(DirInfo,File,data,channelselection,chanselparamclass)
%
%  function for learning a set of classifiers associated to the data given
%  in File.
%
% DirInfo               string containing the path of the model selection file.
% File                  File.app a cell containing the list of data to be used for
%                       learning
%                       File.save  filename in which the classifiers are
%                       saved.
% channelselection      binary value if channelselection is performed (1)
%                       or not (0) 
%
% channelparamclass     channelselection parameters (those used in
%                       modelclassifier function)
%  chanselparam.channelchoice 'optimal' use the channels that maximize the channel sel criterion 
%                       or 'adhoc' predefined number of ranked channel to be
%                
%  chanselparam.nbchannel   number of channel to select if 'adhoc
%  chanselparam.criterion criterion for channel selection 'tp'


kerneloption=[];
kernel=[];
lambda=1e-8;
verbose=1;
span=1;
classcode=[1 -1];
mnormalize=0;
stdnormalize=0;
k=1;
for filen=File.app
    filedata=[DirInfo.pathdata char(filen) '-' data.typedata int2str(data.highcutofffrequency) '.mat'];
    
    if channelselection
        filecv=[DirInfo.pathmodelsel 'CV-VS-' char(filen) '-crit' chanselparamclass.criterion '-norm_' data.normalizationtype ...
                '-NbChan' int2str(chanselparamclass.NbChanMax) '-Fc' int2str(data.highcutofffrequency)];
    else
        filecv=[DirInfo.pathmodelsel 'CV-' char(filen) '-' data.typedata '-norm_' data.normalizationtype];
    end
    
    xa=[];
    ya=[];
    load(filedata)
    xa=[x];  
    ya=[y];
    load(filecv);
    
    if channelselection
        switch chanselparamclass.channelchoice
            case 'optimal'
                [aux,indmax]=max(value);
                restchannel=restchannel(2:end);
                channel=[channel restchannel(1:(indmax)*chanselparamclass.RemoveChan)];
            case 'adhoc'
                restchannel=restchannel(2:end);
                channel=[channel restchannel(1:chanselparamclass.nbchannel-length(channel))];
            case 'all'
                channel=[channel restchannel];
        end;
    end;
    if isempty(kernel) 
        kernel='poly';
    end;
    
    
    
    xa=KeepChannel(xa,channel,lengthperchannel);
    [xa,aux,mnormalize,stdnormalize]=normalize(xa,[],y,[],channel,triallength,[],[],data.normalizationtype) ;
    if size(mnormalize,2)~=size(xa,2)
        keyboard    
    end;
    [xsup,w,b]=svmclass(xa,ya,C,lambda,kernel,kerneloption,verbose,span);

    classifier(k).xsup=xsup;
    classifier(k).w=w;
    classifier(k).b=b;
    classifier(k).mnormalize=mnormalize;
    classifier(k).stdnormalize=stdnormalize;
    classifier(k).typedata=data.typedata;
    classifier(k).filename=filen;
    classifier(k).channel=channel;
    classifier(k).lengthperchannel=lengthperchannel;
    classifier(k).kernel=kernel;
    classifier(k).kerneloption=kerneloption;
    k=k+1;
end;


% %
% %   Learn the second stage classifier using the 2stage dataset
% %
% if dostage2 & isfield(File,'stage2') 
%     if ~isempty(File.stage2)
%         yt2=[];
%         xaux=[];
%         for i=1:length(File.stage2)
%             filen=[DirInfo.pathdata char(File.stage2{i}) '-' data.typedata int2str(data.highcutofffrequency) '.mat']; % DATA FILE
%             load(filen);
%             xaux=[xaux;x];
%             yt2=[yt2;y];
%         end;
%         nbclassifier=length(classifier);
%         for ii=1:nbclassifier   
%             xt=xaux;
%             xsup=classifier(ii).xsup;
%             w=classifier(ii).w;
%             b=classifier(ii).b;
%             mnormalize=classifier(ii).mnormalize;
%             stdnormalize=classifier(ii).stdnormalize;
%             channel=classifier(ii).channel;
%             lengthperchannel=classifier(ii).lengthperchannel;
%             xt=KeepChannel(xt,channel,lengthperchannel);
%             [aux,xt]=normalize([],xt,[],[],channel,triallength,mnormalize,stdnormalize,normalizationtype);
%             xt2(:,ii)=svmval(xt,xsup,w,b,kernel,kerneloption,span);
%         end;
%         Cmerged=10;
%         kernelM='poly';
%         kerneloptionM=1;
%         [xsupMerged,wMerged,bMerged]=svmclassLS(xt2,yt2,Cmerged,lambda,kernelM,kerneloptionM,verbose,span);
%         temp=svmval(xt2,xsupMerged,wMerged,bMerged,kernelM,kerneloptionM,span);mean(yt2==sign(temp))
%        keyboard
%         
%         
%     end;
% end;


clear xa xsup mnormalize stdnormalize w b xapp yapp xt2 yt2 dostage2 xt xaux
save(File.save);